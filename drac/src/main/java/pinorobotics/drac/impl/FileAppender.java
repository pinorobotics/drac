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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import pinorobotics.drac.exceptions.DornaClientException;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class FileAppender implements AutoCloseable {

    private PrintWriter printWriter;

    public FileAppender(Path outputLog) {
        printWriter = openFile(outputLog);
    }

    @Override
    public void close() {
        printWriter.close();
    }

    public void append(String line) {
        printWriter.println(line);
    }

    private PrintWriter openFile(Path file) {
        try {
            return new PrintWriter(new FileWriter(file.toFile(), true));
        } catch (IOException e) {
            throw new DornaClientException(e);
        }
    }
}
