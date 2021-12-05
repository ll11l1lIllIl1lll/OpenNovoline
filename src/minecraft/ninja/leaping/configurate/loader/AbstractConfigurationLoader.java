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

package ninja.leaping.configurate.loader;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.io.*;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for many stream-based configuration loaders. This class provides conversion from a variety of input
 * sources to CharSource/Sink objects, providing a consistent API for loaders to read from and write to.
 * <p>
 * Either the source or sink may be null. If this is true, this loader may not support either loading or saving. In
 * this case, implementing classes are expected to throw an IOException.
 *
 * @param <NodeType> The {@link ninja.leaping.configurate.ConfigurationNode} type produced by the loader
 */
public abstract class AbstractConfigurationLoader<NodeType extends ConfigurationNode> implements ConfigurationLoader<NodeType> {

    /**
     * The escape sequence used by Configurate to separate comment lines
     */
    public static final String CONFIGURATE_LINE_SEPARATOR = "\n";

    /**
     * A {@link Splitter} for splitting comment lines
     */
    protected static final Splitter LINE_SPLITTER = Splitter.on(CONFIGURATE_LINE_SEPARATOR);

    /**
     * The line separator used by the system
     *
     * @see System#lineSeparator()
     */
    protected static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();


    /**
     * The reader source for this loader.
     *
     * <p>Can be null (for loaders which don't support loading!)</p>
     */
    @Nullable
    protected final Callable<BufferedReader> source;

    /**
     * The writer sink for this loader.
     *
     * <p>Can be null (for loaders which don't support saving!)</p>
     */
    @Nullable
    protected final Callable<BufferedWriter> sink;

    /**
     * The comment handlers defined for this loader
     */
    @NotNull
    private final CommentHandler[] commentHandlers;

    /**
     * The mode used to read/write configuration headers
     */
    @NotNull
    private final HeaderMode headerMode;

    /**
     * The default {@link ninja.leaping.configurate.ConfigurationOptions} used by this loader.
     */
    @NotNull
    private final ConfigurationOptions defaultOptions;

    protected AbstractConfigurationLoader(@NotNull Builder<?> builder, @NotNull CommentHandler[] commentHandlers) {
        this.source = builder.getSource();
        this.sink = builder.getSink();
        this.headerMode = builder.getHeaderMode();
        this.commentHandlers = commentHandlers;
        this.defaultOptions = builder.getDefaultOptions();
    }

    /**
     * Gets the primary {@link CommentHandler} used by this loader.
     *
     * @return The default comment handler
     */
    @NotNull
    public CommentHandler getDefaultCommentHandler() {
        return commentHandlers[0];
    }

    @NotNull
    @Override
    public NodeType load(@NotNull ConfigurationOptions options) throws IOException {
        if (source == null) {
            throw new IOException("No source present to read from!");
        }

        try (BufferedReader reader = source.call()) {
            if (headerMode == HeaderMode.PRESERVE || headerMode == HeaderMode.NONE) {
                String comment = CommentHandlers.extractComment(reader, commentHandlers);

                if (comment != null && comment.length() > 0) {
                    options = options.setHeader(comment);
                }
            }

            NodeType node = createEmptyNode(options);

            loadInternal(node, reader);
            return node;
        } catch (FileNotFoundException | NoSuchFileException e) {
            // Squash -- there's nothing to read
            return createEmptyNode(options);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e);
            }
        }
    }

    protected abstract void loadInternal(NodeType node, BufferedReader reader) throws IOException;

    @Override
    public void save(@NotNull ConfigurationNode node) throws IOException {
        if (sink == null) {
            throw new IOException("No sink present to write to!");
        }

        try (Writer writer = sink.call()) {
            writeHeaderInternal(writer);

            if (headerMode != HeaderMode.NONE) {
                String header = node.getOptions().getHeader();

                if (header != null && !header.isEmpty()) {
                    for (String line : getDefaultCommentHandler().toComment(ImmutableList.copyOf(LINE_SPLITTER.split(header)))) {
                        writer.write(line);
                        writer.write(SYSTEM_LINE_SEPARATOR);
                    }

                    writer.write(SYSTEM_LINE_SEPARATOR);
                }
            }

            saveInternal(node, writer);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e);
            }
        }
    }

    protected void writeHeaderInternal(Writer writer) throws IOException {
    }

    protected abstract void saveInternal(ConfigurationNode node, Writer writer) throws IOException;

    @NotNull
    @Override
    public ConfigurationOptions getDefaultOptions() {
        return defaultOptions;
    }

    @Override
    public final boolean canLoad() {
        return source != null;
    }

    @Override
    public final boolean canSave() {
        return sink != null;
    }

    /**
     * An abstract builder implementation for {@link AbstractConfigurationLoader}s.
     *
     * @param <T> The builders own type (for chaining using generic types)
     */
    @SuppressWarnings("rawtypes")
    protected abstract static class Builder<T extends Builder> {

        @NotNull
        protected HeaderMode headerMode = HeaderMode.PRESERVE;
        @Nullable
        protected Callable<BufferedReader> source;
        @Nullable
        protected Callable<BufferedWriter> sink;
        @NotNull
        protected ConfigurationOptions defaultOptions = ConfigurationOptions.defaults();

        protected Builder() {
        }

        @SuppressWarnings("unchecked")
        @NotNull
        private T self() {
            return (T) this;
        }

        /**
         * Sets the sink and source of the resultant loader to the given file.
         *
         * <p>The {@link #getSource() source} is defined using
         * {@link Files#newBufferedReader(Path)} with UTF-8 encoding.</p>
         *
         * <p>The {@link #getSink() sink} is defined using {@link AtomicFiles} with UTF-8
         * encoding.</p>
         *
         * @param file The configuration file
         * @return This builder (for chaining)
         */
        @NotNull
        public T setFile(@NotNull File file) {
            return setPath(Objects.requireNonNull(file, "file").toPath());
        }

        /**
         * Sets the sink and source of the resultant loader to the given path.
         *
         * <p>The {@link #getSource() source} is defined using
         * {@link Files#newBufferedReader(Path)} with UTF-8 encoding.</p>
         *
         * <p>The {@link #getSink() sink} is defined using {@link AtomicFiles} with UTF-8
         * encoding.</p>
         *
         * @param path The path of the configuration file
         * @return This builder (for chaining)
         */
        @NotNull
        public T setPath(@NotNull Path path) {
            Path absPath = Objects.requireNonNull(path, "path").toAbsolutePath();
            this.source = () -> Files.newBufferedReader(absPath, UTF_8);
            this.sink = AtomicFiles.createAtomicWriterFactory(absPath, UTF_8);
            return self();
        }

        /**
         * Sets the source of the resultant loader to the given URL.
         *
         * @param url The URL of the source
         * @return This builder (for chaining)
         */
        @NotNull
        public T setURL(@NotNull URL url) {
            Objects.requireNonNull(url, "url");
            this.source = () -> new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), UTF_8));
            return self();
        }

        /**
         * Gets the source to be used by the resultant loader.
         *
         * @return The source
         */
        @Nullable
        public Callable<BufferedReader> getSource() {
            return source;
        }

        /**
         * Sets the source of the resultant loader.
         *
         * <p>The "source" is used by the loader to load the configuration.</p>
         *
         * @param source The source
         * @return This builder (for chaining)
         */
        @NotNull
        public T setSource(@Nullable Callable<BufferedReader> source) {
            this.source = source;
            return self();
        }

        /**
         * Gets the sink to be used by the resultant loader.
         *
         * @return The sink
         */
        @Nullable
        public Callable<BufferedWriter> getSink() {
            return sink;
        }

        /**
         * Sets the sink of the resultant loader.
         *
         * <p>The "sink" is used by the loader to save the configuration.</p>
         *
         * @param sink The sink
         * @return This builder (for chaining)
         */
        @NotNull
        public T setSink(@Nullable Callable<BufferedWriter> sink) {
            this.sink = sink;
            return self();
        }

        /**
         * Gets the header mode to be used by the resultant loader.
         *
         * @return The header mode
         */
        @NotNull
        public HeaderMode getHeaderMode() {
            return headerMode;
        }

        /**
         * Sets the header mode of the resultant loader.
         *
         * @param mode The header mode
         * @return This builder (for chaining)
         */
        @NotNull
        public T setHeaderMode(@NotNull HeaderMode mode) {
            this.headerMode = Objects.requireNonNull(mode, "mode");
            return self();
        }

        /**
         * Sets if the header of the configuration should be preserved.
         *
         * <p>See {@link HeaderMode#PRESERVE} and {@link HeaderMode#PRESET}.</p>
         *
         * @param preservesHeader If the header should be preserved
         * @return this builder (for chaining)
         * @deprecated In favour of {@link #setHeaderMode(HeaderMode)}
         */
        @NotNull
        @Deprecated
        public T setPreservesHeader(boolean preservesHeader) {
            this.headerMode = preservesHeader ? HeaderMode.PRESERVE : HeaderMode.PRESET;
            return self();
        }

        /**
         * Gets if the header of the configuration should be preserved.
         *
         * @return If the header should be preserved
         * @deprecated In favour of {@link #getHeaderMode()}
         */
        @Deprecated
        public boolean preservesHeader() {
            return headerMode == HeaderMode.PRESERVE;
        }

        /**
         * Gets the default configuration options to be used by the resultant loader.
         *
         * @return The options
         */
        @NotNull
        public ConfigurationOptions getDefaultOptions() {
            return defaultOptions;
        }

        /**
         * Sets the default configuration options to be used by the resultant loader.
         *
         * @param defaultOptions The options
         * @return This builder (for chaining)
         */
        @NotNull
        public T setDefaultOptions(@NotNull ConfigurationOptions defaultOptions) {
            this.defaultOptions = Objects.requireNonNull(defaultOptions, "defaultOptions");
            return self();
        }

        /**
         * Builds the loader.
         *
         * @return The loader
         */
        @NotNull
        public abstract AbstractConfigurationLoader build();

    }

}
