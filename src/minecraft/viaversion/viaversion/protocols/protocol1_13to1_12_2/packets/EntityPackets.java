package viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.entities.Entity1_13Types;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_12;
import viaversion.viaversion.api.type.types.version.Types1_13;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.metadata.MetadataRewriter1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

public class EntityPackets {

    public static void register(Protocol1_13To1_12_2 protocol) {
        MetadataRewriter1_13To1_12_2 metadataRewriter = protocol.get(MetadataRewriter1_13To1_12_2.class);

        protocol.registerOutgoing(ClientboundPackets1_12_1.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity id
                map(Type.UUID); // 1 - UUID
                map(Type.BYTE); // 2 - Type
                map(Type.DOUBLE); // 3 - X
                map(Type.DOUBLE); // 4 - Y
                map(Type.DOUBLE); // 5 - Z
                map(Type.BYTE); // 6 - Pitch
                map(Type.BYTE); // 7 - Yaw
                map(Type.INT); // 8 - Data

                // Track Entity
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        byte type = wrapper.get(Type.BYTE, 0);
                        Entity1_13Types.EntityType entType = Entity1_13Types.getTypeFromId(type, true);

                        if (entType != null) {
                            if (entType.is(Entity1_13Types.EntityType.FALLING_BLOCK)) {
                                int oldId = wrapper.get(Type.INT, 0);
                                int combined = (((oldId & 4095) << 4) | (oldId >> 12 & 15));
                                wrapper.set(Type.INT, 0, WorldPackets.toNewId(combined));
                            }

                            // Fix ItemFrame hitbox
                            if (entType.is(Entity1_13Types.EntityType.ITEM_FRAME)) {
                                int data = wrapper.get(Type.INT, 0);

                                switch (data) {
                                    // South
                                    case 0:
                                        data = 3;
                                        break;
                                    // West
                                    case 1:
                                        data = 4;
                                        break;
                                    // North is the same
                                    // East
                                    case 3:
                                        data = 5;
                                        break;
                                }

                                wrapper.set(Type.INT, 0, data);

                                // Register Type ID
                                wrapper.user().get(EntityTracker1_13.class).addEntity(entityId, entType);
                            }
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Entity UUID
                map(Type.VAR_INT); // 2 - Entity Type
                map(Type.DOUBLE); // 3 - X
                map(Type.DOUBLE); // 4 - Y
                map(Type.DOUBLE); // 5 - Z
                map(Type.BYTE); // 6 - Yaw
                map(Type.BYTE); // 7 - Pitch
                map(Type.BYTE); // 8 - Head Pitch
                map(Type.SHORT); // 9 - Velocity X
                map(Type.SHORT); // 10 - Velocity Y
                map(Type.SHORT); // 11 - Velocity Z
                map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST); // 12 - Metadata

                handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST));
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID
                map(Type.DOUBLE); // 2 - X
                map(Type.DOUBLE); // 3 - Y
                map(Type.DOUBLE); // 4 - Z
                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch
                map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST); // 7 - Metadata

                handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST, Entity1_13Types.EntityType.PLAYER));
            }
        });

        metadataRewriter.registerEntityDestroy(ClientboundPackets1_12_1.DESTROY_ENTITIES);
        metadataRewriter.registerMetadataRewriter(ClientboundPackets1_12_1.ENTITY_METADATA, Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
    }
}
