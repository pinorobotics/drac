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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import pinorobotics.drac.CommandType;
import pinorobotics.drac.Message;
import pinorobotics.drac.messages.Motion;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MessageProcessor {
    private static final XLogger LOGGER = XLogger.getLogger(MessageProcessor.class);
    private Map<String, CompletableFuture<Message>> pendingCommands = new HashMap<>();
    private Map<Integer, CompletableFuture<Message>> pendingCommandsWithId = new HashMap<>();
    private MotionHolder lastMotion = new MotionHolder();

    public void process(Message message) {
        LOGGER.fine("New message: {0}", message);
        var cmd = message.command();
        // since motion messages received more often than any other we process them first
        if (Objects.equals(cmd, CommandType.MOTION)) {
            lastMotion.update(message);
            return;
        }
        var id = message.id();

        var future = pendingCommandsWithId.get(id);
        if (future != null) {
            LOGGER.info("Command with id {0} completed: {1}", id, message);
            future.complete(message);
        } else {
            future = pendingCommands.get(cmd);
            if (future != null) {
                LOGGER.info("Command {0} completed: {1}", cmd, message);
                future.complete(message);
            }
        }
    }

    public Future<Message> await(int id) {
        LOGGER.info("Awaiting message for command with id {0}", id);
        var future = new CompletableFuture<Message>();
        pendingCommandsWithId.put(id, future);
        return future;
    }

    public Future<Message> await(String command) {
        LOGGER.info("Awaiting message for command {0}", command);
        var future = new CompletableFuture<Message>();
        pendingCommands.put(command, future);
        return future;
    }

    public Motion getLastMotion() {
        return lastMotion.get();
    }
}
