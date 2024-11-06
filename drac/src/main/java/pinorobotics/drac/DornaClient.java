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
package pinorobotics.drac;

import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.ExecutionException;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.impl.CommandServerListener;
import pinorobotics.drac.impl.MessageProcessor;
import pinorobotics.drac.messages.Motion;

/**
 * Client to Dorna Command Server
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClient extends IdempotentService {
    private static final XLogger LOGGER = XLogger.getLogger(DornaClient.class);

    private MessageProcessor messageProc = new MessageProcessor();
    private URI dornaUrl;
    private WebSocket webSocket;

    public DornaClient(URI dornaUrl) {
        this.dornaUrl = dornaUrl;
    }

    /**
     * @return last motion message received from the Command Server
     */
    public Motion getLastMotion() {
        return messageProc.getLastMotion();
    }

    /**
     * @return the current version of the firmware
     */
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
