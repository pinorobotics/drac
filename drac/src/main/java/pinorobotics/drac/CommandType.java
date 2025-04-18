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

/**
 * List of Dorna Command Server commands which are supported by <b>drac</b>
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface CommandType {
    /** Command field is not present inside the message received from the Command Server */
    String NONE = "";

    String MOTION = "motion";
    String VERSION = "version";
    String JOINT = "joint";
    String JMOVE = "jmove";
    String MOTOR = "motor";
}
