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

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pinorobotics.drac.impl.MessageUtils;

public class MessageUtilsTest {

    record TestCase(String json, String expected) {}

    public static Stream<TestCase> setIdProvider() {
        return Stream.of(
                new TestCase(
                        """
                {"id": 111, "cmd":"jmove","rel":0,"j0":-10}""",
                        """
                {"id":222, "cmd":"jmove","rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"id" :111, "cmd":"jmove","rel":0,"j0":-10}""",
                        """
                        {"id":222, "cmd":"jmove","rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"id":111, "cmd":"jmove","rel":0,"j0":-10}""",
                        """
                        {"id":222, "cmd":"jmove","rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"id"    : 1, "cmd":"jmove","rel":0,"j0":-10}""",
                        """
                        {"id":222, "cmd":"jmove","rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"id"   :       1, "cmd":"jmove","rel":0,"j0":-10}""",
                        """
                        {"id":222, "cmd":"jmove","rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"cmd":"jmove","id":5,"rel":0,"j0":-10}""",
                        """
                        {"cmd":"jmove","id":222,"rel":0,"j0":-10}"""),
                new TestCase(
                        """
                        {"cmd":"jmove","rel":0,"j0":-10,"id":5}""",
                        """
                        {"cmd":"jmove","rel":0,"j0":-10,"id":222}"""),
                new TestCase(
                        """
                        {"cmd":"jmove","rel":0,"j0":-10,"ids":5}""",
                        """
                        {"id":222,"cmd":"jmove","rel":0,"j0":-10,"ids":5}"""),
                new TestCase(
                        """
                        {"cmd":"jmove"}""",
                        """
                        {"id":222,"cmd":"jmove"}"""));
    }

    @ParameterizedTest
    @MethodSource("setIdProvider")
    public void test_setId(TestCase tc) {
        Assertions.assertEquals(tc.expected, MessageUtils.setId(tc.json, 222));
    }
}
