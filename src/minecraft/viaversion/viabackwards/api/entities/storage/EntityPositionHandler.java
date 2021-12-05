package viaversion.viabackwards.api.entities.storage;

import viaversion.viabackwards.ViaBackwards;
import viaversion.viabackwards.api.rewriters.EntityRewriterBase;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.type.Type;

import java.util.function.Supplier;

public class EntityPositionHandler {

    public static final double RELATIVE_MOVE_FACTOR = 32 * 128;
    private final EntityRewriterBase<?> entityRewriter;
    private final Class<? extends EntityPositionStorage> storageClass;
    private final Supplier<? extends EntityPositionStorage> storageSupplier;
    private boolean warnedForMissingEntity;

    public EntityPositionHandler(EntityRewriterBase<?> entityRewriter,
                                 Class<? extends EntityPositionStorage> storageClass, Supplier<? extends EntityPositionStorage> storageSupplier) {
        this.entityRewriter = entityRewriter;
        this.storageClass = storageClass;
        this.storageSupplier = storageSupplier;
    }

    public void cacheEntityPosition(PacketWrapper wrapper, boolean create, boolean relative) throws Exception {
        cacheEntityPosition(wrapper,
                wrapper.get(Type.DOUBLE, 0), wrapper.get(Type.DOUBLE, 1), wrapper.get(Type.DOUBLE, 2), create, relative);
    }

    public void cacheEntityPosition(PacketWrapper wrapper, double x, double y, double z, boolean create, boolean relative) throws Exception {
        int entityId = wrapper.get(Type.VAR_INT, 0);
        EntityTracker.StoredEntity storedEntity = entityRewriter.getEntityTracker(wrapper.user()).getEntity(entityId);
        if (storedEntity == null) {
            if (Via.getManager().isDebug()) { // There is too many plugins violating this out there, and reading seems to be hard! :>
                ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing at position: " + x + " - " + y + " - " + z + " in " + storageClass.getCanonicalName());
                if (entityId == -1 && x == 0 && y == 0 && z == 0) {
                    ViaBackwards.getPlatform().getLogger().warning("DO NOT REPORT THIS TO VIA, THIS IS A PLUGIN ISSUE");
                } else if (!warnedForMissingEntity) {
                    warnedForMissingEntity = true;
                    ViaBackwards.getPlatform().getLogger().warning("This is very likely caused by a plugin sending a teleport packet for an entity outside of the player's range.");
                }
            }
            return;
        }

        EntityPositionStorage positionStorage = create ? storageSupplier.get() : storedEntity.get(storageClass);
        if (positionStorage == null) {
            ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing " + storageClass.getCanonicalName());
            return;
        }

        positionStorage.setCoordinates(x, y, z, relative);
        storedEntity.put(positionStorage);
    }

    public EntityPositionStorage getStorage(UserConnection user, int entityId) {
        EntityTracker.StoredEntity storedEntity = user.get(EntityTracker.class).get(entityRewriter.getProtocol()).getEntity(entityId);
        EntityPositionStorage entityStorage;
        if (storedEntity == null || (entityStorage = storedEntity.get(EntityPositionStorage.class)) == null) {
            ViaBackwards.getPlatform().getLogger().warning("Untracked entity with id " + entityId + " in " + storageClass.getCanonicalName());
            return null;
        }
        return entityStorage;
    }

    public static void writeFacingAngles(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dX = targetX - x;
        double dY = targetY - y;
        double dZ = targetZ - z;
        double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        double yaw = -Math.atan2(dX, dZ) / Math.PI * 180;
        if (yaw < 0) {
            yaw = 360 + yaw;
        }
        double pitch = -Math.asin(dY / r) / Math.PI * 180;

        wrapper.write(Type.BYTE, (byte) (yaw * 256f / 360f));
        wrapper.write(Type.BYTE, (byte) (pitch * 256f / 360f));
    }

    public static void writeFacingDegrees(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dX = targetX - x;
        double dY = targetY - y;
        double dZ = targetZ - z;
        double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        double yaw = -Math.atan2(dX, dZ) / Math.PI * 180;
        if (yaw < 0) {
            yaw = 360 + yaw;
        }
        double pitch = -Math.asin(dY / r) / Math.PI * 180;

        wrapper.write(Type.FLOAT, (float) yaw);
        wrapper.write(Type.FLOAT, (float) pitch);
    }
}
