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

import java.util.Map;
import java.util.Optional;
import pinorobotics.drac.exceptions.DornaClientException;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record Message(Map<String, Object> body) {

    public Message() {
        this(Map.of());
    }

    public <T> Optional<T> find(String key, Class<T> type) {
        var val = body.get(key);
        if (type.isInstance(val)) return Optional.of((T) val);
        return Optional.empty();
    }

    public <T> T get(String key, Class<T> type) {
        return find(key, type)
                .orElseThrow(
                        () ->
                                new DornaClientException(
                                        "Missing '" + key + "' field inside received message"));
    }

    public String command() {
        return find("cmd", String.class).orElse(CommandType.NONE);
    }

    /**
     * @return 0 if id could not be found inside current message
     */
    public int id() {
        return find("id", Integer.class).orElse(0);
    }

    public Joints joints() {
        return new Joints(
                get("j0", Double.class),
                get("j1", Double.class),
                get("j2", Double.class),
                get("j3", Double.class),
                get("j4", Double.class),
                get("j5", Double.class),
                get("j6", Double.class),
                get("j7", Double.class));
    }
}
