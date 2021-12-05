package viaversion.viaversion.api.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.platform.ViaPlatform;
import viaversion.viaversion.packets.Direction;
import viaversion.viaversion.packets.State;
import viaversion.viaversion.protocols.base.ProtocolInfo;

public class ProtocolPipeline extends SimpleProtocol {

    private List<Protocol> protocolList;
    private UserConnection userConnection;

    public ProtocolPipeline(UserConnection userConnection) {
        init(userConnection);
    }

    @Override
    protected void registerPackets() {
        protocolList = new CopyOnWriteArrayList<>();
        // This is a pipeline so we register basic pipes
        protocolList.add(ProtocolRegistry.BASE_PROTOCOL);
    }

    @Override
    public void init(UserConnection userConnection) {
        this.userConnection = userConnection;

        ProtocolInfo protocolInfo = new ProtocolInfo(userConnection);
        protocolInfo.setPipeline(this);

        userConnection.setProtocolInfo(protocolInfo);

        /* Init through all our pipes */
        for(Protocol protocol : protocolList) {
            protocol.init(userConnection);
        }
    }

    /**
     * Add a protocol to the current pipeline
     * This will call the {@link Protocol#init(UserConnection)} method.
     *
     * @param protocol The protocol to add to the end
     */
    public void add(Protocol protocol) {
        if(protocolList != null) {
            protocolList.add(protocol);
            protocol.init(userConnection);
            // Move base Protocols to the end, so the login packets can be modified by other protocols
            List<Protocol> toMove = new ArrayList<>();
            for(Protocol p : protocolList) {
                if(ProtocolRegistry.isBaseProtocol(p)) {
                    toMove.add(p);
                }
            }
            protocolList.removeAll(toMove);
            protocolList.addAll(toMove);
        } else {
            throw new NullPointerException("Tried to add protocol too early");
        }
    }

    @Override
    public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
        int originalID = packetWrapper.getId();

        // Apply protocols
        packetWrapper.apply(direction, state, 0, protocolList, direction == Direction.OUTGOING);
        super.transform(direction, state, packetWrapper);

        if(Via.getManager().isDebug()) {
            logPacket(direction, state, packetWrapper, originalID);
        }
    }

    private void logPacket(Direction direction, State state, PacketWrapper packetWrapper, int originalID) {
        // Debug packet
        int clientProtocol = userConnection.getProtocolInfo().getProtocolVersion();
        ViaPlatform platform = Via.getPlatform();

        String actualUsername = packetWrapper.user().getProtocolInfo().getUsername();
        String username = actualUsername != null ? actualUsername + " " : "";

        platform.getLogger().log(Level.INFO, "{0}{1} {2}: {3} (0x{4}) -> {5} (0x{6}) [{7}] {8}",
                new Object[]{
                        username,
                        direction,
                        state,
                        originalID,
                        Integer.toHexString(originalID),
                        packetWrapper.getId(),
                        Integer.toHexString(packetWrapper.getId()),
                        Integer.toString(clientProtocol),
                        packetWrapper
                });
    }

    /**
     * Check if the pipeline contains a protocol
     *
     * @param pipeClass The class to check
     * @return True if the protocol class is in the pipeline
     */
    public boolean contains(Class<? extends Protocol> pipeClass) {
        for(Protocol protocol : protocolList) {
            if(protocol.getClass() == pipeClass) return true;
        }
        return false;
    }

    public <P extends Protocol> P getProtocol(Class<P> pipeClass) {
        for(Protocol protocol : protocolList) {
            if(protocol.getClass() == pipeClass) return (P) protocol;
        }
        return null;
    }

    /**
     * Use the pipeline to filter a NMS packet
     *
     * @param o    The NMS packet object
     * @param list The output list to write to
     *
     * @return If it should not write the input object to te list.
     *
     * @throws Exception If it failed to convert / packet cancelld.
     */
    public boolean filter(Object o, List list) throws Exception {
        for(Protocol protocol : protocolList) {
            if(protocol.isFiltered(o.getClass())) {
                protocol.filterPacket(userConnection, o, list);
                return true;
            }
        }

        return false;
    }

    public List<Protocol> pipes() {
        return protocolList;
    }

    /**
     * Cleans the pipe and adds {@link viaversion.viaversion.protocols.base.BaseProtocol}
     * /!\ WARNING - It doesn't add version-specific base Protocol
     */
    public void cleanPipes() {
        pipes().clear();
        registerPackets();
    }
}
