package viaversion.viaversion.protocols.protocol1_11_1to1_11;

import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.protocols.protocol1_11_1to1_11.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;

public class Protocol1_11_1To1_11 extends Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {

    public Protocol1_11_1To1_11() {
        super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
    }

    @Override
    protected void registerPackets() {
        InventoryPackets.register(this);
    }

}
