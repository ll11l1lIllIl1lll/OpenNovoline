package viaversion.viaversion.protocols.protocol1_9to1_8.metadata;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_10Types;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.EulerAngle;
import viaversion.viaversion.api.minecraft.Vector;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.MetaType;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

import java.util.List;
import java.util.UUID;

public class MetadataRewriter1_9To1_8 extends MetadataRewriter {

    public MetadataRewriter1_9To1_8(Protocol1_9To1_8 protocol) {
        super(protocol, EntityTracker1_9.class);
    }

    @Override
    protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
        MetaIndex metaIndex = MetaIndex.searchIndex(type, metadata.getId());
        if (metaIndex == null) {
            throw new Exception("Could not find valid metadata");
        }

        if (metaIndex.getNewType() == MetaType1_9.Discontinued) {
            metadatas.remove(metadata);
            return;
        }

        metadata.setId(metaIndex.getNewIndex());
        metadata.setMetaType(metaIndex.getNewType());

        Object value = metadata.getValue();
        switch (metaIndex.getNewType()) {
            case Byte:
                // convert from int, byte
                if (metaIndex.getOldType() == MetaType1_8.Byte) {
                    metadata.setValue(value);
                }
                if (metaIndex.getOldType() == MetaType1_8.Int) {
                    metadata.setValue(((Integer) value).byteValue());
                }
                // After writing the last one
                if (metaIndex == MetaIndex.ENTITY_STATUS && type == Entity1_10Types.EntityType.PLAYER) {
                    Byte val = 0;
                    if ((((Byte) value) & 0x10) == 0x10) { // Player eating/aiming/drinking
                        val = 1;
                    }
                    int newIndex = MetaIndex.PLAYER_HAND.getNewIndex();
                    MetaType metaType = MetaIndex.PLAYER_HAND.getNewType();
                    metadatas.add(new Metadata(newIndex, metaType, val));
                }
                break;
            case OptUUID:
                String owner = (String) value;
                UUID toWrite = null;
                if (!owner.isEmpty()) {
                    try {
                        toWrite = UUID.fromString(owner);
                    } catch (Exception ignored) {
                    }
                }
                metadata.setValue(toWrite);
                break;
            case VarInt:
                // convert from int, short, byte
                if (metaIndex.getOldType() == MetaType1_8.Byte) {
                    metadata.setValue(((Byte) value).intValue());
                }
                if (metaIndex.getOldType() == MetaType1_8.Short) {
                    metadata.setValue(((Short) value).intValue());
                }
                if (metaIndex.getOldType() == MetaType1_8.Int) {
                    metadata.setValue(value);
                }
                break;
            case Float:
                metadata.setValue(value);
                break;
            case String:
                metadata.setValue(value);
                break;
            case Boolean:
                if (metaIndex == MetaIndex.AGEABLE_AGE)
                    metadata.setValue((Byte) value < 0);
                else
                    metadata.setValue((Byte) value != 0);
                break;
            case Slot:
                metadata.setValue(value);
                ItemRewriter.toClient((Item) metadata.getValue());
                break;
            case Position:
                Vector vector = (Vector) value;
                metadata.setValue(vector);
                break;
            case Vector3F:
                EulerAngle angle = (EulerAngle) value;
                metadata.setValue(angle);
                break;
            case Chat:
                value = Protocol1_9To1_8.fixJson(value.toString());
                metadata.setValue(value);
                break;
            case BlockID:
                // Convert from int, short, byte
                metadata.setValue(((Number) value).intValue());
                break;
            default:
                metadatas.remove(metadata);
                throw new Exception("Unhandled MetaDataType: " + metaIndex.getNewType());
        }
    }

    @Override
    protected EntityType getTypeFromId(int type) {
        return Entity1_10Types.getTypeFromId(type, false);
    }

    @Override
    protected EntityType getObjectTypeFromId(int type) {
        return Entity1_10Types.getTypeFromId(type, true);
    }
}
