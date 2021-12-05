/*
 * Configurate
 * Copyright (C) zml and Configurate contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.leaping.configurate.objectmapping;

import java.lang.annotation.*;

/**
 * Marks a field to be mapped by an {@link ObjectMapper}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Setting {

    /**
     * The path this setting is located at
     *
     * @return The path
     */
    String value();

    /**
     * The default comment associated with this configuration node
     * This will be applied to any comment-capable configuration loader
     *
     * @return The comment
     */
    String comment() default "";

}
