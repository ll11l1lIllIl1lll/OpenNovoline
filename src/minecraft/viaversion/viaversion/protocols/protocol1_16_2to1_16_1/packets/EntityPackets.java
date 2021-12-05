package viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.entities.Entity1_16_2Types;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_14;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.metadata.MetadataRewriter1_16_2To1_16_1;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.storage.EntityTracker1_16_2;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;

public class EntityPackets {

    public static void register(Protocol1_16_2To1_16_1 protocol) {
        MetadataRewriter1_16_2To1_16_1 metadataRewriter = protocol.get(MetadataRewriter1_16_2To1_16_1.class);
        metadataRewriter.registerSpawnTrackerWithData(ClientboundPackets1_16.SPAWN_ENTITY, Entity1_16_2Types.EntityType.FALLING_BLOCK);
        metadataRewriter.registerTracker(ClientboundPackets1_16.SPAWN_MOB);
        metadataRewriter.registerTracker(ClientboundPackets1_16.SPAWN_PLAYER, Entity1_16_2Types.EntityType.PLAYER);
        metadataRewriter.registerMetadataRewriter(ClientboundPackets1_16.ENTITY_METADATA, Types1_14.METADATA_LIST);
        metadataRewriter.registerEntityDestroy(ClientboundPackets1_16.DESTROY_ENTITIES);

        protocol.registerOutgoing(ClientboundPackets1_16.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity ID
                handler(wrapper -> {
                    short gamemode = wrapper.read(Type.UNSIGNED_BYTE);
                    wrapper.write(Type.BOOLEAN, (gamemode & 0x08) != 0); // Hardcore

                    gamemode &= ~0x08;
                    wrapper.write(Type.UNSIGNED_BYTE, gamemode);
                });
                map(Type.BYTE); // Previous Gamemode
                map(Type.STRING_ARRAY); // World List
                handler(wrapper -> {
                    // Throw away the old dimension registry, extra conversion would be too hard of a hit
                    wrapper.read(Type.NBT);
                    wrapper.write(Type.NBT, protocol.getMappingData().getDimensionRegistry());

                    // Instead of the dimension's resource key, it now just wants the data directly
                    String dimensionType = wrapper.read(Type.STRING);
                    wrapper.write(Type.NBT, getDimensionData(dimensionType));
                });
                map(Type.STRING); // Dimension
                map(Type.LONG); // Seed
                map(Type.UNSIGNED_BYTE, Type.VAR_INT); // Max players
                // ...
                handler(wrapper -> {
                    wrapper.user().get(EntityTracker1_16_2.class).addEntity(wrapper.get(Type.INT, 0), Entity1_16_2Types.EntityType.PLAYER);
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_16.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    String dimensionType = wrapper.read(Type.STRING);
                    wrapper.write(Type.NBT, getDimensionData(dimensionType));
                });
            }
        });
    }

    public static CompoundTag getDimensionData(String dimensionType) {
        CompoundTag tag = Protocol1_16_2To1_16_1.MAPPINGS.getDimensionDataMap().get(dimensionType);
        if (tag == null) {
            Via.getPlatform().getLogger().severe("Could not get dimension data of " + dimensionType);
            throw new NullPointerException("Dimension data for " + dimensionType + " is null!");
        }
        return tag;
    }
}
