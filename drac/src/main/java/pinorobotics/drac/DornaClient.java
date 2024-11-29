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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
     * @param isAsync send command and do not wait for it completion (no unique id is assigned to
     *     the command)
     * @see <a href="https://doc.dorna.ai/docs/cmd/joint%20move/">jmove command</a>
     */
    void jmove(
            Joints joints,
            boolean isRelative,
            boolean isAsync,
            double velocity,
            double acceleration,
            double jerk)
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
     * @return Dorna model to which this client instance connects
     */
    DornaRobotModel model();

    /**
     * Last motion message received from the Command Server
     *
     * <p>This is offline operation.
     */
    Motion getLastMotion();

    /**
     * Send list of recorded commands to Dorna Command Server.
     *
     * <p>Commands must be given is JSON format with one command per line (same as they are used in
     * DornaLab)
     *
     * @see {@link #play(List)}
     */
    default void play(Path script) throws DornaClientException {
        try {
            play(Files.readAllLines(script));
        } catch (DornaClientException | IOException e) {
            throw new DornaClientException(e);
        }
    }

    /**
     * @param script multi-line string with one command per line. Each command must be in JSON
     *     format.
     * @throws DornaClientException
     * @see {@link #play(List)}
     */
    default void play(String script) throws DornaClientException {
        play(script.lines().toList());
    }

    /**
     * Play the script
     *
     * <p>Send commands one by one to the Dorna Command Server. Each command is send only when
     * previous command is completed. If any of the commands fails then play stops and {@link
     * DornaClientException} is thrown.
     *
     * <p>If any of the command has "id" field set then there is no guarantee that it will be
     * preserved. It can be replaced with a client managed id.
     *
     * @param script list of commands in JSON format. Example:
     *     <pre>{@code
     * {"cmd":"jmove","rel":0,"j0":180,"j1":180,"j2":-142,"j3":135,"j4":0}
     * {"cmd":"jmove","rel":0,"j0":180,"j1":180,"j2":-142,"j3":91.9125,"j4":0.225}
     * {"cmd":"jmove","rel":0,"j0":180,"j1":180,"j2":-142,"j3":53.2125,"j4":0.27}
     * }</pre>
     */
    void play(List<String> script) throws DornaClientException;

    /**
     * Home all joints of the Dorna arm.
     *
     * <p>Before turning off motors it is recommended to put it into home position.
     *
     * @see DornaRobotModel#DORNA2_BLACK{@link #home()}
     */
    default void home() throws DornaClientException {
        jmove(model().home(), false);
    }

    @Override
    void close();
}
