package viaversion.viaversion.api.platform;

import io.netty.channel.ChannelFutureListener;
import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles injected UserConnections
 */
public class ViaConnectionManager {
    protected final Map<UUID, UserConnection> clients = new ConcurrentHashMap<>();
    protected final Set<UserConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void onLoginSuccess(UserConnection connection) {
        Objects.requireNonNull(connection, "connection is null!");
        connections.add(connection);

        if (isFrontEnd(connection)) {
            UUID id = connection.getProtocolInfo().getUuid();
            if (clients.put(id, connection) != null) {
                Via.getPlatform().getLogger().warning("Duplicate UUID on frontend connection! ("+id+")");
            }
        }

        if (connection.getChannel() != null) {
            connection.getChannel().closeFuture().addListener((ChannelFutureListener) future -> onDisconnect(connection));
        }
    }

    public void onDisconnect(UserConnection connection) {
        Objects.requireNonNull(connection, "connection is null!");
        connections.remove(connection);

        if (isFrontEnd(connection)) {
            UUID id = connection.getProtocolInfo().getUuid();
            clients.remove(id);
        }
    }

    /**
     * Frontend connections will have the UUID stored. Override this if your platform isn't always frontend.
     * UUIDs can't be duplicate between frontend connections.
     */
    public boolean isFrontEnd(UserConnection conn) {
        return !conn.isClientSide();
    }

    /**
     * Returns a map containing the UUIDs and frontend UserConnections from players connected to this proxy server
     * Returns empty list when there isn't a server
     * When ViaVersion is reloaded, this method may not return some players.
     * May not contain ProtocolSupport players.
     */
    public Map<UUID, UserConnection> getConnectedClients() {
        return Collections.unmodifiableMap(clients);
    }

    /**
     * Returns the frontend UserConnection from the player connected to this proxy server
     * Returns null when there isn't a server or connection was not found
     * When ViaVersion is reloaded, this method may not return some players.
     * May not return ProtocolSupport players.
     * <p>
     * Note that connections are removed as soon as their channel is closed,
     * so avoid using this method during player quits for example.
     */
    @Nullable
    public UserConnection getConnectedClient(UUID clientIdentifier) {
        return clients.get(clientIdentifier);
    }

    /**
     * Returns the UUID from the frontend connection to this proxy server
     * Returns null when there isn't a server or this connection isn't frontend or it doesn't have an id
     * When ViaVersion is reloaded, this method may not return some players.
     * May not return ProtocolSupport players.
     * <p>
     * Note that connections are removed as soon as their channel is closed,
     * so avoid using this method during player quits for example.
     */
    @Nullable
    public UUID getConnectedClientId(UserConnection conn) {
        if (conn.getProtocolInfo() == null) return null;
        UUID uuid = conn.getProtocolInfo().getUuid();
        UserConnection client = clients.get(uuid);
        if (client != null && client.equals(conn)) {
            // This is frontend
            return uuid;
        }
        return null;
    }

    /**
     * Returns all UserConnections which are registered
     * May contain duplicated UUIDs on multiple ProtocolInfo.
     * May contain frontend, backend and/or client-sided connections.
     * When ViaVersion is reloaded, this method may not return some players.
     * May not contain ProtocolSupport players.
     */
    public Set<UserConnection> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    /**
     * Returns if Via injected into this player connection.
     *
     * @param playerId player uuid
     * @return true if the player is handled by Via
     */
    public boolean isClientConnected(UUID playerId) {
        return clients.containsKey(playerId);
    }
}
