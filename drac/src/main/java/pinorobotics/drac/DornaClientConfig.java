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
import java.nio.file.Path;
import java.util.Optional;
import pinorobotics.drac.impl.annotations.DornaDocReference;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record DornaClientConfig(
        URI dornaUrl,
        DornaRobotModel model,
        Optional<Path> outputLog,
        boolean confirmMotorTurnOff,
        double velocity,
        double acceleration,
        double jerk) {

    public static class Builder {

        public static final double DEFAULT_VELOCITY = 25;
        public static final double DEFAULT_ACCEL = 500;
        public static final double DEFAULT_JERK = 2500;

        private URI dornaUrl;
        private Optional<Path> outputLog = Optional.empty();
        private boolean confirmMotorTurnOff = true;
        private DornaRobotModel model;
        private double velocity = DEFAULT_VELOCITY;
        private double acceleration = DEFAULT_ACCEL;
        private double jerk = DEFAULT_JERK;

        public Builder(URI dornaUrl, DornaRobotModel model) {
            this.dornaUrl = dornaUrl;
            this.model = model;
            if (!this.dornaUrl.isAbsolute()) {
                throw new IllegalArgumentException("dornaUrl must be an absolute URI");
            }
        }

        /**
         * Record all commands sent to Command Server into a file.
         *
         * <p>File will contain all commands in JSON format which can be played later in DornaLab
         */
        public Builder outputLog(Path outputLog) {
            this.outputLog = Optional.ofNullable(outputLog);
            return this;
        }

        /**
         * Require user confirmation every time when motor is about to turn off. Default is true.
         *
         * <p>Turning off motor can potentially cause robot arm to fall [Dorna Robot User Manual
         * (Last update on Aug 30, 2023): Dorna Lab: Motors]
         *
         * <p>To prevent this from happening, every time when user turns off the motor we show a
         * warning message and wait until user confirms if it is safe to proceed.
         *
         * <p>The warning does not happen when robot is in {@link
         * pinorobotics.drac.DornaRobotModel#home()} position.
         */
        @DornaDocReference(
                name = "Dorna Robot User Manual",
                version = "Last update on Aug 30, 2023",
                paragraph = "Dorna Lab: Motors")
        public Builder confirmMotorShutOff(boolean confirmMotorShutOff) {
            this.confirmMotorTurnOff = confirmMotorShutOff;
            return this;
        }

        /**
         * Velocity for all motion commands
         *
         * <p>Default {@link #DEFAULT_VELOCITY}
         */
        public Builder velocity(double velocity) {
            this.velocity = velocity;
            return this;
        }

        /**
         * Acceleration for all motion commands
         *
         * <p>Default {@link #DEFAULT_ACCEL}
         */
        public Builder acceleration(double accel) {
            this.acceleration = accel;
            return this;
        }

        /**
         * Jerk for all motion commands
         *
         * <p>Default {@link #DEFAULT_JERK}
         */
        public Builder jerk(double jerk) {
            this.jerk = jerk;
            return this;
        }

        public DornaClientConfig build() {
            return new DornaClientConfig(
                    dornaUrl, model, outputLog, confirmMotorTurnOff, velocity, acceleration, jerk);
        }
    }
}
