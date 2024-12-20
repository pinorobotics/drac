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

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Optional;
import pinorobotics.drac.exceptions.DornaClientException;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DracSocketFactory {

    public DracSocket create(URI dornaUrl, MessageProcessor messageProc, Optional<Path> outputLog) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            return new DracSocket(
                    client.newWebSocketBuilder()
                            .buildAsync(dornaUrl, new CommandServerListener(messageProc))
                            .get(),
                    outputLog);
        } catch (Exception e) {
            throw new DornaClientException(e);
        }
    }
}
