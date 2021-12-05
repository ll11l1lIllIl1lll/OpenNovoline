package viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.google.common.collect.ImmutableList;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Pair;
import viaversion.viaversion.api.Triple;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_8;
import viaversion.viaversion.api.type.types.version.Types1_9;
import viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityPackets {
    public static final ValueTransformer<Byte, Short> toNewShort = new ValueTransformer<Byte, Short>(Type.SHORT) {
        @Override
        public Short transform(PacketWrapper wrapper, Byte inputValue) {
            return (short) (inputValue * 128);
        }
    };

    public static void register(Protocol1_9To1_8 protocol) {
        // Attach Entity Packet
        protocol.registerOutgoing(ClientboundPackets1_8.ATTACH_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Entity ID
                map(Type.INT); // 1 - Vehicle

                // Leash boolean is removed in new versions
                map(Type.BOOLEAN, new ValueTransformer<Boolean, Void>(Type.NOTHING) {
                    @Override
                    public Void transform(PacketWrapper wrapper, Boolean inputValue) throws Exception {
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        if (!inputValue) {
                            int passenger = wrapper.get(Type.INT, 0);
                            int vehicle = wrapper.get(Type.INT, 1);

                            wrapper.cancel(); // Don't send current packet

                            PacketWrapper passengerPacket = wrapper.create(0x40); // Passenger Packet ID
                            if (vehicle == -1) {
                                if (!tracker.getVehicleMap().containsKey(passenger))
                                    return null; // Cancel
                                passengerPacket.write(Type.VAR_INT, tracker.getVehicleMap().remove(passenger));
                                passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{});
                            } else {
                                passengerPacket.write(Type.VAR_INT, vehicle);
                                passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{passenger});
                                tracker.getVehicleMap().put(passenger, vehicle);
                            }
                            passengerPacket.send(Protocol1_9To1_8.class); // Send the packet
                        }
                        return null;
                    }
                });
            }
        });
        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_TELEPORT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.INT, SpawnPackets.toNewDouble); // 1 - X - Needs to be divide by 32
                map(Type.INT, SpawnPackets.toNewDouble); // 2 - Y - Needs to be divide by 32
                map(Type.INT, SpawnPackets.toNewDouble); // 3 - Z - Needs to be divide by 32

                map(Type.BYTE); // 4 - Pitch
                map(Type.BYTE); // 5 - Yaw

                map(Type.BOOLEAN); // 6 - On Ground

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        if (Via.getConfig().isHologramPatch()) {
                            EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                            if (tracker.getKnownHolograms().contains(entityID)) {
                                Double newValue = wrapper.get(Type.DOUBLE, 1);
                                newValue += (Via.getConfig().getHologramYOffset());
                                wrapper.set(Type.DOUBLE, 1, newValue);
                            }
                        }
                    }
                });


            }
        });
        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.BYTE, toNewShort); // 1 - X
                map(Type.BYTE, toNewShort); // 2 - Y
                map(Type.BYTE, toNewShort); // 3 - Z

                map(Type.BYTE); // 4 - Yaw
                map(Type.BYTE); // 5 - Pitch

                map(Type.BOOLEAN); // 6 - On Ground
            }
        });
        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_POSITION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.BYTE, toNewShort); // 1 - X
                map(Type.BYTE, toNewShort); // 2 - Y
                map(Type.BYTE, toNewShort); // 3 - Z

                map(Type.BOOLEAN); // 4 - On Ground
            }
        });
        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_EQUIPMENT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                // 1 - Slot ID
                map(Type.SHORT, new ValueTransformer<Short, Integer>(Type.VAR_INT) {
                    @Override
                    public Integer transform(PacketWrapper wrapper, Short slot) throws Exception {
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        int receiverId = wrapper.user().get(EntityTracker1_9.class).getClientEntityId();
                        // Normally, 0 = hand and 1-4 = armor
                        // ... but if the sent id is equal to the receiver's id, 0-3 will instead mark the armor slots
                        // (In 1.9+, every client treats the received the same: 0=hand, 1=offhand, 2-5=armor)
                        if (entityId == receiverId) {
                            return slot.intValue() + 2;
                        }
                        return slot > 0 ? slot.intValue() + 1 : slot.intValue();
                    }
                });
                map(Type.ITEM); // 2 - Item
                // Item Rewriter
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item stack = wrapper.get(Type.ITEM, 0);
                        ItemRewriter.toClient(stack);
                    }
                });
                // Blocking
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        EntityTracker1_9 entityTracker = wrapper.user().get(EntityTracker1_9.class);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        Item stack = wrapper.get(Type.ITEM, 0);

                        if (stack != null) {
                            if (Protocol1_9To1_8.isSword(stack.getIdentifier())) {
                                entityTracker.getValidBlocking().add(entityID);
                                return;
                            }
                        }
                        entityTracker.getValidBlocking().remove(entityID);
                    }
                });
            }
        });
        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_METADATA, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST); // 1 - Metadata List
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = wrapper.user().get(EntityTracker1_9.class);
                        if (tracker.hasEntity(entityId)) {
                            protocol.get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, wrapper.user());
                        } else {
                            // Buffer
                            tracker.addMetadataToBuffer(entityId, metadataList);
                            wrapper.cancel();
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

                // Cancel packet if list empty
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        if (metadataList.isEmpty()) {
                            wrapper.cancel();
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.BYTE); // 1 - Effect ID
                map(Type.BYTE); // 2 - Amplifier
                map(Type.VAR_INT); // 3 - Duration
                handler(new PacketHandler() { //Handle effect indicator
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        boolean showParticles = wrapper.read(Type.BOOLEAN); //In 1.8 = true->Show particles : false->Hide particles
                        boolean newEffect = Via.getConfig().isNewEffectIndicator();
                        //0: hide, 1: shown without indictator, 2: shown with indicator, 3: hide with beacon indicator but we don't use it.
                        wrapper.write(Type.BYTE, (byte) (showParticles ? newEffect ? 2 : 1 : 0));
                    }
                });
            }
        });

        protocol.cancelOutgoing(ClientboundPackets1_8.UPDATE_ENTITY_NBT);

        protocol.registerOutgoing(ClientboundPackets1_8.COMBAT_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); //Event id
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.VAR_INT, 0) == 2) { // entity dead
                            wrapper.passthrough(Type.VAR_INT); //Player id
                            wrapper.passthrough(Type.INT); //Entity id
                            Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_8.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (!Via.getConfig().isMinimizeCooldown()) return;
                        if (wrapper.get(Type.VAR_INT, 0) != wrapper.user().get(EntityTracker1_9.class).getProvidedEntityId()) {
                            return;
                        }
                        int propertiesToRead = wrapper.read(Type.INT);
                        Map<String, Pair<Double, List<Triple<UUID, Double, Byte>>>> properties = new HashMap<>(propertiesToRead);
                        for (int i = 0; i < propertiesToRead; i++) {
                            String key = wrapper.read(Type.STRING);
                            Double value = wrapper.read(Type.DOUBLE);
                            int modifiersToRead = wrapper.read(Type.VAR_INT);
                            List<Triple<UUID, Double, Byte>> modifiers = new ArrayList<>(modifiersToRead);
                            for (int j = 0; j < modifiersToRead; j++) {
                                modifiers.add(
                                        new Triple<>(
                                                wrapper.read(Type.UUID),
                                                wrapper.read(Type.DOUBLE), // Amount
                                                wrapper.read(Type.BYTE) // Operation
                                        )
                                );
                            }
                            properties.put(key, new Pair<>(value, modifiers));
                        }

                        // == Why 15.9? ==
                        // Higher values hides the cooldown but it bugs visual animation on hand
                        // when removing item from hand with inventory gui
                        properties.put("generic.attackSpeed", new Pair<Double, List<Triple<UUID, Double, Byte>>>(15.9, ImmutableList.of( // Neutralize modifiers
                                new Triple<>(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), 0.0, (byte) 0), // Tool and weapon modifier
                                new Triple<>(UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3"), 0.0, (byte) 2), // Dig speed
                                new Triple<>(UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386"), 0.0, (byte) 2) // Dig slow down
                        )));

                        wrapper.write(Type.INT, properties.size());
                        for (Map.Entry<String, Pair<Double, List<Triple<UUID, Double, Byte>>>> entry : properties.entrySet()) {
                            wrapper.write(Type.STRING, entry.getKey()); // Key
                            wrapper.write(Type.DOUBLE, entry.getValue().getKey()); // Value
                            wrapper.write(Type.VAR_INT, entry.getValue().getValue().size());
                            for (Triple<UUID, Double, Byte> modifier : entry.getValue().getValue()) {
                                wrapper.write(Type.UUID, modifier.getFirst());
                                wrapper.write(Type.DOUBLE, modifier.getSecond()); // Amount
                                wrapper.write(Type.BYTE, modifier.getThird()); // Operation
                            }
                        }
                    }
                });
            }
        });


        /* Incoming Packets */
        protocol.registerIncoming(ServerboundPackets1_9.ENTITY_ACTION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Player ID
                map(Type.VAR_INT); // 1 - Action
                map(Type.VAR_INT); // 2 - Jump
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int action = wrapper.get(Type.VAR_INT, 1);
                        if (action == 6 || action == 8)
                            wrapper.cancel();
                        if (action == 7) {
                            wrapper.set(Type.VAR_INT, 1, 6);
                        }
                    }
                });
            }
        });

        protocol.registerIncoming(ServerboundPackets1_9.INTERACT_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID (Target)
                map(Type.VAR_INT); // 1 - Action Type

                // Cancel second hand to prevent double interact
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int type = wrapper.get(Type.VAR_INT, 1);
                        if (type == 2) {
                            wrapper.passthrough(Type.FLOAT); // 2 - X
                            wrapper.passthrough(Type.FLOAT); // 3 - Y
                            wrapper.passthrough(Type.FLOAT); // 4 - Z
                        }
                        if (type == 0 || type == 2) {
                            int hand = wrapper.read(Type.VAR_INT); // 2/5 - Hand

                            if (hand == 1)
                                wrapper.cancel();
                        }
                    }
                });
            }
        });
    }
}
