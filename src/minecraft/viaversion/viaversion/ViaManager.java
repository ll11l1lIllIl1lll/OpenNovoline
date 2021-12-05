package viaversion.viaversion;

import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.platform.TaskId;
import viaversion.viaversion.api.platform.ViaConnectionManager;
import viaversion.viaversion.api.platform.ViaInjector;
import viaversion.viaversion.api.platform.ViaPlatform;
import viaversion.viaversion.api.platform.ViaPlatformLoader;
import viaversion.viaversion.api.platform.providers.ViaProviders;
import viaversion.viaversion.api.protocol.ProtocolRegistry;
import viaversion.viaversion.api.protocol.ProtocolVersion;
import viaversion.viaversion.commands.ViaCommandHandler;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.TabCompleteThread;
import viaversion.viaversion.protocols.protocol1_9to1_8.ViaIdleThread;
import viaversion.viaversion.update.UpdateUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ViaManager {
    private final ViaPlatform<?> platform;
    private final ViaProviders providers = new ViaProviders();
    // Internals
    private final ViaInjector injector;
    private final ViaCommandHandler commandHandler;
    private final ViaPlatformLoader loader;
    private final Set<String> subPlatforms = new HashSet<>();
    private List<Runnable> enableListeners = new ArrayList<>();
    private TaskId mappingLoadingTask;
    private boolean debug;

    public ViaManager(ViaPlatform<?> platform, ViaInjector injector, ViaCommandHandler commandHandler, ViaPlatformLoader loader) {
        this.platform = platform;
        this.injector = injector;
        this.commandHandler = commandHandler;
        this.loader = loader;
    }

    public static ViaManagerBuilder builder() {
        return new ViaManagerBuilder();
    }

    public void init() {
        if (System.getProperty("ViaVersion") != null) {
            // Reload?
            platform.onReload();
        }
        // Check for updates
        if (platform.getConf().isCheckForUpdates()) {
            UpdateUtil.sendUpdateMessage();
        }

        // Force class load
        ProtocolRegistry.init();

        // Inject
        try {
            injector.inject();
        } catch (Exception e) {
            platform.getLogger().severe("ViaVersion failed to inject:");
            e.printStackTrace();
            return;
        }

        // Mark as injected
        System.setProperty("ViaVersion", platform.getPluginVersion());

        for (Runnable listener : enableListeners) {
            listener.run();
        }
        enableListeners = null;

        // If successful
        platform.runSync(this::onServerLoaded);
    }

    public void onServerLoaded() {
        // Load Server Protocol
        try {
            ProtocolRegistry.SERVER_PROTOCOL = ProtocolVersion.getProtocol(injector.getServerProtocolVersion()).getVersion();
        } catch (Exception e) {
            platform.getLogger().severe("ViaVersion failed to get the server protocol!");
            e.printStackTrace();
        }
        // Check if there are any pipes to this version
        if (ProtocolRegistry.SERVER_PROTOCOL != -1) {
            platform.getLogger().info("ViaVersion detected server version: " + ProtocolVersion.getProtocol(ProtocolRegistry.SERVER_PROTOCOL));
            if (!ProtocolRegistry.isWorkingPipe() && !platform.isProxy()) {
                platform.getLogger().warning("ViaVersion does not have any compatible versions for this server version!");
                platform.getLogger().warning("Please remember that ViaVersion only adds support for versions newer than the server version.");
                platform.getLogger().warning("If you need support for older versions you may need to use one or more ViaVersion addons too.");
                platform.getLogger().warning("In that case please read the ViaVersion resource page carefully or use https://jo0001.github.io/ViaSetup");
                platform.getLogger().warning("and if you're still unsure, feel free to join our Discord-Server for further assistance.");
            } else if (ProtocolRegistry.SERVER_PROTOCOL == ProtocolVersion.v1_8.getVersion() && !platform.isProxy()) {
                platform.getLogger().warning("This version of Minecraft is over half a decade old and support for it will be fully dropped eventually. "
                        + "Please upgrade to a newer version to avoid encountering bugs and stability issues that have long been fixed.");
            }
        }
        // Load Listeners / Tasks
        ProtocolRegistry.onServerLoaded();

        // Load Platform
        loader.load();
        // Common tasks
        mappingLoadingTask = Via.getPlatform().runRepeatingSync(() -> {
            if (ProtocolRegistry.checkForMappingCompletion()) {
                platform.cancelTask(mappingLoadingTask);
                mappingLoadingTask = null;
            }
        }, 10L);
        if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion()) {
            if (Via.getConfig().isSimulatePlayerTick()) {
                Via.getPlatform().runRepeatingSync(new ViaIdleThread(), 1L);
            }
        }
        if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_13.getVersion()) {
            if (Via.getConfig().get1_13TabCompleteDelay() > 0) {
                Via.getPlatform().runRepeatingSync(new TabCompleteThread(), 1L);
            }
        }

        // Refresh Versions
        ProtocolRegistry.refreshVersions();
    }

    public void destroy() {
        // Uninject
        platform.getLogger().info("ViaVersion is disabling, if this is a reload and you experience issues consider rebooting.");
        try {
            injector.uninject();
        } catch (Exception e) {
            platform.getLogger().severe("ViaVersion failed to uninject:");
            e.printStackTrace();
        }

        // Unload
        loader.unload();
    }

    public Set<UserConnection> getConnections() {
        return platform.getConnectionManager().getConnections();
    }

    /**
     * @deprecated use getConnectedClients()
     */
    @Deprecated
    public Map<UUID, UserConnection> getPortedPlayers() {
        return getConnectedClients();
    }

    public Map<UUID, UserConnection> getConnectedClients() {
        return platform.getConnectionManager().getConnectedClients();
    }

    public UUID getConnectedClientId(UserConnection conn) {
        return platform.getConnectionManager().getConnectedClientId(conn);
    }

    /**
     * @see ViaConnectionManager#isClientConnected(UUID)
     */
    public boolean isClientConnected(UUID player) {
        return platform.getConnectionManager().isClientConnected(player);
    }

    public void handleLoginSuccess(UserConnection info) {
        platform.getConnectionManager().onLoginSuccess(info);
    }

    public ViaPlatform<?> getPlatform() {
        return platform;
    }

    public ViaProviders getProviders() {
        return providers;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public ViaInjector getInjector() {
        return injector;
    }

    public ViaCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public ViaPlatformLoader getLoader() {
        return loader;
    }

    /**
     * Returns a mutable set of self-added subplatform version strings.
     * This set is expanded by the subplatform itself (e.g. ViaBackwards), and may not contain all running ones.
     *
     * @return mutable set of subplatform versions
     */
    public Set<String> getSubPlatforms() {
        return subPlatforms;
    }

    /**
     * @see ViaConnectionManager#getConnectedClient(UUID)
     */
    @Nullable
    public UserConnection getConnection(UUID playerUUID) {
        return platform.getConnectionManager().getConnectedClient(playerUUID);
    }

    /**
     * Adds a runnable to be executed when ViaVersion has finished its init before the full server load.
     *
     * @param runnable runnable to be executed
     */
    public void addEnableListener(Runnable runnable) {
        enableListeners.add(runnable);
    }

    public static final class ViaManagerBuilder {
        private ViaPlatform<?> platform;
        private ViaInjector injector;
        private ViaCommandHandler commandHandler;
        private ViaPlatformLoader loader;

        public ViaManagerBuilder platform(ViaPlatform<?> platform) {
            this.platform = platform;
            return this;
        }

        public ViaManagerBuilder injector(ViaInjector injector) {
            this.injector = injector;
            return this;
        }

        public ViaManagerBuilder loader(ViaPlatformLoader loader) {
            this.loader = loader;
            return this;
        }

        public ViaManagerBuilder commandHandler(ViaCommandHandler commandHandler) {
            this.commandHandler = commandHandler;
            return this;
        }

        public ViaManager build() {
            return new ViaManager(platform, injector, commandHandler, loader);
        }
    }
}
