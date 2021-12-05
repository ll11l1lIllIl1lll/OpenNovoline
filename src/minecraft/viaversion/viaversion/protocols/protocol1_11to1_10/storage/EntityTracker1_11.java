package viaversion.viaversion.protocols.protocol1_11to1_10.storage;

import com.google.common.collect.Sets;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_11Types.EntityType;
import viaversion.viaversion.api.storage.EntityTracker;

import java.util.Set;

public class EntityTracker1_11 extends EntityTracker {
    private final Set<Integer> holograms = Sets.newConcurrentHashSet();

    public EntityTracker1_11(UserConnection user) {
        super(user, EntityType.PLAYER);
    }

    @Override
    public void removeEntity(int entityId) {
        super.removeEntity(entityId);

        if (isHologram(entityId))
            removeHologram(entityId);
    }

    public void addHologram(int entId) {
        holograms.add(entId);
    }

    public boolean isHologram(int entId) {
        return holograms.contains(entId);
    }

    public void removeHologram(int entId) {
        holograms.remove(entId);
    }
}
