package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;

import java.util.HashSet;
import java.util.Set;


public class FlowerConnectionHandler extends ConnectionHandler {
    private static final Int2IntMap flowers = new Int2IntOpenHashMap();

    static ConnectionData.ConnectorInitAction init() {
        final Set<String> baseFlower = new HashSet<>();
        baseFlower.add("minecraft:rose_bush");
        baseFlower.add("minecraft:sunflower");
        baseFlower.add("minecraft:peony");
        baseFlower.add("minecraft:tall_grass");
        baseFlower.add("minecraft:large_fern");
        baseFlower.add("minecraft:lilac");

        final FlowerConnectionHandler handler = new FlowerConnectionHandler();
        return blockData -> {
            if (baseFlower.contains(blockData.getMinecraftKey())) {
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
                if (blockData.getValue("half").equals("lower")) {
                    blockData.set("half", "upper");
                    flowers.put(blockData.getSavedBlockStateId(), blockData.getBlockStateId());
                }
            }
        };
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        int blockBelowId = getBlockData(user, position.getRelative(BlockFace.BOTTOM));
        int connectBelow = flowers.get(blockBelowId);
        if (connectBelow != 0) {
            int blockAboveId = getBlockData(user, position.getRelative(BlockFace.TOP));
            if (Via.getConfig().isStemWhenBlockAbove()) {
                if (blockAboveId == 0) {
                    return connectBelow;
                }
            } else if (!flowers.containsKey(blockAboveId)) {
                return connectBelow;
            }
        }
        return blockState;
    }
}
