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
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import pinorobotics.drac.CommandStatus;
import pinorobotics.drac.CommandType;
import pinorobotics.drac.DracMetrics;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.messages.Motion;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MessageProcessor {
    private static final XLogger LOGGER = XLogger.getLogger(MessageProcessor.class);
    private final Meter METER =
            GlobalOpenTelemetry.getMeter(MessageProcessor.class.getSimpleName());
    private final LongCounter MOTIO1N_MESSAGE_COUNT_METER =
            METER.counterBuilder(DracMetrics.MOTIO1N_MESSAGE_COUNT_METRIC)
                    .setDescription(DracMetrics.MOTION_MESSAGE_COUNT_METRIC_DESCRIPTION)
                    .build();
    private Map<String, CompletableFuture<Message>> pendingCommands = new HashMap<>();
    private Map<Integer, CompletableFuture<Message>> pendingCommandsAwaitingResult =
            new HashMap<>();
    private Map<Integer, CompletableFuture<Void>> pendingCommandsAwaitingCompletion =
            new HashMap<>();
    private MotionHolder lastMotion = new MotionHolder();

    public void process(Message message) {
        LOGGER.fine("New message: {0}", message);
        var cmd = message.command();
        // since motion messages received more often than any other we process them first
        if (Objects.equals(cmd, CommandType.MOTION)) {
            MOTIO1N_MESSAGE_COUNT_METER.add(1);
            lastMotion.update(message);
            return;
        }
        var id = message.id();
        if (processById(id, cmd, message)) return;
        var future = pendingCommands.get(cmd);
        if (future != null) {
            LOGGER.info("Command {0} result: {1}", cmd, message);
            future.complete(message);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean processById(int id, String cmd, Message message) {
        CompletableFuture future = pendingCommandsAwaitingResult.get(id);
        boolean withoutResult = false;
        if (future == null) {
            withoutResult = true;
            future = pendingCommandsAwaitingCompletion.get(id);
        }
        if (future == null) return false;
        var status =
                message.find("stat", Double.class)
                        .map(s -> CommandStatus.findOrCreate(s.intValue()))
                        .orElse(null);
        if (status == null) {
            LOGGER.info("Command with id {0} result: {1}", id, message);
            if (!withoutResult) future.complete(message);
        } else if (status == CommandStatus.Predefined.COMPLETED.value()) {
            LOGGER.info("Command with id {0} completed: {1}", id, message);
            if (withoutResult) future.complete(null);
        } else {
            LOGGER.info("Command with id {0} has status: {1}", id, status);
            if (status.isError())
                future.completeExceptionally(
                        new DornaClientException(
                                "Command " + cmd + " failed with status " + status));
        }
        return true;
    }

    public Future<Message> awaitResult(int id) {
        LOGGER.info("Awaiting result for command with id {0}", id);
        var future = new CompletableFuture<Message>();
        pendingCommandsAwaitingResult.put(id, future);
        return future;
    }

    public CompletableFuture<Void> awaitCompletion(int id) {
        LOGGER.info("Awaiting completion for command with id {0}", id);
        var future = new CompletableFuture<Void>();
        pendingCommandsAwaitingCompletion.put(id, future);
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
