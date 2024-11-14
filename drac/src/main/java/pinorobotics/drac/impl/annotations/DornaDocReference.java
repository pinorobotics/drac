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
package pinorobotics.drac.impl.annotations;

import java.lang.annotation.Repeatable;

/**
 * Tracks implementation with respect to official Dorna documentation.
 *
 * @author lambdaprime intid@protonmail.com
 */
@Repeatable(DornaManualReferences.class)
public @interface DornaDocReference {

    /** Document name */
    String name();

    /** Document version if any */
    String version() default "";

    String paragraph();
}
