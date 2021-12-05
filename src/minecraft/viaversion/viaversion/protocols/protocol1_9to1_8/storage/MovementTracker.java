package viaversion.viaversion.protocols.protocol1_9to1_8.storage;

import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;

public class MovementTracker extends StoredObject {
    private static final long IDLE_PACKET_DELAY = 50L; // Update every 50ms (20tps)
    private static final long IDLE_PACKET_LIMIT = 20; // Max 20 ticks behind
    private long nextIdlePacket = 0L;
    private boolean ground = true;

    public MovementTracker(UserConnection user) {
        super(user);
    }

    public void incrementIdlePacket() {
        // Notify of next update
        // Allow a maximum lag spike of 1 second (20 ticks/updates)
        this.nextIdlePacket = Math.max(nextIdlePacket + IDLE_PACKET_DELAY, System.currentTimeMillis() - IDLE_PACKET_DELAY * IDLE_PACKET_LIMIT);
    }

    public long getNextIdlePacket() {
        return nextIdlePacket;
    }

    public boolean isGround() {
        return ground;
    }

    public void setGround(boolean ground) {
        this.ground = ground;
    }
}
