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
import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.ExecutionException;
import pinorobotics.drac.CommandType;
import pinorobotics.drac.DornaClient;
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
    private URI dornaUrl;
    private WebSocket webSocket;

    public DornaClientImpl(URI dornaUrl) {
        this.dornaUrl = dornaUrl;
    }

    @Override
    public Motion getLastMotion() {
        return messageProc.getLastMotion();
    }

    @Override
    public int version() throws DornaClientException {
        start();
        LOGGER.info("Call version command");
        var command = """
                {"cmd":"%s"}""".formatted(CommandType.VERSION);
        try {
            webSocket.sendText(command, true).get();
            var future = messageProc.await(CommandType.VERSION);
            webSocket.request(1);
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
        try {
            webSocket.sendText(command, true).get();
            var future = messageProc.await(id);
            webSocket.request(1);
            Preconditions.equals(joints, future.get().joints());
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    @Override
    protected void onClose() {
        LOGGER.info("Closing connection to {0}", dornaUrl);
        try {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "").get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    @Override
    protected void onStart() {
        LOGGER.info("Opening connection to {0}", dornaUrl);
        HttpClient client = HttpClient.newHttpClient();
        try {
            webSocket =
                    client.newWebSocketBuilder()
                            .buildAsync(dornaUrl, new CommandServerListener(messageProc))
                            .get();
        } catch (Exception e) {
            throw new DornaClientException(e);
        }
    }
}
