package viaversion.viaversion.api.platform;

import com.google.gson.JsonObject;
import viaversion.viaversion.api.ViaAPI;
import viaversion.viaversion.api.ViaVersionConfig;
import viaversion.viaversion.api.command.ViaCommandSender;
import viaversion.viaversion.api.configuration.ConfigurationProvider;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.protocols.base.ProtocolInfo;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ViaPlatform represents a platform ViaVersion runs on
 *
 * @param <T> - The player type for the platform, used for API related methods
 */
public interface ViaPlatform<T> {

    /**
     * Get the logger for this platform
     *
     * @return Java Logger (may be a wrapper)
     */
    Logger getLogger();

    /**
     * Get the platform name
     *
     * @return Platform Name (simply its name)
     */
    String getPlatformName();

    /**
     * Get the platform version
     *
     * @return Platform version
     */
    String getPlatformVersion();

    /**
     * Returns true if the server Via is running on is a proxy server.
     *
     * @return true if the platform is a proxy
     */
    default boolean isProxy() {
        return false;
    }

    /**
     * Get the plugin version
     *
     * @return Plugin version as a semver string
     */
    String getPluginVersion();

    /**
     * Run a task Async
     *
     * @param runnable The task to run
     * @return The Task ID
     */
    TaskId runAsync(Runnable runnable);

    /**
     * Run a task Sync
     *
     * @param runnable The task to run
     * @return The Task ID
     */
    TaskId runSync(Runnable runnable);

    /**
     * Run a task Sync after a interval
     * This must be only used after plugin enable.
     *
     * @param runnable The task to run
     * @param ticks    The interval to run it after
     * @return The Task ID
     */
    TaskId runSync(Runnable runnable, Long ticks);

    /**
     * Run a task at a repeating interval.
     * Initial interval is the same as repeat.
     *
     * @param runnable The task to run
     * @param ticks    The interval to run it at
     * @return The Task ID
     */
    TaskId runRepeatingSync(Runnable runnable, Long ticks);

    /**
     * Cancel a task
     *
     * @param taskId The task ID to cancel
     */
    void cancelTask(TaskId taskId);

    /**
     * Get the online players
     *
     * @return Array of ViaCommandSender
     */
    ViaCommandSender[] getOnlinePlayers();

    /**
     * Send a message to a player
     *
     * @param uuid    The player's UUID
     * @param message The message to send
     */
    void sendMessage(UUID uuid, String message);

    /**
     * Kick a player for a reason
     *
     * @param uuid    The player's UUID
     * @param message The message to kick them with
     * @return True if it was successful
     */
    boolean kickPlayer(UUID uuid, String message);

    /**
     * Disconnects an UserConnection for a reason
     *
     * @param connection    The UserConnection
     * @param message The message to kick them with
     * @return True if it was successful
     */
    default boolean disconnect(UserConnection connection, String message) {
        if (connection.isClientSide()) return false;
        UUID uuid = connection.get(ProtocolInfo.class).getUuid();
        if (uuid == null) return false;
        return kickPlayer(uuid, message);
    }

    /**
     * Check if the plugin is enabled.
     *
     * @return True if it is enabled
     */
    boolean isPluginEnabled();

    /**
     * Get the API for this platform
     *
     * @return The API for the platform
     */
    ViaAPI<T> getApi();

    /**
     * Get the config API for this platform
     *
     * @return The config API
     */
    ViaVersionConfig getConf();

    /**
     * Get the backend configuration provider for this platform.
     * (On some platforms this returns the same as getConf())
     *
     * @return The configuration provider
     */
    ConfigurationProvider getConfigurationProvider();

    /**
     * Get ViaVersions's data folder.
     *
     * @return data folder
     */
    File getDataFolder();

    /**
     * Called when a reload happens
     */
    void onReload();

    /**
     * Get the JSON data required for /viaversion dump
     *
     * @return The json data
     */
    JsonObject getDump();

    /**
     * Get if older clients are allowed to be used using ViaVersion.
     * (Only 1.9 on 1.9.2 server is supported by ViaVersion alone)
     *
     * @return True if allowed
     */
    boolean isOldClientsAllowed();

    ViaConnectionManager getConnectionManager();
}
