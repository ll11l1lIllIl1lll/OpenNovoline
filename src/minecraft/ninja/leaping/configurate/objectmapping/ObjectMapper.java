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

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * This is the object mapper. It handles conversion between configuration nodes and
 * fields annotated with {@link Setting} in objects.
 * <p>
 * Values in the node not used by the mapped object will be preserved.
 *
 * @param <T> The type to work with
 */
public class ObjectMapper<T> {

    protected final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Map<String, FieldData> cachedFields = new LinkedHashMap<>();


    /**
     * Create a new object mapper that can work with objects of the given class using the
     * {@link DefaultObjectMapperFactory}.
     *
     * @param clazz The type of object
     * @param <T>   The type
     * @return An appropriate object mapper instance. May be shared with other users.
     * @throws ObjectMappingException If invalid annotated fields are presented
     */
    public static <T> ninja.leaping.configurate.objectmapping.ObjectMapper<T> forClass(@NotNull Class<T> clazz) throws ObjectMappingException {
        return DefaultObjectMapperFactory.getInstance().getMapper(clazz);
    }

    /**
     * Creates a new object mapper bound to the given object.
     *
     * @param obj The object
     * @param <T> The object type
     * @return An appropriate object mapper instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> ninja.leaping.configurate.objectmapping.ObjectMapper<T>.BoundInstance forObject(@NotNull T obj) throws ObjectMappingException {
        return forClass((Class<T>) requireNonNull(obj).getClass()).bind(obj);
    }

    public interface FieldData {

        void serializeTo(Object obj, ConfigurationNode node) throws ObjectMappingException;

        void deserializeFrom(Object obj, ConfigurationNode node) throws ObjectMappingException;

    }

    /**
     * Holder for field-specific information
     */
    public static class SampleFieldData implements FieldData {

        protected final Field field;
        protected final TypeToken<?> fieldType;
        protected final String comment;

        public SampleFieldData(Field field, String comment) {
            this.field = field;
            this.comment = comment;
            this.fieldType = TypeToken.of(field.getGenericType());
        }

        public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
            final TypeSerializer<?> serial = node.getOptions().getSerializers().get(this.fieldType);

            if (serial == null) {
                throw new ObjectMappingException("No TypeSerializer found for field " + field.getName() + " of type " + this.fieldType);
            }

            deserializeAndSet(instance, node, serial);
        }

        protected void deserializeAndSet(Object instance, ConfigurationNode node, TypeSerializer<?> serial) throws ObjectMappingException {
            final Object newVal = node.isVirtual() ? null : serial.deserialize(this.fieldType, node);

            try {
                if (newVal == null) {
                    final Object existingVal = field.get(instance);
                    if (existingVal != null) serializeTo(instance, node);
                } else {
                    field.set(instance, newVal);
                }
            } catch (IllegalAccessException e) {
                throw new ObjectMappingException("Unable to deserialize field " + field.getName(), e);
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            try {
                Object fieldVal = this.field.get(instance);

                if (fieldVal == null) {
                    node.setValue(null);
                } else {
                    TypeSerializer serial = node.getOptions().getSerializers().get(this.fieldType);

                    if (serial == null) {
                        throw new ObjectMappingException("No TypeSerializer found for field " + field.getName() + " of type " + this.fieldType);
                    }

                    serial.serialize(this.fieldType, fieldVal, node);
                }

                if (node instanceof CommentedConfigurationNode && this.comment != null && !this.comment.isEmpty()) {
                    CommentedConfigurationNode commentNode = (CommentedConfigurationNode) node;

                    if (!commentNode.getComment().isPresent()) {
                        commentNode.setComment(this.comment);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new ObjectMappingException("Unable to serialize field " + field.getName(), e);
            }
        }

    }

    /**
     * Represents an object mapper bound to a certain instance of the object
     */
    public class BoundInstance {

        private final T boundInstance;

        protected BoundInstance(T boundInstance) {
            this.boundInstance = boundInstance;
        }

        /**
         * Populate the annotated fields in a pre-created object
         *
         * @param source The source to get data from
         * @return The object provided, for easier chaining
         * @throws ObjectMappingException If an error occurs while populating data
         */
        public T populate(ConfigurationNode source) throws ObjectMappingException {
            for (Map.Entry<String, FieldData> ent : cachedFields.entrySet()) {
                ConfigurationNode node = source.getNode(ent.getKey());
                ent.getValue().deserializeFrom(boundInstance, node);
            }

            return boundInstance;
        }

        /**
         * Serialize the data contained in annotated fields to the configuration node.
         *
         * @param target The target node to serialize to
         * @throws ObjectMappingException if serialization was not possible due to some error.
         */
        public void serialize(ConfigurationNode target) throws ObjectMappingException {
            for (Map.Entry<String, FieldData> ent : cachedFields.entrySet()) {
                ConfigurationNode node = target.getNode(ent.getKey());
                ent.getValue().serializeTo(boundInstance, node);
            }
        }

        /**
         * Return the instance this mapper is bound to.
         *
         * @return The active instance
         */
        public T getInstance() {
            return boundInstance;
        }

    }

    /**
     * Create a new object mapper of a given type
     *
     * @param clazz The type this object mapper will work with
     * @throws ObjectMappingException if the provided class is in someway invalid
     */
    protected ObjectMapper(Class<T> clazz) throws ObjectMappingException {
        this.clazz = clazz;
        Constructor<T> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException ignore) {
        }
        this.constructor = constructor;
        Class<? super T> collectClass = clazz;
        do {
            collectFields(cachedFields, collectClass);
        } while (!(collectClass = collectClass.getSuperclass()).equals(Object.class));
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    protected void collectFields(Map<String, FieldData> cachedFields, Class<? super T> clazz) throws ObjectMappingException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                final Setting setting = field.getAnnotation(Setting.class);
                String path = setting.value();

                if (path.isEmpty()) {
                    throw new ObjectMappingException("Empty path");
                }

                final FieldData data = new SampleFieldData(field, setting.comment());
                field.setAccessible(true);

                if (!cachedFields.containsKey(path)) {
                    cachedFields.put(path, data);
                }
            }
        }
    }

    /**
     * Create a new instance of an object of the appropriate type. This method is not
     * responsible for any population.
     *
     * @return The new object instance
     * @throws ObjectMappingException If constructing a new instance was not possible
     */
    protected T constructObject() throws ObjectMappingException {
        if (constructor == null) {
            throw new ObjectMappingException("No zero-arg constructor is available for class " + clazz + " but is required to construct new instances!");
        }
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ObjectMappingException("Unable to create instance of target class " + clazz, e);
        }
    }

    /**
     * Returns whether this object mapper can create new object instances. This may be
     * false if the provided class has no zero-argument constructors.
     *
     * @return Whether new object instances can be created
     */
    public boolean canCreateInstances() {
        return constructor != null;
    }

    /**
     * Return a view on this mapper that is bound to a single object instance
     *
     * @param instance The instance to bind to
     * @return A view referencing this mapper and the bound instance
     */
    public BoundInstance bind(T instance) {
        return new BoundInstance(instance);
    }

    /**
     * Returns a view on this mapper that is bound to a newly created object instance
     *
     * @return Bound mapper attached to a new object instance
     * @throws ObjectMappingException If the object could not be constructed correctly
     * @see #bind(Object)
     */
    public BoundInstance bindToNew() throws ObjectMappingException {
        return new BoundInstance(constructObject());
    }

    public Class<T> getMappedType() {
        return this.clazz;
    }

}
