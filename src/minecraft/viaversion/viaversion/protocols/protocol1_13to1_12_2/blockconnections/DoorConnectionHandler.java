package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;

import java.util.*;

public class DoorConnectionHandler extends ConnectionHandler {
    private static final Map<Integer, DoorData> doorDataMap = new HashMap<>();
    private static final Map<Short, Integer> connectedStates = new HashMap<>();

    static ConnectionData.ConnectorInitAction init() {
        final List<String> baseDoors = new LinkedList<>();
        baseDoors.add("minecraft:oak_door");
        baseDoors.add("minecraft:birch_door");
        baseDoors.add("minecraft:jungle_door");
        baseDoors.add("minecraft:dark_oak_door");
        baseDoors.add("minecraft:acacia_door");
        baseDoors.add("minecraft:spruce_door");
        baseDoors.add("minecraft:iron_door");

        final DoorConnectionHandler connectionHandler = new DoorConnectionHandler();
        return blockData -> {
            int type = baseDoors.indexOf(blockData.getMinecraftKey());
            if (type == -1) return;

            int id = blockData.getSavedBlockStateId();

            DoorData doorData = new DoorData(
                    blockData.getValue("half").equals("lower"),
                    blockData.getValue("hinge").equals("right"),
                    blockData.getValue("powered").equals("true"),
                    blockData.getValue("open").equals("true"),
                    BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)),
                    type
            );

            doorDataMap.put(id, doorData);

            connectedStates.put(getStates(doorData), id);

            ConnectionData.connectionHandlerMap.put(id, connectionHandler);
        };
    }

    private static short getStates(DoorData doorData) {
        short s = 0;
        if (doorData.isLower()) s |= 1;
        if (doorData.isOpen()) s |= 2;
        if (doorData.isPowered()) s |= 4;
        if (doorData.isRightHinge()) s |= 8;
        s |= doorData.getFacing().ordinal() << 4;
        s |= (doorData.getType() & 0x7) << 6;
        return s;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        DoorData doorData = doorDataMap.get(blockState);
        if (doorData == null) return blockState;
        short s = 0;
        s |= (doorData.getType() & 0x7) << 6;
        if (doorData.isLower()) {
            DoorData upperHalf = doorDataMap.get(getBlockData(user, position.getRelative(BlockFace.TOP)));
            if (upperHalf == null) return blockState;
            s |= 1;
            if (doorData.isOpen()) s |= 2;
            if (upperHalf.isPowered()) s |= 4;
            if (upperHalf.isRightHinge()) s |= 8;
            s |= doorData.getFacing().ordinal() << 4;
        } else {
            DoorData lowerHalf = doorDataMap.get(getBlockData(user, position.getRelative(BlockFace.BOTTOM)));
            if (lowerHalf == null) return blockState;
            if (lowerHalf.isOpen()) s |= 2;
            if (doorData.isPowered()) s |= 4;
            if (doorData.isRightHinge()) s |= 8;
            s |= lowerHalf.getFacing().ordinal() << 4;
        }

        Integer newBlockState = connectedStates.get(s);
        return newBlockState == null ? blockState : newBlockState;
    }

    private static final class DoorData {
        private final boolean lower, rightHinge, powered, open;
        private final BlockFace facing;
        private final int type;

        private DoorData(boolean lower, boolean rightHinge, boolean powered, boolean open, BlockFace facing, int type) {
            this.lower = lower;
            this.rightHinge = rightHinge;
            this.powered = powered;
            this.open = open;
            this.facing = facing;
            this.type = type;
        }

        public boolean isLower() {
            return lower;
        }

        public boolean isRightHinge() {
            return rightHinge;
        }

        public boolean isPowered() {
            return powered;
        }

        public boolean isOpen() {
            return open;
        }

        public BlockFace getFacing() {
            return facing;
        }

        public int getType() {
            return type;
        }
    }
}
