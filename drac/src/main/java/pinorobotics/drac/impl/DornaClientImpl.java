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
import java.util.List;
import java.util.concurrent.ExecutionException;
import pinorobotics.drac.CommandType;
import pinorobotics.drac.DornaClient;
import pinorobotics.drac.DornaClientConfig;
import pinorobotics.drac.DornaRobotModel;
import pinorobotics.drac.Joints;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.messages.Motion;

/**
 * Client to Dorna Command Server
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClientImpl extends IdempotentService implements DornaClient {
    private static final XLogger LOGGER = XLogger.getLogger(DornaClientImpl.class);

    private MessageProcessor messageProc = new MessageProcessor();
    private IdGenerator idGenerator = new IdGenerator();
    private double velocity = DEFAULT_VELOCITY;
    private double acceleration = DEFAULT_ACCEL;
    private double jerk = DEFAULT_JERK;
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
        LOGGER.info("Call version command");
        var future = messageProc.await(CommandType.VERSION);
        webSocket.request(1);
        var command = """
                {"cmd":"%s"}""".formatted(CommandType.VERSION);
        webSocket.sendText(command);
        try {
            return future.get().get("version", Integer.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    @Override
    public void joint(Joints joints) throws DornaClientException {
        start();
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
        webSocket.sendText(command);
        try {
            Preconditions.equals(joints, future.get().joints());
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    @Override
    public void jmove(
            Joints joints, boolean isRelative, double velocity, double acceleration, double jerk) {
        start();
        LOGGER.info("Call jmove command");
        var id = idGenerator.nextId();
        var future = messageProc.awaitCompletion(id);
        webSocket.request(1);
        var command =
                """
                {"cmd":"%s","id":%d,"j0":%f,"j1":%f,"j2":%f,"j3":%f,"j4":%f,"j5":%f,"j6":%f,"j7":%f,"rel":%d,"vel":%f,"accel":%f,"jerk":%f}"""
                        .formatted(
                                CommandType.JMOVE,
                                id,
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
                                jerk);
        webSocket.sendText(command);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    @Override
    public void motor(boolean isOn) throws DornaClientException {
        start();
        LOGGER.info("Call motor command with isOn {0}", isOn);

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
        webSocket.sendText(command);

        try {
            Preconditions.equals(val, future.get().get("motor", Double.class).intValue());
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
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
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    @Override
    public void setAcceleration(double accel) {
        this.acceleration = accel;
    }

    @Override
    public void setJerk(double jerk) {
        this.jerk = jerk;
    }

    @Override
    public void jmove(Joints joints, boolean isRelative) throws DornaClientException {
        jmove(joints, isRelative, velocity, acceleration, jerk);
    }

    @Override
    public void play(List<String> script) throws DornaClientException {
        start();
        LOGGER.info("Call play command");
        for (var messageJson : script) {
            var id = idGenerator.nextId();
            messageJson = MessageUtils.setId(messageJson, id);
            var future = messageProc.awaitCompletion(id);
            webSocket.request(1);
            webSocket.sendText(messageJson);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new DornaClientException(e);
            }
        }
    }

    @Override
    public DornaRobotModel model() {
        return dornaClientConfig.model();
    }
}
