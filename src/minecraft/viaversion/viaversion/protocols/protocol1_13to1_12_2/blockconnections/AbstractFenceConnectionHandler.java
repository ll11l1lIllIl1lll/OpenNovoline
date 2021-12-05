package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.protocol.ProtocolVersion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFenceConnectionHandler extends ConnectionHandler {
    private static final StairConnectionHandler STAIR_CONNECTION_HANDLER = new StairConnectionHandler();
    private final String blockConnections;
    private final Set<Integer> blockStates = new HashSet<>();
    private final Map<Byte, Integer> connectedBlockStates = new HashMap<>();

    protected AbstractFenceConnectionHandler(String blockConnections) {
        this.blockConnections = blockConnections;
    }

    public ConnectionData.ConnectorInitAction getInitAction(final String key) {
        final AbstractFenceConnectionHandler handler = this;
        return blockData -> {
            if (key.equals(blockData.getMinecraftKey())) {
                if (blockData.hasData("waterlogged") && blockData.getValue("waterlogged").equals("true")) return;
                blockStates.add(blockData.getSavedBlockStateId());
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
                connectedBlockStates.put(getStates(blockData), blockData.getSavedBlockStateId());
            }
        };
    }

    protected byte getStates(WrappedBlockData blockData) {
        byte states = 0;
        if (blockData.getValue("east").equals("true")) states |= 1;
        if (blockData.getValue("north").equals("true")) states |= 2;
        if (blockData.getValue("south").equals("true")) states |= 4;
        if (blockData.getValue("west").equals("true")) states |= 8;
        return states;
    }

    protected byte getStates(UserConnection user, Position position, int blockState) {
        byte states = 0;
        boolean pre1_12 = user.getProtocolInfo().getServerProtocolVersion() < ProtocolVersion.v1_12.getVersion();
        if (connects(BlockFace.EAST, getBlockData(user, position.getRelative(BlockFace.EAST)), pre1_12)) states |= 1;
        if (connects(BlockFace.NORTH, getBlockData(user, position.getRelative(BlockFace.NORTH)), pre1_12)) states |= 2;
        if (connects(BlockFace.SOUTH, getBlockData(user, position.getRelative(BlockFace.SOUTH)), pre1_12)) states |= 4;
        if (connects(BlockFace.WEST, getBlockData(user, position.getRelative(BlockFace.WEST)), pre1_12)) states |= 8;
        return states;
    }

    @Override
    public int getBlockData(UserConnection user, Position position) {
        return STAIR_CONNECTION_HANDLER.connect(user, position, super.getBlockData(user, position));
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        final Integer newBlockState = connectedBlockStates.get(getStates(user, position, blockState));
        return newBlockState == null ? blockState : newBlockState;
    }

    protected boolean connects(BlockFace side, int blockState, boolean pre1_12) {
        if (blockStates.contains(blockState)) return true;
        if (blockConnections == null) return false;

        BlockData blockData = ConnectionData.blockConnectionData.get(blockState);
        return blockData != null && blockData.connectsTo(blockConnections, side.opposite(), pre1_12);
    }

    public Set<Integer> getBlockStates() {
        return blockStates;
    }
}
