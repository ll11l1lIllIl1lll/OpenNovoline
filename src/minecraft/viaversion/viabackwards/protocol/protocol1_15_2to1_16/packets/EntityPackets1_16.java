package viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets;

import com.google.gson.JsonElement;
import viaversion.viabackwards.api.rewriters.EntityRewriter;
import viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.WorldNameTracker;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.entities.Entity1_15Types;
import viaversion.viaversion.api.entities.Entity1_16Types;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.MetaType;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.Particle;
import viaversion.viaversion.api.type.types.version.Types1_14;
import viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class EntityPackets1_16 extends EntityRewriter<Protocol1_15_2To1_16> {

    private final ValueTransformer<String, Integer> dimensionTransformer = new ValueTransformer<String, Integer>(Type.STRING, Type.INT) {
        @Override
        public Integer transform(PacketWrapper wrapper, String input) throws Exception {
            switch (input) {
                case "minecraft:the_nether":
                    return -1;
                default:
                case "minecraft:overworld":
                    return 0;
                case "minecraft:the_end":
                    return 1;
            }
        }
    };

    public EntityPackets1_16(Protocol1_15_2To1_16 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        registerSpawnTrackerWithData(ClientboundPackets1_16.SPAWN_ENTITY, Entity1_16Types.EntityType.FALLING_BLOCK);
        registerSpawnTracker(ClientboundPackets1_16.SPAWN_MOB);

        protocol.registerOutgoing(ClientboundPackets1_16.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(dimensionTransformer); // Dimension Type
                handler(wrapper -> {
                    // Grab the tracker for world names
                    WorldNameTracker worldNameTracker = wrapper.user().get(WorldNameTracker.class);
                    String nextWorldName = wrapper.read(Type.STRING); // World Name

                    wrapper.passthrough(Type.LONG); // Seed
                    wrapper.passthrough(Type.UNSIGNED_BYTE); // Gamemode
                    wrapper.read(Type.BYTE); // Previous gamemode

                    // Grab client world
                    ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    int dimension = wrapper.get(Type.INT, 0);

                    // Send a dummy respawn with a different dimension if the world name was different and the same dimension was used
                    if (clientWorld.getEnvironment() != null && dimension == clientWorld.getEnvironment().getId() && !nextWorldName.equals(worldNameTracker.getWorldName())) {
                        PacketWrapper packet = wrapper.create(ClientboundPackets1_15.RESPAWN.ordinal());
                        packet.write(Type.INT, dimension == 0 ? -1 : 0);
                        packet.write(Type.LONG, 0L);
                        packet.write(Type.UNSIGNED_BYTE, (short) 0);
                        packet.write(Type.STRING, "default");
                        packet.send(Protocol1_15_2To1_16.class, true, true);
                    }

                    clientWorld.setEnvironment(dimension);

                    wrapper.write(Type.STRING, "default"); // Level type
                    wrapper.read(Type.BOOLEAN); // Debug
                    if (wrapper.read(Type.BOOLEAN)) {
                        wrapper.set(Type.STRING, 0, "flat");
                    }
                    wrapper.read(Type.BOOLEAN); // Keep all playerdata

                    // Finally update the world name
                    worldNameTracker.setWorldName(nextWorldName);
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_16.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); //  Entity ID
                map(Type.UNSIGNED_BYTE); // Gamemode
                map(Type.BYTE, Type.NOTHING); // Previous gamemode
                map(Type.STRING_ARRAY, Type.NOTHING); // World list
                map(Type.NBT, Type.NOTHING); // whatever this is
                map(dimensionTransformer); // Dimension Type
                handler(wrapper -> {
                    WorldNameTracker worldNameTracker = wrapper.user().get(WorldNameTracker.class);
                    worldNameTracker.setWorldName(wrapper.read(Type.STRING)); // Save the world name
                });
                map(Type.LONG); // Seed
                map(Type.UNSIGNED_BYTE); // Max players
                handler(wrapper -> {
                    ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                    clientChunks.setEnvironment(wrapper.get(Type.INT, 1));
                    getEntityTracker(wrapper.user()).trackEntityType(wrapper.get(Type.INT, 0), Entity1_16Types.EntityType.PLAYER);

                    wrapper.write(Type.STRING, "default"); // Level type

                    wrapper.passthrough(Type.VAR_INT); // View distance
                    wrapper.passthrough(Type.BOOLEAN); // Reduced debug info
                    wrapper.passthrough(Type.BOOLEAN); // Show death screen

                    wrapper.read(Type.BOOLEAN); // Debug
                    if (wrapper.read(Type.BOOLEAN)) {
                        wrapper.set(Type.STRING, 0, "flat");
                    }
                });
            }
        });

        registerExtraTracker(ClientboundPackets1_16.SPAWN_EXPERIENCE_ORB, Entity1_16Types.EntityType.EXPERIENCE_ORB);
        // F Spawn Global Object, it is no longer with us :(
        registerExtraTracker(ClientboundPackets1_16.SPAWN_PAINTING, Entity1_16Types.EntityType.PAINTING);
        registerExtraTracker(ClientboundPackets1_16.SPAWN_PLAYER, Entity1_16Types.EntityType.PLAYER);
        registerEntityDestroy(ClientboundPackets1_16.DESTROY_ENTITIES);
        registerMetadataRewriter(ClientboundPackets1_16.ENTITY_METADATA, Types1_14.METADATA_LIST);

        protocol.registerOutgoing(ClientboundPackets1_16.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT);
                    int size = wrapper.passthrough(Type.INT);
                    for (int i = 0; i < size; i++) {
                        String attributeIdentifier = wrapper.read(Type.STRING);
                        String oldKey = protocol.getMappingData().getAttributeMappings().get(attributeIdentifier);
                        wrapper.write(Type.STRING, oldKey != null ? oldKey : attributeIdentifier.replace("minecraft:", ""));

                        wrapper.passthrough(Type.DOUBLE);
                        int modifierSize = wrapper.passthrough(Type.VAR_INT);
                        for (int j = 0; j < modifierSize; j++) {
                            wrapper.passthrough(Type.UUID);
                            wrapper.passthrough(Type.DOUBLE);
                            wrapper.passthrough(Type.BYTE);
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_16.PLAYER_INFO, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    int action = packetWrapper.passthrough(Type.VAR_INT);
                    int playerCount = packetWrapper.passthrough(Type.VAR_INT);
                    for (int i = 0; i < playerCount; i++) {
                        packetWrapper.passthrough(Type.UUID);
                        if (action == 0) { // Add
                            packetWrapper.passthrough(Type.STRING);
                            int properties = packetWrapper.passthrough(Type.VAR_INT);
                            for (int j = 0; j < properties; j++) {
                                packetWrapper.passthrough(Type.STRING);
                                packetWrapper.passthrough(Type.STRING);
                                if (packetWrapper.passthrough(Type.BOOLEAN)) {
                                    packetWrapper.passthrough(Type.STRING);
                                }
                            }
                            packetWrapper.passthrough(Type.VAR_INT);
                            packetWrapper.passthrough(Type.VAR_INT);
                            if (packetWrapper.passthrough(Type.BOOLEAN)) {
                                // Display Name
                                protocol.getTranslatableRewriter().processText(packetWrapper.passthrough(Type.COMPONENT));
                            }
                        } else if (action == 1) { // Update Game Mode
                            packetWrapper.passthrough(Type.VAR_INT);
                        } else if (action == 2) { // Update Ping
                            packetWrapper.passthrough(Type.VAR_INT);
                        } else if (action == 3) { // Update Display Name
                            if (packetWrapper.passthrough(Type.BOOLEAN)) {
                                // Display name
                                protocol.getTranslatableRewriter().processText(packetWrapper.passthrough(Type.COMPONENT));
                            }
                        } // 4 = Remove Player
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        registerMetaHandler().handle(e -> {
            Metadata meta = e.getData();
            MetaType type = meta.getMetaType();
            if (type == MetaType1_14.Slot) {
                meta.setValue(protocol.getBlockItemPackets().handleItemToClient((Item) meta.getValue()));
            } else if (type == MetaType1_14.BlockID) {
                meta.setValue(protocol.getMappingData().getNewBlockStateId((int) meta.getValue()));
            } else if (type == MetaType1_14.PARTICLE) {
                rewriteParticle((Particle) meta.getValue());
            } else if (type == MetaType1_14.OptChat) {
                JsonElement text = meta.getCastedValue();
                if (text != null) {
                    protocol.getTranslatableRewriter().processText(text);
                }
            }
            return meta;
        });

        mapEntityDirect(Entity1_16Types.EntityType.ZOMBIFIED_PIGLIN, Entity1_15Types.EntityType.ZOMBIE_PIGMAN);
        mapTypes(Entity1_16Types.EntityType.values(), Entity1_15Types.EntityType.class);

        mapEntity(Entity1_16Types.EntityType.HOGLIN, Entity1_16Types.EntityType.COW).jsonName("Hoglin");
        mapEntity(Entity1_16Types.EntityType.ZOGLIN, Entity1_16Types.EntityType.COW).jsonName("Zoglin");
        mapEntity(Entity1_16Types.EntityType.PIGLIN, Entity1_16Types.EntityType.ZOMBIFIED_PIGLIN).jsonName("Piglin");
        mapEntity(Entity1_16Types.EntityType.STRIDER, Entity1_16Types.EntityType.MAGMA_CUBE).jsonName("Strider");

        registerMetaHandler().filter(Entity1_16Types.EntityType.ZOGLIN, 16).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.HOGLIN, 15).removed();

        registerMetaHandler().filter(Entity1_16Types.EntityType.PIGLIN, 16).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.PIGLIN, 17).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.PIGLIN, 18).removed();

        registerMetaHandler().filter(Entity1_16Types.EntityType.STRIDER, 15).handle(meta -> {
            boolean baby = meta.getData().getCastedValue();
            meta.getData().setValue(baby ? 1 : 3);
            meta.getData().setMetaType(MetaType1_14.VarInt);
            return meta.getData();
        });
        registerMetaHandler().filter(Entity1_16Types.EntityType.STRIDER, 16).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.STRIDER, 17).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.STRIDER, 18).removed();

        registerMetaHandler().filter(Entity1_16Types.EntityType.FISHING_BOBBER, 8).removed();

        registerMetaHandler().filter(Entity1_16Types.EntityType.ABSTRACT_ARROW, true, 8).removed();
        registerMetaHandler().filter(Entity1_16Types.EntityType.ABSTRACT_ARROW, true).handle(meta -> {
            if (meta.getIndex() >= 8) {
                meta.getData().setId(meta.getIndex() + 1);
            }
            return meta.getData();
        });
    }

    @Override
    protected EntityType getTypeFromId(int typeId) {
        return Entity1_16Types.getTypeFromId(typeId);
    }
}
