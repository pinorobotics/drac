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

import id.xfunction.Preconditions;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Joint values are given in degrees.
 *
 * @author lambdaprime intid@protonmail.com
 */
public record Joints(
        double j0, double j1, double j2, double j3, double j4, double j5, double j6, double j7) {

    public static final Joints ZERO = new Joints();

    public static final Comparator<Joints> EUCLID_DISTANCE_COMPARATOR =
            (j1, j2) -> {
                return (int)
                        Math.sqrt(
                                Math.pow(j1.j0 - j2.j0, 2)
                                        + Math.pow(j1.j1 - j2.j1, 2)
                                        + Math.pow(j1.j2 - j2.j2, 2)
                                        + Math.pow(j1.j3 - j2.j3, 2)
                                        + Math.pow(j1.j4 - j2.j4, 2)
                                        + Math.pow(j1.j5 - j2.j5, 2)
                                        + Math.pow(j1.j6 - j2.j6, 2)
                                        + Math.pow(j1.j7 - j2.j7, 2));
            };

    public static Joints of(double[] joints) {
        Preconditions.isLessOrEqual(8, joints.length, "Mismatch in number of joints");
        if (joints.length < 8) {
            joints = Arrays.copyOf(joints, 8);
        }
        return new Joints(
                joints[0], joints[1], joints[2], joints[3], joints[4], joints[5], joints[6],
                joints[7]);
    }

    public static Joints ofRadians(double[] joints) {
        var buf = new double[8];
        for (int i = 0; i < joints.length; i++) {
            buf[i] = Math.toDegrees(joints[i]);
        }
        return of(buf);
    }

    public Joints() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public Joints(double j0, double j1, double j2, double j3, double j4) {
        this(j0, j1, j2, j3, j4, 0, 0, 0);
    }

    public double[] toArray() {
        return new double[] {j0, j1, j2, j3, j4, j5, j6, j7};
    }

    public double[] toArrayOfRadians() {
        return new double[] {
            Math.toRadians(j0),
            Math.toRadians(j1),
            Math.toRadians(j2),
            Math.toRadians(j3),
            Math.toRadians(j4),
            Math.toRadians(j5),
            Math.toRadians(j6),
            Math.toRadians(j7)
        };
    }

    @Override
    public final String toString() {
        return Arrays.toString(toArray());
    }
}
