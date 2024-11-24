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

import pinorobotics.drac.impl.annotations.DornaDocReference;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record DornaRobotModel(
        String modelName, double[] lowerLimit, double[] upperLimit, Joints home) {

    @DornaDocReference(
            name = "Dorna Robot User Manual",
            version = "Last update on Aug 30, 2023",
            paragraph = "Motion concepts: Assigning values to the joints")
    public static final DornaRobotModel DORNA2_BLACK =
            new DornaRobotModel(
                    "Dorna 2 Black",
                    // -3.0543, -1.5707, -2.4783, -2.3561 (in radians)
                    new double[] {-175, -90, -142, -135, Double.NEGATIVE_INFINITY},
                    // 3.1415, 3.1415, 2.4783, 2.3561 (in radians)
                    new double[] {180, 180, 142, 135, Double.POSITIVE_INFINITY},
                    // 3.1415, 3.1415, -2.4783, 2.3561, 0.0 (in radians)
                    new Joints(180.0, 180.0, -142.0, 135.0, 0.0, 0.0, 0.0, 0.0));

    @DornaDocReference(
            name = "Dorna Robot User Manual",
            version = "Last update on Aug 30, 2023",
            paragraph = "Motion concepts: Assigning values to the joints")
    public static final DornaRobotModel DORNA2_BLUE =
            new DornaRobotModel(
                    "Dorna 2 Blue",
                    new double[] {-175, -90, -142, -135, Double.NEGATIVE_INFINITY},
                    new double[] {180, 180, 142, 135, Double.POSITIVE_INFINITY},
                    new Joints(180.0, 180.0, -142.0, 135.0, 0.0, 0.0, 0.0, 0.0));

    @DornaDocReference(
            name = "Dorna Robot User Manual",
            version = "Last update on Aug 30, 2023",
            paragraph = "Motion concepts: Assigning values to the joints")
    public static final DornaRobotModel DORNA2S =
            new DornaRobotModel(
                    "Dorna 2S",
                    new double[] {-175, -91, -142, -135, Double.NEGATIVE_INFINITY},
                    new double[] {180, 181, 142, 135, Double.POSITIVE_INFINITY},
                    new Joints(180.0, 181.0, -142.0, 135.0, 0.0, 0.0, 0.0, 0.0));

    /**
     * Home is a well known position of all joints when they are hitting their hard limits.
     *
     * @see <a href="https://doc.dorna.ai/docs/guides/getting-started/#homing-process">Homing
     *     process</a>
     */
    public Joints home() {
        return home;
    }
}
