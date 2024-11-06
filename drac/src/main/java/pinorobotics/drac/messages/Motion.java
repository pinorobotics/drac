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
package pinorobotics.drac.messages;

import pinorobotics.drac.Joints;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record Motion(
        Joints joints,
        double x,
        double y,
        double z,
        double a,
        double b,
        double c,
        double d,
        double e,
        double vel,
        double accel) {

    public Motion() {
        this(new Joints(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public String toString() {
        return "Motion [joints="
                + joints
                + ", x="
                + x
                + ", y="
                + y
                + ", z="
                + z
                + ", a="
                + a
                + ", b="
                + b
                + ", c="
                + c
                + ", d="
                + d
                + ", e="
                + e
                + ", vel="
                + vel
                + ", accel="
                + accel
                + "]";
    }
}
