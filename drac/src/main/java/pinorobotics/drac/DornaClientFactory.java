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

import java.net.URI;
import pinorobotics.drac.impl.DornaClientImpl;
import pinorobotics.drac.impl.DornaClientNoop;

/**
 * Factory class for {@link DornaClient}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClientFactory {

    /**
     * Create client to Dorna2 Black (for other models use {@link #createClient(DornaClientConfig)}
     * with explicit {@link DornaRobotModel})
     *
     * @param dornaUrl websocket URL of the Dorna Command Server
     */
    public DornaClient createClient(URI dornaUrl) {
        return createClient(
                new DornaClientConfig.Builder(dornaUrl, DornaRobotModel.DORNA2_BLACK).build());
    }

    /** Create Dorna client with given configuration */
    public DornaClient createClient(DornaClientConfig config) {
        return config.noop() ? new DornaClientNoop(config) : new DornaClientImpl(config);
    }
}
