package viaversion.viabackwards.protocol.protocol1_13_1to1_13_2.packets;

import viaversion.viabackwards.protocol.protocol1_13_1to1_13_2.Protocol1_13_1To1_13_2;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13_2;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_13;
import viaversion.viaversion.api.type.types.version.Types1_13_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class EntityPackets1_13_2 {


    public static void register(Protocol1_13_1To1_13_2 protocol) {
        protocol.registerOutgoing(ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
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
                map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST); // 12 - Metadata

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                            if (metadata.getMetaType() == MetaType1_13_2.Slot) {
                                metadata.setMetaType(MetaType1_13.Slot);
                            }
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID
                map(Type.DOUBLE); // 2 - X
                map(Type.DOUBLE); // 3 - Y
                map(Type.DOUBLE); // 4 - Z
                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch
                map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST); // 7 - Metadata

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                            if (metadata.getMetaType() == MetaType1_13_2.Slot) {
                                metadata.setMetaType(MetaType1_13.Slot);
                            }
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST); // 1 - Metadata list

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                            if (metadata.getMetaType() == MetaType1_13_2.Slot) {
                                metadata.setMetaType(MetaType1_13.Slot);
                            }
                        }
                    }
                });
            }
        });
    }

}
