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
 * Client to Dorna robotic arm.
 *
 * <p>Offline operations does not involve any interactions with Dorna Command Server and so they are
 * cheap to call.
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface DornaClient extends AutoCloseable {

    double DEFAULT_VELOCITY = 25;
    double DEFAULT_ACCEL = 500;
    double DEFAULT_JERK = 2500;

    /*
     *  Dorna commands goes below
     */

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
     * Calls {@link #jmove(Joints, boolean, double, double, double)} with with current values ({@link
     * #setVeloctiry(double), ...)
     *
     * @see #jmove(Joints, boolean, double, double, double)
     */
    void jmove(Joints joints, boolean isRelative) throws DornaClientException;

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

    /*
     * drac features goes below
     */

    /**
     * Last motion message received from the Command Server
     *
     * <p>This is offline operation.
     */
    Motion getLastMotion();

    /**
     * Change velocity for all motion commands of this client instance. Default is {@link
     * #DEFAULT_VELOCITY}
     *
     * <p>This is offline operation. The value is stored internally and it is not sent to the Dorna
     * Command Server. Instead it is included later in all motion commands.
     */
    void setVelocity(double vel);

    /**
     * Change acceleration for all motion commands of this client instance. Default is {@link
     * #DEFAULT_ACCEL}
     *
     * <p>This is offline operation. The value is stored internally and it is not sent to the Dorna
     * Command Server. Instead it is included later in all motion commands.
     */
    void setAcceleration(double accel);

    /**
     * Change jerk for all motion commands of this client instance. Default is {@link #DEFAULT_JERK}
     *
     * <p>This is offline operation. The value is stored internally and it is not sent to the Dorna
     * Command Server. Instead it is included later in all motion commands.
     */
    void setJerk(double jerk);

    @Override
    void close();
}
