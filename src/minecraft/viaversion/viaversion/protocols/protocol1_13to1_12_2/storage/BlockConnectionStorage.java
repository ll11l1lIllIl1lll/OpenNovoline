package viaversion.viaversion.protocols.protocol1_13to1_12_2.storage;

import viaversion.viaversion.api.Pair;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlockConnectionStorage extends StoredObject {
    private static final short[] REVERSE_BLOCK_MAPPINGS = new short[8582];
    private static Constructor<?> fastUtilLongObjectHashMap;

    private final Map<Long, Pair<byte[], NibbleArray>> blockStorage = createLongObjectMap();

    static {
        try {
            //noinspection StringBufferReplaceableByString - prevent relocation
            String className = new StringBuilder("it").append(".unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap").toString();
            fastUtilLongObjectHashMap = Class.forName(className).getConstructor();
            Via.getPlatform().getLogger().info("Using FastUtil Long2ObjectOpenHashMap for block connections");
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }

        Arrays.fill(REVERSE_BLOCK_MAPPINGS, (short) -1);
        for (int i = 0; i < 4096; i++) {
            int newBlock = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(i);
            if (newBlock != -1) {
                REVERSE_BLOCK_MAPPINGS[newBlock] = (short) i;
            }
        }
    }

    public BlockConnectionStorage(UserConnection user) {
        super(user);
    }

    public void store(int x, int y, int z, int blockState) {
        short mapping = REVERSE_BLOCK_MAPPINGS[blockState];
        if (mapping == -1) return;

        blockState = mapping;
        long pair = getChunkSectionIndex(x, y, z);
        Pair<byte[], NibbleArray> map = getChunkSection(pair, (blockState & 0xF) != 0);
        int blockIndex = encodeBlockPos(x, y, z);
        map.getKey()[blockIndex] = (byte) (blockState >> 4);
        NibbleArray nibbleArray = map.getValue();
        if (nibbleArray != null) {
            nibbleArray.set(blockIndex, blockState);
        }
    }

    public int get(int x, int y, int z) {
        long pair = getChunkSectionIndex(x, y, z);
        Pair<byte[], NibbleArray> map = blockStorage.get(pair);
        if (map == null) return 0;
        short blockPosition = encodeBlockPos(x, y, z);
        NibbleArray nibbleArray = map.getValue();
        return WorldPackets.toNewId(
                ((map.getKey()[blockPosition] & 0xFF) << 4)
                        | (nibbleArray == null ? 0 : nibbleArray.get(blockPosition))
        );
    }

    public void remove(int x, int y, int z) {
        long pair = getChunkSectionIndex(x, y, z);
        Pair<byte[], NibbleArray> map = blockStorage.get(pair);
        if (map == null) return;
        int blockIndex = encodeBlockPos(x, y, z);
        NibbleArray nibbleArray = map.getValue();
        if (nibbleArray != null) {
            nibbleArray.set(blockIndex, 0);
            boolean allZero = true;
            for (int i = 0; i < 4096; i++) {
                if (nibbleArray.get(i) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero) map.setValue(null);
        }
        map.getKey()[blockIndex] = 0;
        for (short entry : map.getKey()) {
            if (entry != 0) return;
        }
        blockStorage.remove(pair);
    }

    public void clear() {
        blockStorage.clear();
    }

    public void unloadChunk(int x, int z) {
        for (int y = 0; y < 256; y += 16) {
            blockStorage.remove(getChunkSectionIndex(x << 4, y, z << 4));
        }
    }

    private Pair<byte[], NibbleArray> getChunkSection(long index, boolean requireNibbleArray) {
        Pair<byte[], NibbleArray> map = blockStorage.get(index);
        if (map == null) {
            map = new Pair<>(new byte[4096], null);
            blockStorage.put(index, map);
        }
        if (map.getValue() == null && requireNibbleArray) {
            map.setValue(new NibbleArray(4096));
        }
        return map;
    }

    private long getChunkSectionIndex(int x, int y, int z) {
        return (((x >> 4) & 0x3FFFFFFL) << 38) | (((y >> 4) & 0xFFFL) << 26) | ((z >> 4) & 0x3FFFFFFL);
    }

    private long getChunkSectionIndex(Position position) {
        return getChunkSectionIndex(position.getX(), position.getY(), position.getZ());
    }

    private short encodeBlockPos(int x, int y, int z) {
        return (short) (((y & 0xF) << 8) | ((x & 0xF) << 4) | (z & 0xF));
    }

    private short encodeBlockPos(Position pos) {
        return encodeBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private <T> Map<Long, T> createLongObjectMap() {
        if (fastUtilLongObjectHashMap != null) {
            try {
                return (Map<Long, T>) fastUtilLongObjectHashMap.newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }
}
