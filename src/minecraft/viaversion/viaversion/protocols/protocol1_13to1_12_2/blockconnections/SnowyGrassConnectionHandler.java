package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import viaversion.viaversion.api.Pair;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SnowyGrassConnectionHandler extends ConnectionHandler {
    private static final Map<Pair<Integer, Boolean>, Integer> grassBlocks = new HashMap<>();
    private static final Set<Integer> snows = new HashSet<>();

    static ConnectionData.ConnectorInitAction init() {
        final Set<String> snowyGrassBlocks = new HashSet<>();
        snowyGrassBlocks.add("minecraft:grass_block");
        snowyGrassBlocks.add("minecraft:podzol");
        snowyGrassBlocks.add("minecraft:mycelium");

        final SnowyGrassConnectionHandler handler = new SnowyGrassConnectionHandler();
        return blockData -> {
            if (snowyGrassBlocks.contains(blockData.getMinecraftKey())) {
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
                blockData.set("snowy", "true");
                grassBlocks.put(new Pair<>(blockData.getSavedBlockStateId(), true), blockData.getBlockStateId());
                blockData.set("snowy", "false");
                grassBlocks.put(new Pair<>(blockData.getSavedBlockStateId(), false), blockData.getBlockStateId());
            }
            if (blockData.getMinecraftKey().equals("minecraft:snow") || blockData.getMinecraftKey().equals("minecraft:snow_block")) {
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
                snows.add(blockData.getSavedBlockStateId());
            }
        };
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        int blockUpId = getBlockData(user, position.getRelative(BlockFace.TOP));
        Integer newId = grassBlocks.get(new Pair<>(blockState, snows.contains(blockUpId)));
        if (newId != null) {
            return newId;
        }
        return blockState;
    }
}