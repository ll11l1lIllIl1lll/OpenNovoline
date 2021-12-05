package viaversion.viaversion.protocols.protocol1_14_1to1_14.storage;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_14Types.EntityType;
import viaversion.viaversion.api.storage.EntityTracker;

public class EntityTracker1_14_1 extends EntityTracker {

    public EntityTracker1_14_1(UserConnection user) {
        super(user, EntityType.PLAYER);
    }
}
