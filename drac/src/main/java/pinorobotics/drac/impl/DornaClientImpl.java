/*
 * Copyright 2024 drac project
 * 
 * Website: https://github.com/pinorobotics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pinorobotics.drac.impl;

import id.xfunction.Preconditions;
import id.xfunction.function.Unchecked;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import pinorobotics.drac.CommandType;
import pinorobotics.drac.DornaClient;
import pinorobotics.drac.DornaClientConfig;
import pinorobotics.drac.DornaRobotModel;
import pinorobotics.drac.Joints;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.messages.Motion;
import pinorobotics.drac.metrics.DracMetrics;

/**
 * Client to Dorna Command Server
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClientImpl extends IdempotentService implements DornaClient {
    private static final XLogger LOGGER = XLogger.getLogger(DornaClientImpl.class);
    private static final Meter METER =
            GlobalOpenTelemetry.getMeter(DornaClientImpl.class.getSimpleName());
    private static final LongCounter VERSION_COUNT_METER =
            METER.counterBuilder(DracMetrics.VERSION_COUNT_METRIC)
                    .setDescription(DracMetrics.VERSION_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongCounter VERSION_FAILED_COUNT_METER =
            METER.counterBuilder(DracMetrics.VERSION_FAILED_COUNT_METRIC)
                    .setDescription(DracMetrics.VERSION_FAILED_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongHistogram VERSION_TIME_METER =
            METER.histogramBuilder(DracMetrics.VERSION_TIME_METRIC)
                    .setDescription(DracMetrics.VERSION_TIME_METRIC_DESCRIPTION)
                    .ofLongs()
                    .build();
    private static final LongCounter JOINT_COUNT_METER =
            METER.counterBuilder(DracMetrics.JOINT_COUNT_METRIC)
                    .setDescription(DracMetrics.JOINT_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongCounter JOINT_FAILED_COUNT_METER =
            METER.counterBuilder(DracMetrics.JOINT_FAILED_COUNT_METRIC)
                    .setDescription(DracMetrics.JOINT_FAILED_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongHistogram JOINT_TIME_METER =
            METER.histogramBuilder(DracMetrics.JOINT_TIME_METRIC)
                    .setDescription(DracMetrics.JOINT_TIME_METRIC_DESCRIPTION)
                    .ofLongs()
                    .build();
    private static final LongCounter JMOVE_COUNT_METER =
            METER.counterBuilder(DracMetrics.JMOVE_COUNT_METRIC)
                    .setDescription(DracMetrics.JMOVE_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongCounter JMOVE_FAILED_COUNT_METER =
            METER.counterBuilder(DracMetrics.JMOVE_FAILED_COUNT_METRIC)
                    .setDescription(DracMetrics.JMOVE_FAILED_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongHistogram JMOVE_TIME_METER =
            METER.histogramBuilder(DracMetrics.JMOVE_TIME_METRIC)
                    .setDescription(DracMetrics.JMOVE_TIME_METRIC_DESCRIPTION)
                    .ofLongs()
                    .build();
    private static final LongCounter MOTOR_COUNT_METER =
            METER.counterBuilder(DracMetrics.MOTOR_COUNT_METRIC)
                    .setDescription(DracMetrics.MOTOR_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongCounter MOTOR_FAILED_COUNT_METER =
            METER.counterBuilder(DracMetrics.MOTOR_FAILED_COUNT_METRIC)
                    .setDescription(DracMetrics.MOTOR_FAILED_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongHistogram MOTOR_TIME_METER =
            METER.histogramBuilder(DracMetrics.MOTOR_TIME_METRIC)
                    .setDescription(DracMetrics.MOTOR_TIME_METRIC_DESCRIPTION)
                    .ofLongs()
                    .build();
    private static final LongCounter PLAY_COUNT_METER =
            METER.counterBuilder(DracMetrics.PLAY_COUNT_METRIC)
                    .setDescription(DracMetrics.PLAY_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongCounter PLAY_FAILED_COUNT_METER =
            METER.counterBuilder(DracMetrics.PLAY_FAILED_COUNT_METRIC)
                    .setDescription(DracMetrics.PLAY_FAILED_COUNT_METRIC_DESCRIPTION)
                    .build();
    private static final LongHistogram PLAY_TIME_METER =
            METER.histogramBuilder(DracMetrics.PLAY_TIME_METRIC)
                    .setDescription(DracMetrics.PLAY_TIME_METRIC_DESCRIPTION)
                    .ofLongs()
                    .build();

    private MessageProcessor messageProc = new MessageProcessor();
    private IdGenerator idGenerator = new IdGenerator();
    private DornaClientConfig dornaClientConfig;
    private DracSocket webSocket;
    private DracSocketFactory socketFactory;

    public DornaClientImpl(DornaClientConfig dornaClientConfig) {
        this(dornaClientConfig, new DracSocketFactory());
    }

    public DornaClientImpl(DornaClientConfig dornaClientConfig, DracSocketFactory socketFactory) {
        this.dornaClientConfig = dornaClientConfig;
        this.socketFactory = socketFactory;
    }

    @Override
    public Motion getLastMotion() {
        start();
        return messageProc.getLastMotion();
    }

    @Override
    public int version() throws DornaClientException {
        start();
        var startAt = Instant.now();
        LOGGER.info("Call version command");
        var future = messageProc.await(CommandType.VERSION);
        webSocket.request(1);
        var command = """
                {"cmd":"%s"}""".formatted(CommandType.VERSION);
        VERSION_COUNT_METER.add(1);
        webSocket.sendText(command);
        try {
            return future.get().get("version", Integer.class);
        } catch (InterruptedException | ExecutionException e) {
            VERSION_FAILED_COUNT_METER.add(1);
            throw new DornaClientException(e);
        } finally {
            VERSION_TIME_METER.record(Duration.between(startAt, Instant.now()).toMillis());
        }
    }

    @Override
    public void joint(Joints joints) throws DornaClientException {
        verifyLimits(joints);
        start();
        var startAt = Instant.now();
        LOGGER.info("Call joint command");
        var id = idGenerator.nextId();
        var future = messageProc.awaitResult(id);
        webSocket.request(1);
        var command =
                """
                {"cmd":"%s","id":%d,"j0":%f,"j1":%f,"j2":%f,"j3":%f,"j4":%f,"j5":%f,"j6":%f,"j7":%f}"""
                        .formatted(
                                CommandType.JOINT,
                                id,
                                joints.j0(),
                                joints.j1(),
                                joints.j2(),
                                joints.j3(),
                                joints.j4(),
                                joints.j5(),
                                joints.j6(),
                                joints.j7());
        JOINT_COUNT_METER.add(1);
        webSocket.sendText(command);
        try {
            Preconditions.equals(joints, future.get().joints());
        } catch (InterruptedException | ExecutionException e) {
            JOINT_FAILED_COUNT_METER.add(1);
            throw new DornaClientException(e);
        } finally {
            JOINT_TIME_METER.record(Duration.between(startAt, Instant.now()).toMillis());
        }
    }

    private void verifyLimits(Joints joints) {
        var lower = model().lowerLimit();
        var upper = model().upperLimit();
        var actual = joints.toArray();
        for (int i = 0; i < lower.length; i++) {
            Preconditions.isTrue(
                    lower[i] <= actual[i] && actual[i] <= upper[i],
                    "Joint %d is out of limits: actual %f, limit [%f, %f]",
                    i,
                    actual[i],
                    lower[i],
                    upper[i]);
        }
    }

    @Override
    public void jmove(
            Joints joints,
            boolean isRelative,
            boolean isAsync,
            boolean isContinuous,
            double velocity,
            double acceleration,
            double jerk) {
        verifyLimits(joints);
        start();
        var startAt = Instant.now();
        LOGGER.info("Call jmove command");
        var id = isAsync ? -1 : idGenerator.nextId();
        var future =
                isAsync ? CompletableFuture.completedFuture(null) : messageProc.awaitCompletion(id);
        webSocket.request(1);
        var command =
                """
                {"cmd":"%s"%s,"j0":%f,"j1":%f,"j2":%f,"j3":%f,"j4":%f,"j5":%f,"j6":%f,"j7":%f,"rel":%d,"vel":%f,"accel":%f,"jerk":%f,"cont":%d}"""
                        .formatted(
                                CommandType.JMOVE,
                                isAsync ? "" : ",\"id\":" + id,
                                joints.j0(),
                                joints.j1(),
                                joints.j2(),
                                joints.j3(),
                                joints.j4(),
                                joints.j5(),
                                joints.j6(),
                                joints.j7(),
                                isRelative ? 1 : 0,
                                velocity,
                                acceleration,
                                jerk,
                                isContinuous ? 1 : 0);
        JMOVE_COUNT_METER.add(1);
        webSocket.sendText(command);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            JMOVE_FAILED_COUNT_METER.add(1);
            throw new DornaClientException(e);
        } finally {
            JMOVE_TIME_METER.record(Duration.between(startAt, Instant.now()).toMillis());
        }
    }

    @Override
    public void motor(boolean isOn) throws DornaClientException {
        start();
        LOGGER.info("Call motor command with isOn {0}", isOn);
        var startAt = Instant.now();
        if (!isOn) {
            if (Joints.EUCLID_DISTANCE_COMPARATOR.compare(getLastMotion().joints(), model().home())
                    > 5) {
                if (dornaClientConfig.confirmMotorTurnOff()) {
                    LOGGER.warning(
                            """


                            Received request to turn off the motor. Turning off motor can potentially cause robot arm to fall [Dorna Robot User Manual (Last update on Aug 30, 2023): Dorna Lab: Motors]
                            To disable this warning see DornaClientConfig.

                            If it is safe to proceed please press Enter.
                            Otherwise please move robotic arm into a safe position first and then press Enter.
                            To move robotic arm to home position automatically, type "h" and press Enter.
                            """);
                    Unchecked.run(
                            () -> {
                                var key = System.in.read();
                                if (key == -1) {
                                    // if stream is closed then wait indefinitely
                                    XThread.sleep(Long.MAX_VALUE);
                                } else if (key == 'h') {
                                    home();
                                }
                            });
                }
            }
        }

        var id = idGenerator.nextId();
        var val = isOn ? 1 : 0;
        var future = messageProc.awaitResult(id);
        webSocket.request(1);
        var command =
                """
                {"cmd":"%s","id":%d,"motor":%d}"""
                        .formatted(CommandType.MOTOR, id, val);
        MOTOR_COUNT_METER.add(1);
        webSocket.sendText(command);

        try {
            Preconditions.equals(val, future.get().get("motor", Double.class).intValue());
        } catch (InterruptedException | ExecutionException e) {
            MOTOR_FAILED_COUNT_METER.add(1);
            throw new DornaClientException(e);
        } finally {
            MOTOR_TIME_METER.record(Duration.between(startAt, Instant.now()).toMillis());
        }
    }

    @Override
    protected void onClose() {
        LOGGER.info("Closing connection to {0}", dornaClientConfig.dornaUrl());
        webSocket.sendClose();
    }

    @Override
    protected void onStart() {
        LOGGER.info("Opening connection to {0}", dornaClientConfig.dornaUrl());
        webSocket =
                socketFactory.create(
                        dornaClientConfig.dornaUrl(), messageProc, dornaClientConfig.outputLog());
    }

    @Override
    public void jmove(Joints joints, boolean isRelative) throws DornaClientException {
        jmove(
                joints,
                isRelative,
                false,
                false,
                dornaClientConfig.velocity(),
                dornaClientConfig.acceleration(),
                dornaClientConfig.jerk());
    }

    @Override
    public void play(List<String> script) throws DornaClientException {
        start();
        var startAt = Instant.now();
        LOGGER.info("Call play command");
        PLAY_COUNT_METER.add(1);
        for (var messageJson : script) {
            var id = idGenerator.nextId();
            messageJson = MessageUtils.setId(messageJson, id);
            var future = messageProc.awaitCompletion(id);
            webSocket.request(1);
            webSocket.sendText(messageJson);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                PLAY_FAILED_COUNT_METER.add(1);
                throw new DornaClientException(e);
            }
        }
        PLAY_TIME_METER.record(Duration.between(startAt, Instant.now()).toMillis());
    }

    @Override
    public DornaRobotModel model() {
        return dornaClientConfig.model();
    }
}
