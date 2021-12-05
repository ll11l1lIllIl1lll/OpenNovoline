package viaversion.viaversion.protocols.protocol1_14to1_13_2.storage;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_14Types.EntityType;
import viaversion.viaversion.api.storage.EntityTracker;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.WorldPackets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker1_14 extends EntityTracker {
    private final Map<Integer, Byte> insentientData = new ConcurrentHashMap<>();
    // 0x1 = sleeping, 0x2 = riptide
    private final Map<Integer, Byte> sleepingAndRiptideData = new ConcurrentHashMap<>();
    private final Map<Integer, Byte> playerEntityFlags = new ConcurrentHashMap<>();
    private int latestTradeWindowId;
    private boolean forceSendCenterChunk = true;
    private int chunkCenterX, chunkCenterZ;

    public EntityTracker1_14(UserConnection user) {
        super(user, EntityType.PLAYER);
    }

    @Override
    public void removeEntity(int entityId) {
        super.removeEntity(entityId);

        insentientData.remove(entityId);
        sleepingAndRiptideData.remove(entityId);
        playerEntityFlags.remove(entityId);
    }

    public byte getInsentientData(int entity) {
        Byte val = insentientData.get(entity);
        return val == null ? 0 : val;
    }

    public void setInsentientData(int entity, byte value) {
        insentientData.put(entity, value);
    }

    private static byte zeroIfNull(Byte val) {
        if (val == null) return 0;
        return val;
    }

    public boolean isSleeping(int player) {
        return (zeroIfNull(sleepingAndRiptideData.get(player)) & 1) != 0;
    }

    public void setSleeping(int player, boolean value) {
        byte newValue = (byte) ((zeroIfNull(sleepingAndRiptideData.get(player)) & ~1) | (value ? 1 : 0));
        if (newValue == 0) {
            sleepingAndRiptideData.remove(player);
        } else {
            sleepingAndRiptideData.put(player, newValue);
        }
    }

    public boolean isRiptide(int player) {
        return (zeroIfNull(sleepingAndRiptideData.get(player)) & 2) != 0;
    }

    public void setRiptide(int player, boolean value) {
        byte newValue = (byte) ((zeroIfNull(sleepingAndRiptideData.get(player)) & ~2) | (value ? 2 : 0));
        if (newValue == 0) {
            sleepingAndRiptideData.remove(player);
        } else {
            sleepingAndRiptideData.put(player, newValue);
        }
    }

    @Override
    public void onExternalJoinGame(int playerEntityId) {
        super.onExternalJoinGame(playerEntityId);

        PacketWrapper setViewDistance = new PacketWrapper(0x41, null, getUser());
        setViewDistance.write(Type.VAR_INT, WorldPackets.SERVERSIDE_VIEW_DISTANCE);
        try {
            setViewDistance.send(Protocol1_14To1_13_2.class, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte getEntityFlags(int player) {
        return zeroIfNull(playerEntityFlags.get(player));
    }

    public void setEntityFlags(int player, byte data) {
        playerEntityFlags.put(player, data);
    }

    public int getLatestTradeWindowId() {
        return latestTradeWindowId;
    }

    public void setLatestTradeWindowId(int latestTradeWindowId) {
        this.latestTradeWindowId = latestTradeWindowId;
    }

    public boolean isForceSendCenterChunk() {
        return forceSendCenterChunk;
    }

    public void setForceSendCenterChunk(boolean forceSendCenterChunk) {
        this.forceSendCenterChunk = forceSendCenterChunk;
    }

    public int getChunkCenterX() {
        return chunkCenterX;
    }

    public void setChunkCenterX(int chunkCenterX) {
        this.chunkCenterX = chunkCenterX;
    }

    public int getChunkCenterZ() {
        return chunkCenterZ;
    }

    public void setChunkCenterZ(int chunkCenterZ) {
        this.chunkCenterZ = chunkCenterZ;
    }
}
