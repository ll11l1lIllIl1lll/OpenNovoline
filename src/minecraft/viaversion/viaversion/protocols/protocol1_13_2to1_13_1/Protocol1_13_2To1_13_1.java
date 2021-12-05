package viaversion.viaversion.protocols.protocol1_13_2to1_13_1;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.EntityPackets;
import viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.WorldPackets;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;

public class Protocol1_13_2To1_13_1 extends Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {

    public Protocol1_13_2To1_13_1() {
        super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
    }

    @Override
    protected void registerPackets() {
        InventoryPackets.register(this);
        WorldPackets.register(this);
        EntityPackets.register(this);

        registerIncoming(ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLAT_VAR_INT_ITEM, Type.FLAT_ITEM);
            }
        });

        registerOutgoing(ClientboundPackets1_13.ADVANCEMENTS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.passthrough(Type.BOOLEAN); // Reset/clear
                        int size = wrapper.passthrough(Type.VAR_INT); // Mapping size

                        for (int i = 0; i < size; i++) {
                            wrapper.passthrough(Type.STRING); // Identifier

                            // Parent
                            if (wrapper.passthrough(Type.BOOLEAN))
                                wrapper.passthrough(Type.STRING);

                            // Display data
                            if (wrapper.passthrough(Type.BOOLEAN)) {
                                wrapper.passthrough(Type.COMPONENT); // Title
                                wrapper.passthrough(Type.COMPONENT); // Description
                                Item icon = wrapper.read(Type.FLAT_ITEM);
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, icon);
                                wrapper.passthrough(Type.VAR_INT); // Frame type
                                int flags = wrapper.passthrough(Type.INT); // Flags
                                if ((flags & 1) != 0) {
                                    wrapper.passthrough(Type.STRING); // Background texture
                                }
                                wrapper.passthrough(Type.FLOAT); // X
                                wrapper.passthrough(Type.FLOAT); // Y
                            }

                            wrapper.passthrough(Type.STRING_ARRAY); // Criteria

                            int arrayLength = wrapper.passthrough(Type.VAR_INT);
                            for (int array = 0; array < arrayLength; array++) {
                                wrapper.passthrough(Type.STRING_ARRAY); // String array
                            }
                        }
                    }
                });
            }
        });
    }
}
