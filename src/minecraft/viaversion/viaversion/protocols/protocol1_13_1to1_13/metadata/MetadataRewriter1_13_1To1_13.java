package viaversion.viaversion.protocols.protocol1_13_1to1_13.metadata;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_13Types;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.api.type.types.Particle;
import viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import viaversion.viaversion.protocols.protocol1_13_1to1_13.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

import java.util.List;

public class MetadataRewriter1_13_1To1_13 extends MetadataRewriter {

    public MetadataRewriter1_13_1To1_13(Protocol1_13_1To1_13 protocol) {
        super(protocol, EntityTracker1_13.class);
    }

    @Override
    protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
        // 1.13 changed item to flat item (no data)
        if (metadata.getMetaType() == MetaType1_13.Slot) {
            InventoryPackets.toClient((Item) metadata.getValue());
        } else if (metadata.getMetaType() == MetaType1_13.BlockID) {
            // Convert to new block id
            int data = (int) metadata.getValue();
            metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
        }

        if (type == null) return;

        if (type.isOrHasParent(Entity1_13Types.EntityType.MINECART_ABSTRACT) && metadata.getId() == 9) {
            // New block format
            int data = (int) metadata.getValue();
            metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
        } else if (type.isOrHasParent(Entity1_13Types.EntityType.ABSTRACT_ARROW) && metadata.getId() >= 7) {
            metadata.setId(metadata.getId() + 1); // New shooter UUID
        } else if (type.is(Entity1_13Types.EntityType.AREA_EFFECT_CLOUD) && metadata.getId() == 10) {
            rewriteParticle((Particle) metadata.getValue());
        }
    }

    @Override
    protected EntityType getTypeFromId(int type) {
        return Entity1_13Types.getTypeFromId(type, false);
    }

    @Override
    protected EntityType getObjectTypeFromId(int type) {
        return Entity1_13Types.getTypeFromId(type, true);
    }
}
