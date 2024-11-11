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
package pinorobotics.drac.tests;

import id.xfunction.concurrent.NamedThreadFactory;
import id.xfunction.function.Unchecked;
import id.xfunction.lang.XThread;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Assertions;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CommandServerWebSocketMock implements WebSocket {

    private static final String EOF = "";
    private StringBuilder buf = new StringBuilder();
    private BlockingQueue<String> in = new SynchronousQueue<>();
    private BlockingQueue<String> out = new SynchronousQueue<>();
    private BlockingQueue<String> periodic = new LinkedBlockingQueue<String>();
    private AtomicLong requested = new AtomicLong();
    private ExecutorService executor =
            Executors.newCachedThreadPool(
                    new NamedThreadFactory(CommandServerWebSocketMock.class.getSimpleName()));

    public CommandServerWebSocketMock(List<String> recording, Listener listener) {

        // publish Command Server periodic messages
        executor.execute(
                () -> {
                    while (true) {
                        var msg = Unchecked.get(periodic::take);
                        if (msg == EOF) {
                            System.out.println("Stopped Command Server periodic thread");
                            periodic.clear();
                            return;
                        }
                        Unchecked.run(() -> in.put(msg));
                        periodic.add(msg);
                    }
                });

        // send Command Server response messages
        executor.execute(
                () -> {
                    while (true) {
                        while (requested.get() > 0) {
                            var msg = Unchecked.get(in::take);
                            if (msg == EOF) {
                                System.out.println("Stopped Command Server response thread");
                                return;
                            }
                            listener.onText(this, msg, true);
                            requested.decrementAndGet();
                        }
                        XThread.sleep(500);
                    }
                });

        // play the recording
        executor.execute(
                () -> {
                    var msg = "";
                    Queue<String> q = new LinkedList<>(recording);
                    try {
                        while ((msg = q.poll()) != null) {
                            var ch = msg.charAt(0);
                            msg = msg.substring(1);
                            if (ch == '!') {
                                periodic.put(msg);
                            } else if (ch == '<') {
                                out.put(msg);
                            } else if (ch == '>') {
                                in.put(msg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Unchecked.run(
                            () -> {
                                periodic.put(EOF);
                                while (periodic.size() > 0)
                                    ;
                                in.put(EOF);
                                out.put(EOF);
                            });
                    System.out.println("Stopped play");
                });

        listener.onOpen(this);
    }

    @Override
    public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
        buf.append(data);
        if (last) {
            var expected = Unchecked.get(out::take);
            Assertions.assertEquals(expected, buf.toString());
            buf.setLength(0);
        }
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
        var expected = Unchecked.get(out::take);
        Assertions.assertEquals(expected, EOF);
        executor.close();
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public void request(long n) {
        requested.addAndGet(n);
    }

    @Override
    public String getSubprotocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOutputClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInputClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abort() {
        executor.close();
    }
}
