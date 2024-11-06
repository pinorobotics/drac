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
package pinorobotics.drac.impl;

import id.xfunction.logging.XLogger;
import java.util.Objects;
import pinorobotics.drac.Message;
import pinorobotics.drac.messages.Motion;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MotionHolder {
    private static final XLogger LOGGER = XLogger.getLogger(MotionHolder.class);
    private Message previousMessage = new Message();
    private Motion motion = new Motion();

    public void update(Message message) {
        if (Objects.equals(message, previousMessage)) return;
        try {
            motion =
                    new Motion(
                            message.joints(),
                            message.get("x", Double.class),
                            message.get("y", Double.class),
                            message.get("z", Double.class),
                            message.get("a", Double.class),
                            message.get("b", Double.class),
                            message.get("c", Double.class),
                            message.get("d", Double.class),
                            message.get("e", Double.class),
                            message.get("vel", Double.class),
                            message.get("accel", Double.class));
        } catch (Exception e) {
            LOGGER.warning("Could not parse Motion message: {0}", e.getMessage());
            LOGGER.fine(e.getMessage(), e);
        }
    }

    public Motion get() {
        return motion;
    }
}
