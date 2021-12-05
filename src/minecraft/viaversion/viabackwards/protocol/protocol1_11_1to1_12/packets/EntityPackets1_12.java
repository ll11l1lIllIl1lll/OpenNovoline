/*
 * Copyright (c) 2016 Matsv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package viaversion.viabackwards.protocol.protocol1_11_1to1_12.packets;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import java.util.Optional;
import viaversion.viabackwards.api.exceptions.RemovedValueException;
import viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import viaversion.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.ParrotStorage;
import viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.ShoulderTracker;
import viaversion.viabackwards.utils.Block;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.entities.Entity1_12Types;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_12;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_12;
import viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;

public class EntityPackets1_12 extends LegacyEntityRewriter<Protocol1_11_1To1_12> {

    public EntityPackets1_12(Protocol1_11_1To1_12 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        protocol.registerOutgoing(ClientboundPackets1_12.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity id
                map(Type.UUID); // 1 - UUID
                map(Type.BYTE); // 2 - Type
                map(Type.DOUBLE); // 3 - x
                map(Type.DOUBLE); // 4 - y
                map(Type.DOUBLE); // 5 - z
                map(Type.BYTE); // 6 - Pitch
                map(Type.BYTE); // 7 - Yaw
                map(Type.INT); // 8 - data

                // Track Entity
                handler(getObjectTrackerHandler());
                handler(getObjectRewriter(id -> Entity1_12Types.ObjectType.findById(id).orElse(null)));

                // Handle FallingBlock blocks
                handler(wrapper -> {
                    Optional<Entity1_12Types.ObjectType> type = Entity1_12Types.ObjectType.findById(wrapper.get(Type.BYTE, 0));
                    if (type.isPresent() && type.get() == Entity1_12Types.ObjectType.FALLING_BLOCK) {
                        int objectData = wrapper.get(Type.INT, 0);
                        int objType = objectData & 4095;
                        int data = objectData >> 12 & 15;

                        Block block = getProtocol().getBlockItemPackets().handleBlock(objType, data);
                        if (block == null) {
                            return;
                        }

                        wrapper.set(Type.INT, 0, block.getId() | block.getData() << 12);
                    }
                });
            }
        });

        registerExtraTracker(ClientboundPackets1_12.SPAWN_EXPERIENCE_ORB, Entity1_12Types.EntityType.EXPERIENCE_ORB);
        registerExtraTracker(ClientboundPackets1_12.SPAWN_GLOBAL_ENTITY, Entity1_12Types.EntityType.WEATHER);

        protocol.registerOutgoing(ClientboundPackets1_12.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity id
                map(Type.UUID); // 1 - UUID
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
                map(Types1_12.METADATA_LIST); // 12 - Metadata

                // Track entity
                handler(getTrackerHandler());

                // Rewrite entity type / metadata
                handler(getMobSpawnRewriter(Types1_12.METADATA_LIST));
            }
        });

        registerExtraTracker(ClientboundPackets1_12.SPAWN_PAINTING, Entity1_12Types.EntityType.PAINTING);

        protocol.registerOutgoing(ClientboundPackets1_12.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID
                map(Type.DOUBLE); // 2 - X
                map(Type.DOUBLE); // 3 - Y
                map(Type.DOUBLE); // 4 - Z
                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch
                map(Types1_12.METADATA_LIST); // 7 - Metadata list

                handler(getTrackerAndMetaHandler(Types1_12.METADATA_LIST, Entity1_12Types.EntityType.PLAYER));
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Entity ID
                map(Type.UNSIGNED_BYTE); // 1 - Gamemode
                map(Type.INT); // 2 - Dimension

                handler(getTrackerHandler(Entity1_12Types.EntityType.PLAYER, Type.INT));

                handler(getDimensionHandler(1));

                handler(wrapper -> {
                    ShoulderTracker tracker = wrapper.user().get(ShoulderTracker.class);
                    tracker.setEntityId(wrapper.get(Type.INT, 0));
                });

                // Send fake inventory achievement
                handler(packetWrapper -> {
                    PacketWrapper wrapper = new PacketWrapper(0x07, null, packetWrapper.user());

                    wrapper.write(Type.VAR_INT, 1);
                    wrapper.write(Type.STRING, "achievement.openInventory");
                    wrapper.write(Type.VAR_INT, 1);

                    wrapper.send(Protocol1_11_1To1_12.class);
                });
            }
        });

        registerRespawn(ClientboundPackets1_12.RESPAWN);
        registerEntityDestroy(ClientboundPackets1_12.DESTROY_ENTITIES);
        registerMetadataRewriter(ClientboundPackets1_12.ENTITY_METADATA, Types1_12.METADATA_LIST);

        protocol.registerOutgoing(ClientboundPackets1_12.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.INT);
                handler(wrapper -> {
                    int size = wrapper.get(Type.INT, 0);
                    int newSize = size;
                    for (int i = 0; i < size; i++) {
                        String key = wrapper.read(Type.STRING);
                        // Remove new attribute
                        if (key.equals("generic.flyingSpeed")) {
                            newSize--;
                            wrapper.read(Type.DOUBLE);
                            int modSize = wrapper.read(Type.VAR_INT);
                            for (int j = 0; j < modSize; j++) {
                                wrapper.read(Type.UUID);
                                wrapper.read(Type.DOUBLE);
                                wrapper.read(Type.BYTE);
                            }
                        } else {
                            wrapper.write(Type.STRING, key);
                            wrapper.passthrough(Type.DOUBLE);
                            int modSize = wrapper.passthrough(Type.VAR_INT);
                            for (int j = 0; j < modSize; j++) {
                                wrapper.passthrough(Type.UUID);
                                wrapper.passthrough(Type.DOUBLE);
                                wrapper.passthrough(Type.BYTE);
                            }
                        }
                    }

                    if (newSize != size) {
                        wrapper.set(Type.INT, 0, newSize);
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        mapEntity(Entity1_12Types.EntityType.PARROT, Entity1_12Types.EntityType.BAT).mobName("Parrot").spawnMetadata(storage -> storage.add(new Metadata(12, MetaType1_12.Byte, (byte) 0x00)));
        mapEntity(Entity1_12Types.EntityType.ILLUSION_ILLAGER, Entity1_12Types.EntityType.EVOCATION_ILLAGER).mobName("Illusioner");

        // Handle Illager
        registerMetaHandler().filter(Entity1_12Types.EntityType.EVOCATION_ILLAGER, true, 12).removed();
        registerMetaHandler().filter(Entity1_12Types.EntityType.EVOCATION_ILLAGER, true, 13).handleIndexChange(12);

        registerMetaHandler().filter(Entity1_12Types.EntityType.ILLUSION_ILLAGER, 0).handle(e -> {
            byte mask = (byte) e.getData().getValue();

            if ((mask & 0x20) == 0x20)
                mask &= ~0x20;

            e.getData().setValue(mask);
            return e.getData();
        });

        // Create Parrot storage
        registerMetaHandler().filter(Entity1_12Types.EntityType.PARROT, true).handle(e -> {
            if (!e.getEntity().has(ParrotStorage.class))
                e.getEntity().put(new ParrotStorage());
            return e.getData();
        });
        // Parrot remove animal metadata
        registerMetaHandler().filter(Entity1_12Types.EntityType.PARROT, 12).removed(); // Is baby
        registerMetaHandler().filter(Entity1_12Types.EntityType.PARROT, 13).handle(e -> {
            Metadata data = e.getData();
            ParrotStorage storage = e.getEntity().get(ParrotStorage.class);
            boolean isSitting = (((byte) data.getValue()) & 0x01) == 0x01;
            boolean isTamed = (((byte) data.getValue()) & 0x04) == 0x04;

            if (!storage.isTamed() && isTamed) {
                // TODO do something to let the user know it's done
            }

            storage.setTamed(isTamed);

            if (isSitting) {
                data.setId(12);
                data.setValue((byte) 0x01);
                storage.setSitting(true);
            } else if (storage.isSitting()) {
                data.setId(12);
                data.setValue((byte) 0x00);
                storage.setSitting(false);
            } else
                throw RemovedValueException.EX;

            return data;
        }); // Flags (Is sitting etc, might be useful in the future
        registerMetaHandler().filter(Entity1_12Types.EntityType.PARROT, 14).removed(); // Owner
        registerMetaHandler().filter(Entity1_12Types.EntityType.PARROT, 15).removed(); // Variant

        // Left shoulder entity data
        registerMetaHandler().filter(Entity1_12Types.EntityType.PLAYER, 15).handle(e -> {
            CompoundTag tag = (CompoundTag) e.getData().getValue();
            ShoulderTracker tracker = e.getUser().get(ShoulderTracker.class);

            if (tag.isEmpty() && tracker.getLeftShoulder() != null) {
                tracker.setLeftShoulder(null);
                tracker.update();
            } else if (tag.contains("id") && e.getEntity().getEntityId() == tracker.getEntityId()) {
                String id = (String) tag.get("id").getValue();
                if (tracker.getLeftShoulder() == null || !tracker.getLeftShoulder().equals(id)) {
                    tracker.setLeftShoulder(id);
                    tracker.update();
                }
            }

            throw RemovedValueException.EX;
        });

        // Right shoulder entity data
        registerMetaHandler().filter(Entity1_12Types.EntityType.PLAYER, 16).handle(e -> {
            CompoundTag tag = (CompoundTag) e.getData().getValue();
            ShoulderTracker tracker = e.getUser().get(ShoulderTracker.class);

            if (tag.isEmpty() && tracker.getRightShoulder() != null) {
                tracker.setRightShoulder(null);
                tracker.update();
            } else if (tag.contains("id") && e.getEntity().getEntityId() == tracker.getEntityId()) {
                String id = (String) tag.get("id").getValue();
                if (tracker.getRightShoulder() == null || !tracker.getRightShoulder().equals(id)) {
                    tracker.setRightShoulder(id);
                    tracker.update();
                }
            }

            throw RemovedValueException.EX;
        });
    }

    @Override
    protected EntityType getTypeFromId(int typeId) {
        return Entity1_12Types.getTypeFromId(typeId, false);
    }

    @Override
    protected EntityType getObjectTypeFromId(final int typeId) {
        return Entity1_12Types.getTypeFromId(typeId, true);
    }
}
