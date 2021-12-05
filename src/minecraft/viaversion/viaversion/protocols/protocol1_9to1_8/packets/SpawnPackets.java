package viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.entities.Entity1_10Types;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.remapper.ValueCreator;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_8;
import viaversion.viaversion.api.type.types.version.Types1_9;
import viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

import java.util.ArrayList;
import java.util.List;

public class SpawnPackets {
    public static final ValueTransformer<Integer, Double> toNewDouble = new ValueTransformer<Integer, Double>(Type.DOUBLE) {
        @Override
        public Double transform(PacketWrapper wrapper, Integer inputValue) {
            return inputValue / 32D;
        }
    };

    public static void register(Protocol1_9To1_8 protocol) {
        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID

                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        wrapper.write(Type.UUID, tracker.getEntityUUID(entityID)); // 1 - UUID
                    }
                });
                map(Type.BYTE); // 2 - Type

                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        int typeID = wrapper.get(Type.BYTE, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.getTypeFromId(typeID, true));
                        tracker.sendMetadataBuffer(entityID);
                    }
                });

                map(Type.INT, toNewDouble); // 3 - X - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 4 - Y - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 5 - Z - Needs to be divide by 32

                map(Type.BYTE); // 6 - Pitch
                map(Type.BYTE); // 7 - Yaw

                map(Type.INT); // 8 - Data

                // Create last 3 shorts
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        int data = wrapper.get(Type.INT, 0); // Data (1st Integer)

                        short vX = 0, vY = 0, vZ = 0;
                        if (data > 0) {
                            vX = wrapper.read(Type.SHORT);
                            vY = wrapper.read(Type.SHORT);
                            vZ = wrapper.read(Type.SHORT);
                        }

                        wrapper.write(Type.SHORT, vX);
                        wrapper.write(Type.SHORT, vY);
                        wrapper.write(Type.SHORT, vZ);
                    }
                });

                // Handle potions
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        final int entityID = wrapper.get(Type.VAR_INT, 0);
                        final int data = wrapper.get(Type.INT, 0); // Data

                        int typeID = wrapper.get(Type.BYTE, 0);
                        if (Entity1_10Types.getTypeFromId(typeID, true) == Entity1_10Types.EntityType.SPLASH_POTION) {
                            // Convert this to meta data, woo!
                            PacketWrapper metaPacket = wrapper.create(0x39, new ValueCreator() {
                                @Override
                                public void write(PacketWrapper wrapper) throws Exception {
                                    wrapper.write(Type.VAR_INT, entityID);
                                    List<Metadata> meta = new ArrayList<>();
                                    Item item = new Item(373, (byte) 1, (short) data, null); // Potion
                                    ItemRewriter.toClient(item); // Rewrite so that it gets the right nbt
                                    // TEMP FIX FOR POTIONS UNTIL WE FIGURE OUT HOW TO TRANSFORM SENT PACKETS
                                    Metadata potion = new Metadata(5, MetaType1_9.Slot, item);
                                    meta.add(potion);
                                    wrapper.write(Types1_9.METADATA_LIST, meta);
                                }
                            });
                            metaPacket.send(Protocol1_9To1_8.class);
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID

                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });

                map(Type.INT, toNewDouble); // 1 - X - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 2 - Y - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 3 - Z - Needs to be divide by 32

                map(Type.SHORT); // 4 - Amount to spawn
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.BYTE); // 1 - Type
                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        // Currently only lightning uses this
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.EntityType.LIGHTNING);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });

                map(Type.INT, toNewDouble); // 2 - X - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 3 - Y - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 4 - Z - Needs to be divide by 32
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID

                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        wrapper.write(Type.UUID, tracker.getEntityUUID(entityID)); // 1 - UUID
                    }
                });
                map(Type.UNSIGNED_BYTE); // 2 - Type

                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        int typeID = wrapper.get(Type.UNSIGNED_BYTE, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.getTypeFromId(typeID, false));
                        tracker.sendMetadataBuffer(entityID);
                    }
                });

                map(Type.INT, toNewDouble); // 3 - X - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 4 - Y - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 5 - Z - Needs to be divide by 32

                map(Type.BYTE); // 6 - Yaw
                map(Type.BYTE); // 7 - Pitch
                map(Type.BYTE); // 8 - Head Pitch

                map(Type.SHORT); // 9 - Velocity X
                map(Type.SHORT); // 10 - Velocity Y
                map(Type.SHORT); // 11 - Velocity Z

                map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        if (tracker.hasEntity(entityId)) {
                            protocol.get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, wrapper.user());
                        } else {
                            Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                            metadataList.clear();
                        }
                    }
                });
                // Handler for meta data
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.handleMetadata(entityID, metadataList);
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID

                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.EntityType.PAINTING);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        wrapper.write(Type.UUID, tracker.getEntityUUID(entityID)); // 1 - UUID
                    }
                });

                map(Type.STRING); // 2 - Title
                map(Type.POSITION); // 3 - Position
                map(Type.BYTE); // 4 - Direction
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID

                // Parse this info
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.addEntity(entityID, Entity1_10Types.EntityType.PLAYER);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });

                map(Type.INT, toNewDouble); // 2 - X - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 3 - Y - Needs to be divide by 32
                map(Type.INT, toNewDouble); // 4 - Z - Needs to be divide by 32

                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch

                handler(new PacketHandler() { //Handle discontinued player hand item
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        short item = wrapper.read(Type.SHORT);
                        if (item != 0) {
                            PacketWrapper packet = new PacketWrapper(0x3C, null, wrapper.user());
                            packet.write(Type.VAR_INT, wrapper.get(Type.VAR_INT, 0));
                            packet.write(Type.VAR_INT, 0);
                            packet.write(Type.ITEM, new Item(item, (byte) 1, (short) 0, null));
                            try {
                                packet.send(Protocol1_9To1_8.class, true, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        if (tracker.hasEntity(entityId)) {
                            protocol.get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, wrapper.user());
                        } else {
                            Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                            metadataList.clear();
                        }
                    }
                });

                // Handler for meta data
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        tracker.handleMetadata(entityID, metadataList);
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.DESTROY_ENTITIES, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT_ARRAY_PRIMITIVE); // 0 - Entities to destroy

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int[] entities = wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0);
                        for (int entity : entities) {
                            // EntityTracker
                            wrapper.user().get(EntityTracker1_9.class).removeEntity(entity);
                        }
                    }
                });
            }
        });
    }
}
