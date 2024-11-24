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
package pinorobotics.drac.tests;

import id.xfunction.PreconditionException;
import id.xfunction.ResourceUtils;
import id.xfunction.lang.XThread;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pinorobotics.drac.DornaClient;
import pinorobotics.drac.DornaClientConfig;
import pinorobotics.drac.DornaRobotModel;
import pinorobotics.drac.Joints;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.impl.CommandServerListener;
import pinorobotics.drac.impl.DornaClientImpl;
import pinorobotics.drac.impl.DracSocket;
import pinorobotics.drac.impl.DracSocketFactory;
import pinorobotics.drac.impl.MessageProcessor;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClientImplTest {
    private static final ResourceUtils resourceUtils = new ResourceUtils();

    @Test
    public void test_getLastMotion() {
        try (var client = createClient("recording_motion")) {
            Assertions.assertEquals(
                    "Motion [joints=[180.0, 180.0, -142.0, 140.0, 0.0, 0.0, 0.0, 0.0], x=36.526052,"
                            + " y=-0.0, z=301.938713, a=178.0, b=0.0, c=0.0, d=0.0, e=0.0, vel=0.0,"
                            + " accel=0.0]",
                    client.getLastMotion().toString());
        }
    }

    @Test
    public void test_version() {
        try (var client = createClient("recording_version")) {
            Assertions.assertEquals(109, client.version());
        }
    }

    @Test
    public void test_joint() {
        try (var client = createClient("recording_joint")) {
            var j = DornaRobotModel.DORNA2_BLACK.home().toArray();
            j[3] = 100;
            client.joint(Joints.of(j));
        }
    }

    @Test
    public void test_jmove_zero_velocity() {
        try (var client = createClient("recording_jmove")) {
            var joints = DornaRobotModel.DORNA2_BLACK.home();
            var ex =
                    Assertions.assertThrows(
                            DornaClientException.class, () -> client.jmove(joints, false, 0, 0, 0));
            Assertions.assertEquals(
                    "java.util.concurrent.ExecutionException:"
                        + " pinorobotics.drac.exceptions.DornaClientException: Command  failed with"
                        + " status -107=<Velocity should be positive>",
                    ex.getMessage());
        }
    }

    @Test
    public void test_jmove_limits() {
        try (var client = createClient("recording_jmove")) {
            var joints = DornaRobotModel.DORNA2_BLACK.home().toArray();
            joints[2] -= 1;
            var ex =
                    Assertions.assertThrows(
                            PreconditionException.class,
                            () -> client.jmove(Joints.of(joints), false, 0, 0, 0));
            Assertions.assertEquals(
                    "Joint 2 is out of limits: actual -143.000000, limit [-142.000000, 142.000000]",
                    ex.getMessage());
        }
    }

    @Test
    public void test_motor_safe_to_turn_off() throws Exception {
        try (var client = createClient("recording_motor_safe_to_turn_off")) {
            client.motor(true);
            var future = ForkJoinPool.commonPool().submit(() -> client.motor(false));
            XThread.sleep(500);
            Assertions.assertEquals(false, future.isDone());
            future.cancel(true);
        }
    }

    @Test
    public void test_outputLog() throws IOException {
        var outputLog = Files.createTempFile("drac", null);
        try (var client = createClient("recording_outputLog", Optional.of(outputLog))) {
            var motion = client.getLastMotion();
            client.motor(true);
            var joints = motion.joints().toArray();
            joints[3] -= 10;
            client.jmove(Joints.of(joints), false);
            joints[3] += 10;
            client.jmove(Joints.of(joints), false);
            client.motor(false);
        }
        Assertions.assertEquals(
                """
{"cmd":"motor","id":1,"motor":1}
{"cmd":"jmove","id":2,"j0":180.000000,"j1":180.000000,"j2":-142.000000,"j3":125.000000,"j4":-0.011250,"j5":0.000000,"j6":0.000000,"j7":0.000000,"rel":0,"vel":25.000000,"accel":500.000000,"jerk":2500.000000}
{"cmd":"jmove","id":3,"j0":180.000000,"j1":180.000000,"j2":-142.000000,"j3":135.000000,"j4":-0.011250,"j5":0.000000,"j6":0.000000,"j7":0.000000,"rel":0,"vel":25.000000,"accel":500.000000,"jerk":2500.000000}
{"cmd":"motor","id":4,"motor":0}
                """,
                Files.readString(outputLog));
    }

    @Test
    public void test_play() {
        try (var client = createClient("recording_play")) {
            client.motor(true);
            client.play(
                    """
{"cmd":"jmove","rel":0,"j0":180,"j1":180,"id":12,"j2":-142,"j3":135,"j4":0}
{"cmd":"jmove","rel":0,"j0":180,"j1":180,"j2":-142,"j3":91.9125,"j4":0.225, "id":123}
                    """);
            client.motor(false);
        }
    }

    private DornaClient createClient(String recording) {
        return createClient(recording, Optional.empty());
    }

    private DornaClient createClient(String recording, Optional<Path> outputLog) {
        var factory =
                new DracSocketFactory() {
                    @Override
                    public DracSocket create(
                            URI dornaUrl, MessageProcessor messageProc, Optional<Path> outputLog) {
                        return new DracSocket(
                                new CommandServerWebSocketMock(
                                        resourceUtils.readResourceAsList(recording),
                                        new CommandServerListener(messageProc)),
                                outputLog);
                    }
                };
        var configBuilder =
                new DornaClientConfig.Builder(
                        URI.create("ws://dorna"), DornaRobotModel.DORNA2_BLACK);
        outputLog.ifPresent(configBuilder::outputLog);
        return new DornaClientImpl(configBuilder.build(), factory);
    }
}
