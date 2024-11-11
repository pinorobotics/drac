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

/**
 * @author lambdaprime intid@protonmail.com
 */
public record DornaClientConfig(URI dornaUrl, Optional<Path> outputLog) {

    public static class Builder {
        private URI dornaUrl;
        private Optional<Path> outputLog = Optional.empty();

        public Builder(URI dornaUrl) {
            this.dornaUrl = dornaUrl;
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

        public DornaClientConfig build() {
            return new DornaClientConfig(this.dornaUrl, this.outputLog);
        }
    }
}
