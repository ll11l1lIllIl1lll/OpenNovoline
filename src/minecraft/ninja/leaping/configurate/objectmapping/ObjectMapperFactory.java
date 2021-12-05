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

import org.jetbrains.annotations.NotNull;

/**
 * A factory to produce {@link ObjectMapper} instances
 */
public interface ObjectMapperFactory {

    /**
     * Creates an {@link ObjectMapper} for the given type.
     *
     * @param type The type
     * @param <T>  The type
     * @return An object mapper
     * @throws ObjectMappingException If an exception occured whilst mapping
     */
    @NotNull <T> ObjectMapper<T> getMapper(@NotNull Class<T> type) throws ObjectMappingException;

}
