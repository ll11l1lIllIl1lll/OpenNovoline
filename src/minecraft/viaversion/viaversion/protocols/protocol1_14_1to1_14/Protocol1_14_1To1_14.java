package viaversion.viaversion.protocols.protocol1_14_1to1_14;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.protocols.protocol1_14_1to1_14.metadata.MetadataRewriter1_14_1To1_14;
import viaversion.viaversion.protocols.protocol1_14_1to1_14.packets.EntityPackets;
import viaversion.viaversion.protocols.protocol1_14_1to1_14.storage.EntityTracker1_14_1;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class Protocol1_14_1To1_14 extends Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {

    public Protocol1_14_1To1_14() {
        super(ClientboundPackets1_14.class, ClientboundPackets1_14.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
    }

    @Override
    protected void registerPackets() {
        MetadataRewriter metadataRewriter = new MetadataRewriter1_14_1To1_14(this);

        EntityPackets.register(this);
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new EntityTracker1_14_1(userConnection));
    }
}
