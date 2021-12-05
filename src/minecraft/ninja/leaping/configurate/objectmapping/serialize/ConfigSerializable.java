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

package ninja.leaping.configurate.objectmapping.serialize;

import java.lang.annotation.*;

/**
 * This annotation is used to indicate that the given type is capable of being serialized and
 * deserialized by the configuration object mapper.
 *
 * <p>Types with this annotation must have a zero-argument constructor to be instantiated by the
 * object mapper (though already instantiated objects can be passed to the object mapper to be
 * populated with settings)</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ConfigSerializable {
}
