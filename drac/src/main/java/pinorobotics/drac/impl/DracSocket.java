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
import java.net.http.WebSocket;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import pinorobotics.drac.exceptions.DornaClientException;
import pinorobotics.drac.metrics.DracMetrics;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DracSocket {
    private static final XLogger LOGGER = XLogger.getLogger(DracSocket.class);
    private final Meter METER = GlobalOpenTelemetry.getMeter(DracSocket.class.getSimpleName());
    private final LongCounter SENT_BYTES_COUNT_METER =
            METER.counterBuilder(DracMetrics.SENT_BYTES_COUNT_METRIC)
                    .setDescription(DracMetrics.SENT_BYTES_COUNT_METRIC_DESCRIPTION)
                    .build();
    private WebSocket socket;
    private Optional<FileAppender> outputLog;

    @SuppressWarnings("exports")
    public DracSocket(WebSocket socket, Optional<Path> outputLog) {
        this.socket = socket;
        this.outputLog = outputLog.map(FileAppender::new);
    }

    public void sendText(String command) {
        LOGGER.fine("send: {0}", command);
        outputLog.ifPresent(out -> out.append(command));
        try {
            socket.sendText(command, true).get();
            SENT_BYTES_COUNT_METER.add(command.length());
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }

    public void request(int n) {
        LOGGER.fine("requested number of messages: {0}", n);
        socket.request(n);
    }

    public void sendClose() {
        LOGGER.fine("send close");
        outputLog.ifPresent(FileAppender::close);
        try {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "").get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DornaClientException(e);
        }
    }
}
