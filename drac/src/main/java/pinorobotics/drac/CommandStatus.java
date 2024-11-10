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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CommandStatus {

    public enum Predefined {
        RECEIVED(new CommandStatus(0, "RECEIVED")),
        EXECUTING(new CommandStatus(1, "EXECUTING")),
        COMPLETED(new CommandStatus(2, "COMPLETED")),
        GENERAL_ERROR(new CommandStatus(-1, "General error")),
        FINAL_POSITION_OUT_OF_RANGE(new CommandStatus(-100, "Final position is out of range")),
        MIDPOINT_OUT_OF_RANGE_FOR_CIRCLE(
                new CommandStatus(-102, "Midpoint is out of range for circle")),
        MIDPOINT_NOT_PROVIDED_FOR_CIRCLE(
                new CommandStatus(-103, "Midpoint is not provided for circle")),
        VELOCITY_COEFFICIENT_OUT_OF_RANGE(
                new CommandStatus(-104, "Velocity coefficient is out of range")),
        ACCELERATION_COEFFICIENT_OUT_OF_RANGE(
                new CommandStatus(-105, "Acceleration coefficient is out of range")),
        JERK_COEFFICIENT_OUT_OF_RANGE(new CommandStatus(-106, "Jerk coefficient is out of range")),
        VELOCITY_SHOULD_BE_POSITIVE(new CommandStatus(-107, "Velocity should be positive")),
        ACCELERATION_SHOULD_BE_POSITIVE(new CommandStatus(-108, "Acceleration should be positive")),
        JERK_SHOULD_BE_POSITIVE(new CommandStatus(-109, "Jerk should be positive")),
        POINT_OUT_OF_RANGE_ON_PATH(new CommandStatus(-110, "Point out of range on the path")),
        CIRCLE_CANNOT_BE_REALIZED(new CommandStatus(-111, "Circle cannot be realized")),
        HALT_IN_PROCESS(new CommandStatus(-300, "Halt already in process")),
        ALARM_ACTIVATED(new CommandStatus(-400, "Alarm activated"));
        private CommandStatus val;

        Predefined(CommandStatus val) {
            this.val = val;
        }

        public CommandStatus value() {
            return val;
        }
    }

    private int status;
    private String message;

    private CommandStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CommandStatus findOrCreate(int status) {
        return Optional.ofNullable(MAP.get(status))
                .orElseGet(() -> new CommandStatus(status, "unknown"));
    }

    private static final Map<Integer, CommandStatus> MAP =
            Arrays.stream(Predefined.values())
                    .collect(Collectors.toMap(p -> p.val.status, p -> p.val));

    @Override
    public final String toString() {
        return "%d=<%s>".formatted(status, message);
    }

    public boolean isError() {
        return status < 0;
    }
}
