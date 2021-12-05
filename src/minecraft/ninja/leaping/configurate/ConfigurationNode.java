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

package ninja.leaping.configurate;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A node in the configuration tree.
 *
 * <p>All aspects of a configurations structure are represented using instances of
 * {@link ninja.leaping.configurate.ConfigurationNode}, and the links between them.</p>
 *
 * <p>{@link ninja.leaping.configurate.ConfigurationNode}s can hold different types of {@link ValueType values}. They can:</p>
 * <p>
 * <ul>
 *     <li>Hold a single "scalar" value ({@link ValueType#SCALAR})</li>
 *     <li>Represent a "list" of child {@link ninja.leaping.configurate.ConfigurationNode}s ({@link ValueType#LIST})</li>
 *     <li>Represent a "map" of child {@link ninja.leaping.configurate.ConfigurationNode}s ({@link ValueType#MAP})</li>
 *     <li>Hold no value at all ({@link ValueType#NULL})</li>
 * </ul>
 *
 * <p>The overall configuration stems from a single "root" node, which is provided by the
 * {@link ConfigurationLoader}, or by other means programmatically.</p>
 *
 * <p>This is effectively the main class of ninja.leaping.configurate.</p>
 */
public interface ConfigurationNode {

    int NUMBER_DEF = 0;

    /**
     * Gets the "key" of this node.
     *
     * <p>The key determines this {@link ninja.leaping.configurate.ConfigurationNode}s position within the overall
     * configuration structure.</p>
     *
     * <p>If this node is currently {@link #isVirtual() virtual}, this method's result may be
     * inaccurate.</p>
     *
     * <p>Note that this method only returns the nearest "link" in the hierarchy, and does not
     * return a representation of the full path. See {@link #getPath()} for that.</p>
     *
     * <p>The {@link ninja.leaping.configurate.ConfigurationNode}s returned as values from {@link #getChildrenMap()} will
     * have keys derived from their pairing in the map node.</p>
     *
     * <p>The {@link ninja.leaping.configurate.ConfigurationNode}s returned from {@link #getChildrenList()} will have keys
     * derived from their position (index) in the list node.</p>
     *
     * @return The key of this node
     */
    @Nullable Object getKey();

    /**
     * Gets the full path of {@link #getKey() keys} from the root node to this node.
     *
     * <p>Node implementations may not keep a full path for each node, so this method may be
     * somewhat complex to calculate.</p>
     *
     * @return An array compiled from the keys for each node up the hierarchy
     */
    @NotNull Object[] getPath();

    /**
     * Gets the parent of this node.
     *
     * <p>If this node is currently {@link #isVirtual() virtual}, this method's result may be
     * inaccurate.</p>
     *
     * @return The nodes parent
     */
    @Nullable ConfigurationNode getParent();

    /**
     * Gets the node at the given (relative) path, possibly traversing multiple levels of nodes.
     *
     * <p>This is the main method used to navigate through the configuration.</p>
     *
     * <p>The path parameter effectively consumes an array of keys, which locate the unique position
     * of a given node within the structure.</p>
     *
     * <p>A node is <b>always</b> returned by this method. If the given node does not exist in the
     * structure, a {@link #isVirtual() virtual} node will be returned which represents the
     * position.</p>
     *
     * @param path The path to fetch the node at
     * @return The node at the given path, possibly virtual
     */
    @NotNull ConfigurationNode getNode(@NotNull Object... path);

    /**
     * Gets if this node is virtual.
     *
     * <p>Virtual nodes are nodes which are not attached to a wider configuration structure.</p>
     *
     * <p>A node is primarily "virtual" when it has no set value.</p>
     *
     * @return true if this node is virtual
     */
    boolean isVirtual();

    /**
     * Gets the options that currently apply to this node
     *
     * @return The ConfigurationOptions instance that governs the functionality of this node
     */
    @NotNull ConfigurationOptions getOptions();

    /**
     * Gets the value type of this node.
     *
     * @return The value type
     */
    @NotNull ValueType getValueType();

    /**
     * Gets if this node has "list children".
     *
     * @return if this node has children in the form of a list
     */
    default boolean hasListChildren() {
        return getValueType() == ValueType.LIST;
    }

    /**
     * Gets if this node has "map children".
     *
     * @return if this node has children in the form of a map
     */
    default boolean hasMapChildren() {
        return getValueType() == ValueType.MAP;
    }

    /**
     * Gets the "list children" attached to this node, if it has any.
     *
     * <p>If this node does not {@link #hasListChildren() have list children}, an empty list is
     * returned.</p>
     *
     * @return The list children currently attached to this node
     */
    @NotNull List<? extends ConfigurationNode> getChildrenList();

    /**
     * Gets the "map children" attached to this node, if it has any.
     *
     * <p>If this node does not {@link #hasMapChildren() have map children}, an empty map
     * returned.</p>
     *
     * @return The map children currently attached to this node
     */
    @NotNull Map<Object, ? extends ConfigurationNode> getChildrenMap();

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a List
     * or a Map.</p>
     *
     * @return This configuration's current value, or null if there is none
     * @see #getValue(Object)
     */
    @Nullable
    default Object getValue() {
        return getValue((Object) null);
    }

    /**
     * Set this node's value to the given value.
     *
     * <p>If the provided value is a {@link Collection} or a {@link Map}, it will be unwrapped into
     * the appropriate configuration node structure.</p>
     *
     * @param value The value to set
     * @return this
     */
    @NotNull ConfigurationNode setValue(@Nullable Object value);

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a List
     * or a Map.</p>
     *
     * @param def The default value to return if this node has no set value
     * @return This configuration's current value, or {@code def} if there is none
     */
    Object getValue(@Nullable Object def);

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a List
     * or a Map.</p>
     *
     * @param defSupplier The function that will be called to calculate a default value only if
     *                    there is no existing value
     * @return This configuration's current value, or {@code def} if there is none
     */
    Object getValue(@NotNull Supplier<Object> defSupplier);

    /**
     * Gets the appropriately transformed typed version of this node's value from the provided
     * transformation function.
     *
     * @param transformer The transformation function
     * @param <T>         The expected type
     * @return A transformed value of the correct type, or null either if no value is present or the
     * value could not be converted
     */
    @Nullable
    default <T> T getValue(@NotNull Function<Object, T> transformer) {
        return getValue(transformer, (T) null);
    }

    /**
     * Gets the appropriately transformed typed version of this node's value from the provided
     * transformation function.
     *
     * @param transformer The transformation function
     * @param def         The default value to return if this node has no set value or is not of a
     *                    convertible type
     * @param <T>         The expected type
     * @return A transformed value of the correct type, or {@code def} either if no value is present
     * or the value could not be converted
     */
    <T> T getValue(@NotNull Function<Object, T> transformer, @Nullable T def);

    /**
     * Gets the appropriately transformed typed version of this node's value from the provided
     * transformation function.
     *
     * @param transformer The transformation function
     * @param defSupplier The function that will be called to calculate a default value only if
     *                    there is no existing value of the correct type
     * @param <T>         The expected type
     * @return A transformed value of the correct type, or {@code def} either if no value is present
     * or the value could not be converted
     */
    <T> T getValue(@NotNull Function<Object, T> transformer, @NotNull Supplier<T> defSupplier);

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value</p>
     *
     * @param transformer The transformation function
     * @param <T>         The expected type
     * @return An immutable copy of the values contained
     */
    @NotNull <T> List<T> getList(@NotNull Function<Object, T> transformer);

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value.</p>
     *
     * @param transformer The transformation function
     * @param def         The default value if no appropriate value is set
     * @param <T>         The expected type
     * @return An immutable copy of the values contained that could be successfully converted, or {@code def} if no
     * values could be converted
     */
    <T> List<T> getList(@NotNull Function<Object, T> transformer, @Nullable List<T> def);

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value.</p>
     *
     * @param transformer The transformation function
     * @param defSupplier The function that will be called to calculate a default value only if there is no existing
     *                    value of the correct type
     * @param <T>         The expected type
     * @return An immutable copy of the values contained that could be successfully converted, or {@code def} if no
     * values could be converted
     */
    <T> List<T> getList(@NotNull Function<Object, T> transformer, @NotNull Supplier<List<T>> defSupplier);

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value.</p>
     *
     * @param type The expected type
     * @param <T>  The expected type
     * @return An immutable copy of the values contained
     */
    @NotNull
    default <T> List<T> getList(@NotNull TypeToken<List<T>> type) throws ObjectMappingException {
        return getList(type, ImmutableList.of());
    }

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value.</p>
     *
     * @param type The expected type
     * @param def  The default value if no appropriate value is set
     * @param <T>  The expected type
     * @return An immutable copy of the values contained that could be successfully converted, or {@code def} if no
     * values could be converted
     */

    <T> List<T> getList(@NotNull TypeToken<List<T>> type, @Nullable List<T> def) throws ObjectMappingException;

    /**
     * If this node has list values, this function unwraps them and converts them to an appropriate
     * type based on the provided function.
     *
     * <p>If this node has a scalar value, this function treats it as a list with one value.</p>
     *
     * @param type        The expected type
     * @param defSupplier The function that will be called to calculate a default value only if there is no existing
     *                    value of the correct type
     * @param <T>         The expected type
     * @return An immutable copy of the values contained that could be successfully converted, or {@code def} if no
     * values could be converted
     */
    <T> List<T> getList(@NotNull TypeToken<List<T>> type, @NotNull Supplier<List<T>> defSupplier) throws ObjectMappingException;

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, null if no appropriate value is available
     * @see #getValue()
     */
    @Nullable
    default String getString() {
        return getString(null);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default String getString(@Nullable String def) {
        return getValue(Types::asString, def);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, 0 if no appropriate value is available
     * @see #getValue()
     */
    default float getFloat() {
        return getFloat(NUMBER_DEF);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default float getFloat(float def) {
        return getValue(Types::asFloat, def);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, 0 if no appropriate value is available
     * @see #getValue()
     */
    default double getDouble() {
        return getDouble(NUMBER_DEF);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default double getDouble(double def) {
        return getValue(Types::asDouble, def);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, 0 if no appropriate value is available
     * @see #getValue()
     */
    default int getInt() {
        return getInt(NUMBER_DEF);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default int getInt(int def) {
        return getValue(Types::asInt, def);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, 0 if no appropriate value is available
     * @see #getValue()
     */
    default long getLong() {
        return getLong(NUMBER_DEF);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default long getLong(long def) {
        return getValue(Types::asLong, def);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @return The appropriate type conversion, false if no appropriate value is available
     * @see #getValue()
     */
    default boolean getBoolean() {
        return getBoolean(false);
    }

    /**
     * Gets the value typed using the appropriate type conversion from {@link Types}
     *
     * @param def The default value if no appropriate value is set
     * @return The appropriate type conversion, {@code def} if no appropriate value is available
     * @see #getValue()
     */
    default boolean getBoolean(boolean def) {
        return getValue(Types::asBoolean, def);
    }

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a
     * List or a Map.</p>
     *
     * <p>This method will also perform deserialization using the appropriate TypeSerializer for
     * the given type, or casting if no type serializer is found.</p>
     *
     * @param type The type to deserialize to
     * @param <T>  the type to get
     * @return the value if present and of the proper type, else null
     */
    @Nullable
    default <T> T getValue(@NotNull TypeToken<T> type) throws ObjectMappingException {
        return getValue(type, (T) null);
    }

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a
     * List or a Map.</p>
     *
     * <p>This method will also perform deserialization using the appropriate TypeSerializer for
     * the given type, or casting if no type serializer is found.</p>
     *
     * @param type The type to deserialize to
     * @param def  The value to return if no value or value is not of appropriate type
     * @param <T>  the type to get
     * @return the value if of the proper type, else {@code def}
     */
    <T> T getValue(@NotNull TypeToken<T> type, T def) throws ObjectMappingException;

    /**
     * Get the current value associated with this node.
     *
     * <p>If this node has children, this method will recursively unwrap them to construct a
     * List or a Map.</p>
     *
     * <p>This method will also perform deserialization using the appropriate TypeSerializer for
     * the given type, or casting if no type serializer is found.</p>
     *
     * @param type        The type to deserialize to
     * @param defSupplier The function that will be called to calculate a default value only if there is no existing
     *                    value of the correct type
     * @param <T>         the type to get
     * @return the value if of the proper type, else {@code def}
     */
    <T> T getValue(@NotNull TypeToken<T> type, @NotNull Supplier<T> defSupplier) throws ObjectMappingException;

    /**
     * Set this node's value to the given value.
     *
     * <p>If the provided value is a {@link Collection} or a {@link Map}, it will be unwrapped into
     * the appropriate configuration node structure.</p>
     *
     * <p>This method will also perform serialization using the appropriate TypeSerializer for the
     * given type, or casting if no type serializer is found.</p>
     *
     * @param type  The type to use for serialization type information
     * @param value The value to set
     * @param <T>   The type to serialize to
     * @return this
     */
    @NotNull
    default <T> ConfigurationNode setValue(@NotNull TypeToken<T> type, @Nullable T value) throws ObjectMappingException {
        if (value == null) {
            setValue(null);
            return this;
        }

        TypeSerializer<T> serial = getOptions().getSerializers().get(type);

        if (serial != null) {
            serial.serialize(type, value, this);
        } else if (getOptions().acceptsType(value.getClass())) {
            setValue(value); // Just write if no applicable serializer exists?
        } else {
            throw new ObjectMappingException("No serializer available for type " + type);
        }

        return this;
    }

    /**
     * Set all the values from the given node that are not present in this node
     * to their values in the provided node.
     *
     * <p>Map keys will be merged. Lists and scalar values will be replaced.</p>
     *
     * @param other The node to merge values from
     * @return this
     */
    @NotNull ConfigurationNode mergeValuesFrom(@NotNull ConfigurationNode other);

    /**
     * Removes a direct child of this node
     *
     * @param key The key of the node to remove
     * @return If a node was removed
     */
    boolean removeChild(@NotNull Object key);

    /**
     * Gets a new child node created as the next entry in the list.
     *
     * @return A new child created as the next entry in the list when it is attached
     */
    @NotNull ConfigurationNode getAppendedNode();

    /**
     * Creates a deep copy of this node.
     *
     * <p>If this node has child nodes (is a list or map), the child nodes will
     * also be copied. This action is performed recursively.</p>
     *
     * <p>The resultant node will (initially) contain the same value(s) as this node,
     * and will therefore be {@link Object#equals(Object) equal}, however, changes made to
     * the original will not be reflected in the copy, and vice versa.</p>
     *
     * <p>The actual scalar values that back the configuration will
     * <strong>not</strong> be copied - only the node structure that forms the
     * configuration. This is not a problem in most cases, as the scalar values
     * stored in configurations are usually immutable. (e.g. strings, numbers, booleans).</p>
     *
     * @return A copy of this node
     */
    @NotNull ConfigurationNode copy();

}
