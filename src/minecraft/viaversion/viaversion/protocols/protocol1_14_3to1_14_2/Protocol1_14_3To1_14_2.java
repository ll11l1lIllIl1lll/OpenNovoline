package viaversion.viaversion.protocols.protocol1_14_3to1_14_2;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class Protocol1_14_3To1_14_2 extends Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {

    public Protocol1_14_3To1_14_2() {
        super(ClientboundPackets1_14.class, ClientboundPackets1_14.class, null, null);
    }

    @Override
    protected void registerPackets() {
        registerOutgoing(ClientboundPackets1_14.TRADE_LIST, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.passthrough(Type.VAR_INT);
                        int size = wrapper.passthrough(Type.UNSIGNED_BYTE);
                        for (int i = 0; i < size; i++) {
                            wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                            wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                            if (wrapper.passthrough(Type.BOOLEAN)) {
                                wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                            }
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.FLOAT);
                        }
                        wrapper.passthrough(Type.VAR_INT);
                        wrapper.passthrough(Type.VAR_INT);
                        boolean regularVillager = wrapper.passthrough(Type.BOOLEAN);
                        wrapper.write(Type.BOOLEAN, regularVillager); // new boolean added in pre-1
                    }
                });
            }
        });
    }
}
