/*
 * Copyright 2025 drac project
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

import java.util.List;
import pinorobotics.drac.DornaClient;
import pinorobotics.drac.DornaClientConfig;
import pinorobotics.drac.DornaRobotModel;
import pinorobotics.drac.Joints;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.messages.Motion;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DornaClientNoop implements DornaClient {

    private DornaClientConfig dornaClientConfig;
    private Motion currentMotion;

    public DornaClientNoop(DornaClientConfig dornaClientConfig) {
        this.dornaClientConfig = dornaClientConfig;
        currentMotion = new Motion(dornaClientConfig.model().home());
    }

    @Override
    public int version() throws DornaClientException {
        return 0;
    }

    @Override
    public void joint(Joints joints) throws DornaClientException {}

    @Override
    public void jmove(Joints joints, boolean isRelative) throws DornaClientException {
        currentMotion = new Motion(joints);
    }

    @Override
    public void jmove(
            Joints joints,
            boolean isRelative,
            boolean isAsync,
            boolean isContinuous,
            double velocity,
            double acceleration,
            double jerk)
            throws DornaClientException {
        currentMotion = new Motion(joints);
    }

    @Override
    public void motor(boolean isOn) throws DornaClientException {}

    @Override
    public DornaRobotModel model() {
        return dornaClientConfig.model();
    }

    @Override
    public Motion getLastMotion() {
        return currentMotion;
    }

    @Override
    public void play(List<String> script) throws DornaClientException {}

    @Override
    public void close() {}
}
