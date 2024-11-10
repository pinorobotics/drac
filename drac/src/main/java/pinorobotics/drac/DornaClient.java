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

import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.messages.Motion;

/**
 * Client to Dorna robotic arm
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface DornaClient extends AutoCloseable {

    double DEFAULT_VELOCITY = 25;
    double DEFAULT_ACCEL = 500;
    double DEFAULT_JERK = 2500;

    /**
     * @return last motion message received from the Command Server
     */
    Motion getLastMotion();

    /**
     * @return the current version of the firmware
     * @see <a href="https://doc.dorna.ai/docs/cmd/version/">version command</a>
     */
    int version() throws DornaClientException;

    /**
     * @see <a href="https://doc.dorna.ai/docs/cmd/joint/">joint command</a>
     */
    void joint(Joints joints) throws DornaClientException;

    /**
     * Calls {@link #jmove(Joints, boolean, double, double, double)} with default values ({@link
     * #DEFAULT_VELOCITY}, ...)
     *
     * @see #jmove(Joints, boolean, double, double, double)
     */
    default void jmove(Joints joints, boolean isRelative) throws DornaClientException {
        jmove(joints, isRelative, DEFAULT_VELOCITY, DEFAULT_ACCEL, DEFAULT_JERK);
    }

    /**
     * @see <a href="https://doc.dorna.ai/docs/cmd/joint%20move/">jmove command</a>
     */
    void jmove(Joints joints, boolean isRelative, double velocity, double acceleration, double jerk)
            throws DornaClientException;

    /**
     * @param isOn motor on or off
     * @see <a href="https://doc.dorna.ai/docs/cmd/motor/">motor command</a>
     */
    void motor(boolean isOn) throws DornaClientException;

    @Override
    void close();
}
