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

package ninja.leaping.configurate.util;

import cc.novoline.utils.java.Checks;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Utility class to cache more flexible enum lookup.
 *
 * <p>While normally case and punctuation have to match exactly, this method performs lookup that:</p>
 * <p>
 * <ul>
 *     <li>is case-insensitive</li>
 *     <li>ignores underscores</li>
 *     <li>caches mappings</li>
 * </ul>
 *
 * <p>If the enum has two fields that are equal except for case and underscores, an exact match
 * will return the appropriate value, and any fuzzy matches will map to the first value in the enum
 * that is applicable.</p>
 */
public final class EnumLookup {

    private EnumLookup() {
    }

    private static final LoadingCache<Class<? extends Enum<?>>, Map<String, Enum<?>>> ENUM_FIELD_CACHE = CacheBuilder.newBuilder().weakKeys().maximumSize(512).build(new CacheLoader<Class<? extends Enum<?>>, Map<String, Enum<?>>>() {

        @Override
        public Map<String, Enum<?>> load(Class<? extends Enum<?>> key) {
            Checks.notNull(key, "key");

            final Map<String, Enum<?>> ret = new HashMap<>();

            for (Enum<?> field : key.getEnumConstants()) {
                ret.put(field.name(), field);
                ret.putIfAbsent(processKey(field.name()), field);
            }

            return ImmutableMap.copyOf(ret);
        }
    });

    @NonNull
    private static String processKey(@NonNull String key) {
        // stick a flower at the front so processed keys are different from literal keys
        return "🌸" + key.toLowerCase().replace("_", "");
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T extends Enum<T>> Optional<T> lookupEnum(@NonNull Class<T> clazz, @NonNull String key) {
        Checks.notNull(clazz, "class");
        Checks.notNull(key, "key");

        try {
            final Map<String, Enum<?>> values = ENUM_FIELD_CACHE.get(clazz);
            final Enum<?> possibleReturn = values.get(key);

            if (possibleReturn != null) {
                return Optional.of((T) possibleReturn);
            } else {
                return Optional.ofNullable((T) values.get(processKey(key)));
            }
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

}
