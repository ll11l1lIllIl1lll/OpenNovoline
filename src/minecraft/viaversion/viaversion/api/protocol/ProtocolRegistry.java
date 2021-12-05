package viaversion.viaversion.api.protocol;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.Pair;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.MappingDataLoader;
import viaversion.viaversion.protocols.base.BaseProtocol;
import viaversion.viaversion.protocols.base.BaseProtocol1_16;
import viaversion.viaversion.protocols.base.BaseProtocol1_7;
import viaversion.viaversion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4;
import viaversion.viaversion.protocols.protocol1_11_1to1_11.Protocol1_11_1To1_11;
import viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.Protocol1_12_1To1_12;
import viaversion.viaversion.protocols.protocol1_12_2to1_12_1.Protocol1_12_2To1_12_1;
import viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import viaversion.viaversion.protocols.protocol1_13_2to1_13_1.Protocol1_13_2To1_13_1;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import viaversion.viaversion.protocols.protocol1_14_2to1_14_1.Protocol1_14_2To1_14_1;
import viaversion.viaversion.protocols.protocol1_14_3to1_14_2.Protocol1_14_3To1_14_2;
import viaversion.viaversion.protocols.protocol1_14_4to1_14_3.Protocol1_14_4To1_14_3;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import viaversion.viaversion.protocols.protocol1_15_1to1_15.Protocol1_15_1To1_15;
import viaversion.viaversion.protocols.protocol1_15_2to1_15_1.Protocol1_15_2To1_15_1;
import viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import viaversion.viaversion.protocols.protocol1_16_1to1_16.Protocol1_16_1To1_16;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import viaversion.viaversion.protocols.protocol1_16_3to1_16_2.Protocol1_16_3To1_16_2;
import viaversion.viaversion.protocols.protocol1_16_4to1_16_3.Protocol1_16_4To1_16_3;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.Protocol1_9_1_2To1_9_3_4;
import viaversion.viaversion.protocols.protocol1_9_1to1_9.Protocol1_9_1To1_9;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.Protocol1_9_3To1_9_1_2;
import viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_9_1.Protocol1_9To1_9_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProtocolRegistry {
    public static final Protocol BASE_PROTOCOL = new BaseProtocol();
    public static int SERVER_PROTOCOL = -1;
    public static int maxProtocolPathSize = 50;
    // Input Version -> Output Version & Protocol (Allows fast lookup)
    private static final Int2ObjectMap<Int2ObjectMap<Protocol>> registryMap = new Int2ObjectOpenHashMap<>(32);
    private static final Map<Class<? extends Protocol>, Protocol> protocols = new HashMap<>();
    private static final Map<Pair<Integer, Integer>, List<Pair<Integer, Protocol>>> pathCache = new ConcurrentHashMap<>();
    private static final Set<Integer> supportedVersions = new HashSet<>();
    private static final List<Pair<Range<Integer>, Protocol>> baseProtocols = Lists.newCopyOnWriteArrayList();
    private static final List<Protocol> registerList = new ArrayList<>();

    private static final Object MAPPING_LOADER_LOCK = new Object();
    private static Map<Class<? extends Protocol>, CompletableFuture<Void>> mappingLoaderFutures = new HashMap<>();
    private static ThreadPoolExecutor mappingLoaderExecutor;
    private static boolean mappingsLoaded;

    static {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Via-Mappingloader-%d").build();
        mappingLoaderExecutor = new ThreadPoolExecutor(5, 16, 45L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
        mappingLoaderExecutor.allowCoreThreadTimeOut(true);

        // Base Protocol
        registerBaseProtocol(BASE_PROTOCOL, Range.lessThan(Integer.MIN_VALUE));
        registerBaseProtocol(new BaseProtocol1_7(), Range.lessThan(ProtocolVersion.v1_16.getVersion()));
        registerBaseProtocol(new BaseProtocol1_16(), Range.atLeast(ProtocolVersion.v1_16.getVersion()));

        registerProtocol(new Protocol1_9To1_8(), ProtocolVersion.v1_9, ProtocolVersion.v1_8);
        registerProtocol(new Protocol1_9_1To1_9(), Arrays.asList(ProtocolVersion.v1_9_1.getVersion(), ProtocolVersion.v1_9_2.getVersion()), ProtocolVersion.v1_9.getVersion());
        registerProtocol(new Protocol1_9_3To1_9_1_2(), ProtocolVersion.v1_9_3, ProtocolVersion.v1_9_2);

        registerProtocol(new Protocol1_9To1_9_1(), ProtocolVersion.v1_9, ProtocolVersion.v1_9_2);
        registerProtocol(new Protocol1_9_1_2To1_9_3_4(), Arrays.asList(ProtocolVersion.v1_9_1.getVersion(), ProtocolVersion.v1_9_2.getVersion()), ProtocolVersion.v1_9_3.getVersion());
        registerProtocol(new Protocol1_10To1_9_3_4(), ProtocolVersion.v1_10, ProtocolVersion.v1_9_3);

        registerProtocol(new Protocol1_11To1_10(), ProtocolVersion.v1_11, ProtocolVersion.v1_10);
        registerProtocol(new Protocol1_11_1To1_11(), ProtocolVersion.v1_11_1, ProtocolVersion.v1_11);

        registerProtocol(new Protocol1_12To1_11_1(), ProtocolVersion.v1_12, ProtocolVersion.v1_11_1);
        registerProtocol(new Protocol1_12_1To1_12(), ProtocolVersion.v1_12_1, ProtocolVersion.v1_12);
        registerProtocol(new Protocol1_12_2To1_12_1(), ProtocolVersion.v1_12_2, ProtocolVersion.v1_12_1);

        registerProtocol(new Protocol1_13To1_12_2(), ProtocolVersion.v1_13, ProtocolVersion.v1_12_2);
        registerProtocol(new Protocol1_13_1To1_13(), ProtocolVersion.v1_13_1, ProtocolVersion.v1_13);
        registerProtocol(new Protocol1_13_2To1_13_1(), ProtocolVersion.v1_13_2, ProtocolVersion.v1_13_1);

        registerProtocol(new Protocol1_14To1_13_2(), ProtocolVersion.v1_14, ProtocolVersion.v1_13_2);
        registerProtocol(new Protocol1_14_1To1_14(), ProtocolVersion.v1_14_1, ProtocolVersion.v1_14);
        registerProtocol(new Protocol1_14_2To1_14_1(), ProtocolVersion.v1_14_2, ProtocolVersion.v1_14_1);
        registerProtocol(new Protocol1_14_3To1_14_2(), ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_2);
        registerProtocol(new Protocol1_14_4To1_14_3(), ProtocolVersion.v1_14_4, ProtocolVersion.v1_14_3);

        registerProtocol(new Protocol1_15To1_14_4(), ProtocolVersion.v1_15, ProtocolVersion.v1_14_4);
        registerProtocol(new Protocol1_15_1To1_15(), ProtocolVersion.v1_15_1, ProtocolVersion.v1_15);
        registerProtocol(new Protocol1_15_2To1_15_1(), ProtocolVersion.v1_15_2, ProtocolVersion.v1_15_1);

        registerProtocol(new Protocol1_16To1_15_2(), ProtocolVersion.v1_16, ProtocolVersion.v1_15_2);
        registerProtocol(new Protocol1_16_1To1_16(), ProtocolVersion.v1_16_1, ProtocolVersion.v1_16);
        registerProtocol(new Protocol1_16_2To1_16_1(), ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_1);
        registerProtocol(new Protocol1_16_3To1_16_2(), ProtocolVersion.v1_16_3, ProtocolVersion.v1_16_2);
        registerProtocol(new Protocol1_16_4To1_16_3(), ProtocolVersion.v1_16_4, ProtocolVersion.v1_16_3);
    }

    public static void init() {
        // Empty method to trigger static initializer once
    }

    /**
     * Register a protocol
     *
     * @param protocol  The protocol to register.
     * @param supported Supported client versions.
     * @param output    The output server version it converts to.
     */
    public static void registerProtocol(Protocol protocol, ProtocolVersion supported, ProtocolVersion output) {
        registerProtocol(protocol, Collections.singletonList(supported.getVersion()), output.getVersion());
    }

    /**
     * Register a protocol
     *
     * @param protocol  The protocol to register.
     * @param supported Supported client versions.
     * @param output    The output server version it converts to.
     */
    public static void registerProtocol(Protocol protocol, List<Integer> supported, int output) {
        // Clear cache as this may make new routes.
        if (!pathCache.isEmpty()) {
            pathCache.clear();
        }

        protocols.put(protocol.getClass(), protocol);

        for (int version : supported) {
            Int2ObjectMap<Protocol> protocolMap = registryMap.computeIfAbsent(version, s -> new Int2ObjectOpenHashMap<>(2));
            protocolMap.put(output, protocol);
        }

        if (Via.getPlatform().isPluginEnabled()) {
            protocol.register(Via.getManager().getProviders());
            refreshVersions();
        } else {
            registerList.add(protocol);
        }

        if (protocol.hasMappingDataToLoad()) {
            if (mappingLoaderExecutor != null) {
                // Submit mapping data loading
                addMappingLoaderFuture(protocol.getClass(), protocol::loadMappingData);
            } else {
                // Late protocol adding - just do it on the current thread
                protocol.loadMappingData();
            }
        }
    }

    /**
     * Registers a base protocol.
     * Base Protocols registered later have higher priority
     * Only one base protocol will be added to pipeline
     *
     * @param baseProtocol       Base Protocol to register
     * @param supportedProtocols Versions that baseProtocol supports
     */
    public static void registerBaseProtocol(Protocol baseProtocol, Range<Integer> supportedProtocols) {
        baseProtocols.add(new Pair<>(supportedProtocols, baseProtocol));
        if (Via.getPlatform().isPluginEnabled()) {
            baseProtocol.register(Via.getManager().getProviders());
            refreshVersions();
        } else {
            registerList.add(baseProtocol);
        }
    }

    public static void refreshVersions() {
        supportedVersions.clear();

        supportedVersions.add(ProtocolRegistry.SERVER_PROTOCOL);
        for (ProtocolVersion versions : ProtocolVersion.getProtocols()) {
            List<Pair<Integer, Protocol>> paths = getProtocolPath(versions.getVersion(), ProtocolRegistry.SERVER_PROTOCOL);
            if (paths == null) continue;
            supportedVersions.add(versions.getVersion());
            for (Pair<Integer, Protocol> path : paths) {
                supportedVersions.add(path.getKey());
            }
        }
    }

    /**
     * Get the versions compatible with the server.
     *
     * @return Read-only set of the versions.
     */
    public static SortedSet<Integer> getSupportedVersions() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(supportedVersions));
    }

    /**
     * Check if this plugin is useful to the server.
     *
     * @return True if there is a useful pipe
     */
    public static boolean isWorkingPipe() {
        for (Int2ObjectMap<Protocol> map : registryMap.values()) {
            if (map.containsKey(SERVER_PROTOCOL)) return true;
        }
        return false; // No destination for protocol
    }

    /**
     * Called when the server is enabled, to register any non registered listeners.
     */
    public static void onServerLoaded() {
        for (Protocol protocol : registerList) {
            protocol.register(Via.getManager().getProviders());
        }
        registerList.clear();
    }

    /**
     * Calculate a path to get from an input protocol to the servers protocol.
     *
     * @param current       The current items in the path
     * @param clientVersion The current input version
     * @param serverVersion The desired output version
     * @return The path which has been generated, null if failed.
     */
    @Nullable
    private static List<Pair<Integer, Protocol>> getProtocolPath(List<Pair<Integer, Protocol>> current, int clientVersion, int serverVersion) {
        if (clientVersion == serverVersion) return null; // We're already there
        if (current.size() > maxProtocolPathSize) return null; // Fail safe, protocol too complicated.

        // First check if there is any protocols for this
        Int2ObjectMap<Protocol> inputMap = registryMap.get(clientVersion);
        if (inputMap == null) {
            return null; // Not supported
        }

        // Next check there isn't an obvious path
        Protocol protocol = inputMap.get(serverVersion);
        if (protocol != null) {
            current.add(new Pair<>(serverVersion, protocol));
            return current; // Easy solution
        }

        // There might be a more advanced solution... So we'll see if any of the others can get us there
        List<Pair<Integer, Protocol>> shortest = null;
        for (Int2ObjectMap.Entry<Protocol> entry : inputMap.int2ObjectEntrySet()) {
            // Ensure it wasn't caught by the other loop
            if (entry.getIntKey() == (serverVersion)) continue;

            Pair<Integer, Protocol> pair = new Pair<>(entry.getIntKey(), entry.getValue());
            // Ensure no recursion
            if (current.contains(pair)) continue;

            // Create a copy
            List<Pair<Integer, Protocol>> newCurrent = new ArrayList<>(current);
            newCurrent.add(pair);
            // Calculate the rest of the protocol using the current
            newCurrent = getProtocolPath(newCurrent, entry.getKey(), serverVersion);
            if (newCurrent != null) {
                // If it's shorter then choose it
                if (shortest == null || shortest.size() > newCurrent.size()) {
                    shortest = newCurrent;
                }
            }
        }

        return shortest; // null if none found
    }

    /**
     * Calculate a path from a client version to server version.
     *
     * @param clientVersion The input client version
     * @param serverVersion The desired output server version
     * @return The path it generated, null if it failed.
     */
    @Nullable
    public static List<Pair<Integer, Protocol>> getProtocolPath(int clientVersion, int serverVersion) {
        Pair<Integer, Integer> protocolKey = new Pair<>(clientVersion, serverVersion);
        // Check cache
        List<Pair<Integer, Protocol>> protocolList = pathCache.get(protocolKey);
        if (protocolList != null) {
            return protocolList;
        }
        // Generate path
        List<Pair<Integer, Protocol>> outputPath = getProtocolPath(new ArrayList<>(), clientVersion, serverVersion);
        // If it found a path, cache it.
        if (outputPath != null) {
            pathCache.put(protocolKey, outputPath);
        }
        return outputPath;
    }

    /**
     * Returns a protocol instance by its class.
     *
     * @param protocolClass class of the protocol
     * @return protocol if present
     */
    @Nullable
    public static Protocol getProtocol(Class<? extends Protocol> protocolClass) {
        return protocols.get(protocolClass);
    }

    public static Protocol getBaseProtocol(int serverVersion) {
        for (Pair<Range<Integer>, Protocol> rangeProtocol : Lists.reverse(baseProtocols)) {
            if (rangeProtocol.getKey().contains(serverVersion)) {
                return rangeProtocol.getValue();
            }
        }
        throw new IllegalStateException("No Base Protocol for " + serverVersion);
    }

    public static boolean isBaseProtocol(Protocol protocol) {
        for (Pair<Range<Integer>, Protocol> p : baseProtocols) {
            if (p.getValue() == protocol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensure that mapping data for that protocol has already been loaded, completes it otherwise.
     *
     * @param protocolClass protocol class
     */
    public static void completeMappingDataLoading(Class<? extends Protocol> protocolClass) throws Exception {
        if (mappingsLoaded) return;

        CompletableFuture<Void> future = getMappingLoaderFuture(protocolClass);
        if (future == null) return;

        future.get();
    }

    /**
     * Shuts down the executor and uncaches mappings if all futures have been completed.
     *
     * @return true if the executor has now been shut down
     */
    public static boolean checkForMappingCompletion() {
        synchronized (MAPPING_LOADER_LOCK) {
            if (mappingsLoaded) return false;

            for (CompletableFuture<Void> future : mappingLoaderFutures.values()) {
                // Return if any future hasn't completed yet
                if (!future.isDone()) {
                    return false;
                }
            }

            shutdownLoaderExecutor();
            return true;
        }
    }

    private static void shutdownLoaderExecutor() {
        Via.getPlatform().getLogger().info("Finished mapping loading, shutting down loader executor!");
        mappingsLoaded = true;
        mappingLoaderExecutor.shutdown();
        mappingLoaderExecutor = null;
        mappingLoaderFutures.clear();
        mappingLoaderFutures = null;
        if (MappingDataLoader.isCacheJsonMappings()) {
            MappingDataLoader.getMappingsCache().clear();
        }
    }

    public static void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Runnable runnable) {
        synchronized (MAPPING_LOADER_LOCK) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, mappingLoaderExecutor).exceptionally(throwable -> {
                Via.getPlatform().getLogger().severe("Error during mapping loading of " + protocolClass.getCanonicalName());
                throwable.printStackTrace();
                return null;
            });
            mappingLoaderFutures.put(protocolClass, future);
        }
    }

    public static void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Class<? extends Protocol> dependsOn, Runnable runnable) {
        synchronized (MAPPING_LOADER_LOCK) {
            CompletableFuture<Void> future = getMappingLoaderFuture(dependsOn)
                    .whenCompleteAsync((v, throwable) -> runnable.run(), mappingLoaderExecutor).exceptionally(throwable -> {
                        Via.getPlatform().getLogger().severe("Error during mapping loading of " + protocolClass.getCanonicalName());
                        throwable.printStackTrace();
                        return null;
                    });
            mappingLoaderFutures.put(protocolClass, future);
        }
    }

    @Nullable
    public static CompletableFuture<Void> getMappingLoaderFuture(Class<? extends Protocol> protocolClass) {
        synchronized (MAPPING_LOADER_LOCK) {
            if (mappingsLoaded) return null;
            return mappingLoaderFutures.get(protocolClass);
        }
    }
}
