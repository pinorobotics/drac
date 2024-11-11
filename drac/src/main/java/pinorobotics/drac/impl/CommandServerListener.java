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

import static pinorobotics.drac.impl.MessageUtils.parse;

import id.xfunction.logging.XLogger;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import pinorobotics.drac.DornaClient;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CommandServerListener implements Listener {
    private static final XLogger LOGGER = XLogger.getLogger(DornaClient.class);

    private StringBuilder buf = new StringBuilder();
    private MessageProcessor messageProc;

    public CommandServerListener(MessageProcessor messageProc) {
        this.messageProc = messageProc;
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        buf.append(data);
        if (last) {
            var jsonMessage = buf.toString();
            LOGGER.fine("incoming message: {0}", jsonMessage);
            parse(jsonMessage).ifPresent(messageProc::process);
            buf = new StringBuilder();
        } else {
            LOGGER.fine("incoming data: {0}", data);
        }
        webSocket.request(1);
        return null;
    }
}
