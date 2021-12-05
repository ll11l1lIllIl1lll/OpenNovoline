package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;

import java.util.HashSet;
import java.util.Set;

public class RedstoneConnectionHandler extends ConnectionHandler {
    private static final Set<Integer> redstone = new HashSet<>();
    private static final Int2IntMap connectedBlockStates = new Int2IntOpenHashMap(1296);
    private static final Int2IntMap powerMappings = new Int2IntOpenHashMap(1296);

    static ConnectionData.ConnectorInitAction init() {
        final RedstoneConnectionHandler connectionHandler = new RedstoneConnectionHandler();
        final String redstoneKey = "minecraft:redstone_wire";
        return blockData -> {
            if (!redstoneKey.equals(blockData.getMinecraftKey())) return;
            redstone.add(blockData.getSavedBlockStateId());
            ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), connectionHandler);
            connectedBlockStates.put(getStates(blockData), blockData.getSavedBlockStateId());
            powerMappings.put(blockData.getSavedBlockStateId(), Integer.parseInt(blockData.getValue("power")));
        };
    }

    private static short getStates(WrappedBlockData data) {
        short b = 0;
        b |= getState(data.getValue("east"));
        b |= getState(data.getValue("north")) << 2;
        b |= getState(data.getValue("south")) << 4;
        b |= getState(data.getValue("west")) << 6;
        b |= Integer.parseInt(data.getValue("power")) << 8;
        return b;
    }

    private static int getState(String value) {
        switch (value) {
            case "none":
                return 0;
            case "side":
                return 1;
            case "up":
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        short b = 0;
        b |= connects(user, position, BlockFace.EAST);
        b |= connects(user, position, BlockFace.NORTH) << 2;
        b |= connects(user, position, BlockFace.SOUTH) << 4;
        b |= connects(user, position, BlockFace.WEST) << 6;
        b |= powerMappings.get(blockState) << 8;
        return connectedBlockStates.getOrDefault(b, blockState);
    }

    private int connects(UserConnection user, Position position, BlockFace side) {
        final Position relative = position.getRelative(side);
        int blockState = getBlockData(user, relative);
        if (connects(side, blockState)) {
            return 1; //side
        }
        int up = getBlockData(user, relative.getRelative(BlockFace.TOP));
        if (redstone.contains(up) && !ConnectionData.occludingStates.contains(getBlockData(user, position.getRelative(BlockFace.TOP)))) {
            return 2; //"up"
        }
        int down = getBlockData(user, relative.getRelative(BlockFace.BOTTOM));
        if (redstone.contains(down) && !ConnectionData.occludingStates.contains(getBlockData(user, relative))) {
            return 1; //side
        }
        return 0; //none
    }

    private boolean connects(BlockFace side, int blockState) {
        final BlockData blockData = ConnectionData.blockConnectionData.get(blockState);
        return blockData != null && blockData.connectsTo("redstoneConnections", side.opposite(), false);
    }
}
