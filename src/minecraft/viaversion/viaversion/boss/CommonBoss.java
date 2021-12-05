package viaversion.viaversion.boss;

import com.google.common.base.Preconditions;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.boss.BossBar;
import viaversion.viaversion.api.boss.BossColor;
import viaversion.viaversion.api.boss.BossFlag;
import viaversion.viaversion.api.boss.BossStyle;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommonBoss<T> extends BossBar<T> {
    private final UUID uuid;
    private final Set<UserConnection> connections;
    private final Set<BossFlag> flags;
    private String title;
    private float health;
    private BossColor color;
    private BossStyle style;
    private boolean visible;

    public CommonBoss(String title, float health, BossColor color, BossStyle style) {
        Preconditions.checkNotNull(title, "Title cannot be null");
        Preconditions.checkArgument((health >= 0 && health <= 1), "Health must be between 0 and 1");

        this.uuid = UUID.randomUUID();
        this.title = title;
        this.health = health;
        this.color = color == null ? BossColor.PURPLE : color;
        this.style = style == null ? BossStyle.SOLID : style;
        this.connections = Collections.newSetFromMap(new WeakHashMap<>());
        this.flags = new HashSet<>();
        visible = true;
    }

    @Override
    public BossBar setTitle(String title) {
        Preconditions.checkNotNull(title);
        this.title = title;
        sendPacket(UpdateAction.UPDATE_TITLE);
        return this;
    }

    @Override
    public BossBar setHealth(float health) {
        Preconditions.checkArgument((health >= 0 && health <= 1), "Health must be between 0 and 1");
        this.health = health;
        sendPacket(UpdateAction.UPDATE_HEALTH);
        return this;
    }

    @Override
    public BossColor getColor() {
        return color;
    }

    @Override
    public BossBar setColor(BossColor color) {
        Preconditions.checkNotNull(color);
        this.color = color;
        sendPacket(UpdateAction.UPDATE_STYLE);
        return this;
    }

    @Override
    public BossBar setStyle(BossStyle style) {
        Preconditions.checkNotNull(style);
        this.style = style;
        sendPacket(UpdateAction.UPDATE_STYLE);
        return this;
    }

    @Override
    public BossBar addPlayer(UUID player) {
        return addConnection(Via.getManager().getConnection(player));
    }

    @Override
    public BossBar addConnection(UserConnection conn) {
        if (connections.add(conn) && visible) {
            sendPacketConnection(conn, getPacket(UpdateAction.ADD, conn));
        }
        return this;
    }

    @Override
    public BossBar removePlayer(UUID uuid) {
        return removeConnection(Via.getManager().getConnection(uuid));
    }

    @Override
    public BossBar removeConnection(UserConnection conn) {
        if (connections.remove(conn)) {
            sendPacketConnection(conn, getPacket(UpdateAction.REMOVE, conn));
        }
        return this;
    }

    @Override
    public BossBar addFlag(BossFlag flag) {
        Preconditions.checkNotNull(flag);
        if (!hasFlag(flag))
            flags.add(flag);
        sendPacket(UpdateAction.UPDATE_FLAGS);
        return this;
    }

    @Override
    public BossBar removeFlag(BossFlag flag) {
        Preconditions.checkNotNull(flag);
        if (hasFlag(flag))
            flags.remove(flag);
        sendPacket(UpdateAction.UPDATE_FLAGS);
        return this;
    }

    @Override
    public boolean hasFlag(BossFlag flag) {
        Preconditions.checkNotNull(flag);
        return flags.contains(flag);
    }

    @Override
    public Set<UUID> getPlayers() {
        return connections.stream().map(conn -> Via.getManager().getConnectedClientId(conn)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserConnection> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    @Override
    public BossBar show() {
        setVisible(true);
        return this;
    }

    @Override
    public BossBar hide() {
        setVisible(false);
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    private void setVisible(boolean value) {
        if (visible != value) {
            visible = value;
            sendPacket(value ? UpdateAction.ADD : UpdateAction.REMOVE);
        }
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public BossStyle getStyle() {
        return style;
    }

    public Set<BossFlag> getFlags() {
        return flags;
    }

    private void sendPacket(UpdateAction action) {
        for (UserConnection conn : new ArrayList<>(connections)) {
            PacketWrapper wrapper = getPacket(action, conn);
            sendPacketConnection(conn, wrapper);
        }
    }

    private void sendPacketConnection(UserConnection conn, PacketWrapper wrapper) {
        if (conn.getProtocolInfo() == null || !conn.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
            connections.remove(conn);
            return;
        }
        try {
            wrapper.send(Protocol1_9To1_8.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PacketWrapper getPacket(UpdateAction action, UserConnection connection) {
        try {
            PacketWrapper wrapper = new PacketWrapper(0x0C, null, connection); // TODO don't use fixed packet ids for future support
            wrapper.write(Type.UUID, uuid);
            wrapper.write(Type.VAR_INT, action.getId());
            switch (action) {
                case ADD:
                    Protocol1_9To1_8.FIX_JSON.write(wrapper, title);
                    wrapper.write(Type.FLOAT, health);
                    wrapper.write(Type.VAR_INT, color.getId());
                    wrapper.write(Type.VAR_INT, style.getId());
                    wrapper.write(Type.BYTE, (byte) flagToBytes());
                    break;
                case REMOVE:
                    break;
                case UPDATE_HEALTH:
                    wrapper.write(Type.FLOAT, health);
                    break;
                case UPDATE_TITLE:
                    Protocol1_9To1_8.FIX_JSON.write(wrapper, title);
                    break;
                case UPDATE_STYLE:
                    wrapper.write(Type.VAR_INT, color.getId());
                    wrapper.write(Type.VAR_INT, style.getId());
                    break;
                case UPDATE_FLAGS:
                    wrapper.write(Type.BYTE, (byte) flagToBytes());
                    break;
            }

            return wrapper;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int flagToBytes() {
        int bitmask = 0;
        for (BossFlag flag : flags)
            bitmask |= flag.getId();
        return bitmask;
    }

    private enum UpdateAction {

        ADD(0),
        REMOVE(1),
        UPDATE_HEALTH(2),
        UPDATE_TITLE(3),
        UPDATE_STYLE(4),
        UPDATE_FLAGS(5);

        private final int id;

        UpdateAction(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
