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

import id.xfunction.ResourceUtils;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pinorobotics.drac.DornaClient;
import pinorobotics.drac.Joints;
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
    public void test_version() {
        try (var client = createClient("recording_version")) {
            Assertions.assertEquals(109, client.version());
        }
    }

    @Test
    public void test_joint() {
        try (var client = createClient("recording_joint")) {
            var j = Joints.HOME_DORNA2_BLACK.toArray();
            j[3] = 100;
            client.joint(Joints.of(j));
        }
    }

    private DornaClient createClient(String recording) {
        var factory =
                new DracSocketFactory() {
                    @Override
                    public DracSocket create(URI dornaUrl, MessageProcessor messageProc) {
                        return new DracSocket(
                                new CommandServerWebSocketMock(
                                        resourceUtils.readResourceAsList(recording),
                                        new CommandServerListener(messageProc)));
                    }
                };
        return new DornaClientImpl(URI.create("dorna"), factory);
    }
}
