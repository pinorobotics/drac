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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import pinorobotics.drac.Message;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MessageUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String REGEXP_ID = "\"id\"\\s*:\\s*\\d+";

    public static Optional<Message> parse(String jsonMessage) {
        try {
            Map<String, Object> message = MAPPER.readValue(jsonMessage, Map.class);
            return Optional.of(new Message(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /** Set new or replace any existing id */
    public static String setId(String jsonMessage, int id) {
        var m = Pattern.compile(REGEXP_ID).matcher(jsonMessage);
        if (m.find()) {
            return m.replaceAll("\"id\":" + id);
        } else {
            return jsonMessage.replace("{", "{\"id\":" + id + ",");
        }
    }
}
