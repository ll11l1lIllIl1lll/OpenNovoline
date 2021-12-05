package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.MappingDataLoader;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockChangeRecord1_8;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.minecraft.chunks.Chunk;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.PacketBlockConnectionProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class ConnectionData {

    private static final BlockChangeRecord1_8[] EMPTY_RECORDS = new BlockChangeRecord1_8[0];
    public static BlockConnectionProvider blockConnectionProvider;
    static Int2ObjectMap<String> idToKey = new Int2ObjectOpenHashMap<>(8582, 1F);
    public static Map<String, Integer> keyToId = new HashMap<>(8582, 1F);
    static Int2ObjectMap<ConnectionHandler> connectionHandlerMap = new Int2ObjectOpenHashMap<>(1);
    static Int2ObjectMap<BlockData> blockConnectionData = new Int2ObjectOpenHashMap<>(1);
    static IntSet occludingStates = new IntOpenHashSet(377, 1F);

    public static void update(UserConnection user, Position position) {
        for(BlockFace face : BlockFace.values()) {
            Position pos = position.getRelative(face);
            int blockState = blockConnectionProvider.getBlockData(user, pos.getX(), pos.getY(), pos.getZ());
            ConnectionHandler handler = connectionHandlerMap.get(blockState);
            if(handler == null) continue;

            int newBlockState = handler.connect(user, pos, blockState);
            PacketWrapper blockUpdatePacket = new PacketWrapper(0x0B, null, user);
            blockUpdatePacket.write(Type.POSITION, pos);
            blockUpdatePacket.write(Type.VAR_INT, newBlockState);
            try {
                blockUpdatePacket.send(Protocol1_13To1_12_2.class, true, true);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateChunkSectionNeighbours(UserConnection user, int chunkX, int chunkZ, int chunkSectionY) {
        for(int chunkDeltaX = -1; chunkDeltaX <= 1; chunkDeltaX++) {
            for(int chunkDeltaZ = -1; chunkDeltaZ <= 1; chunkDeltaZ++) {
                if(Math.abs(chunkDeltaX) + Math.abs(chunkDeltaZ) == 0) continue;

                List<BlockChangeRecord1_8> updates = new ArrayList<>();

                if(Math.abs(chunkDeltaX) + Math.abs(chunkDeltaZ) == 2) { // Corner
                    for(int blockY = chunkSectionY * 16; blockY < chunkSectionY * 16 + 16; blockY++) {
                        int blockPosX = chunkDeltaX == 1 ? 0 : 15;
                        int blockPosZ = chunkDeltaZ == 1 ? 0 : 15;
                        updateBlock(user, new Position(((chunkX + chunkDeltaX) << 4) + blockPosX, (short) blockY,
                                ((chunkZ + chunkDeltaZ) << 4) + blockPosZ), updates);
                    }
                } else {
                    for(int blockY = chunkSectionY * 16; blockY < chunkSectionY * 16 + 16; blockY++) {
                        int xStart;
                        int xEnd;
                        int zStart;
                        int zEnd;
                        if(chunkDeltaX == 1) {
                            xStart = 0;
                            xEnd = 2;
                            zStart = 0;
                            zEnd = 16;
                        } else if(chunkDeltaX == -1) {
                            xStart = 14;
                            xEnd = 16;
                            zStart = 0;
                            zEnd = 16;
                        } else if(chunkDeltaZ == 1) {
                            xStart = 0;
                            xEnd = 16;
                            zStart = 0;
                            zEnd = 2;
                        } else {
                            xStart = 0;
                            xEnd = 16;
                            zStart = 14;
                            zEnd = 16;
                        }
                        for(int blockX = xStart; blockX < xEnd; blockX++) {
                            for(int blockZ = zStart; blockZ < zEnd; blockZ++) {
                                updateBlock(user, new Position(((chunkX + chunkDeltaX) << 4) + blockX, (short) blockY,
                                        ((chunkZ + chunkDeltaZ) << 4) + blockZ), updates);
                            }
                        }
                    }
                }

                if(!updates.isEmpty()) {
                    PacketWrapper wrapper = new PacketWrapper(0x0F, null, user);
                    wrapper.write(Type.INT, chunkX + chunkDeltaX);
                    wrapper.write(Type.INT, chunkZ + chunkDeltaZ);
                    wrapper.write(Type.BLOCK_CHANGE_RECORD_ARRAY, updates.toArray(EMPTY_RECORDS));
                    try {
                        wrapper.send(Protocol1_13To1_12_2.class, true, true);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void updateBlock(UserConnection user, Position pos, List<BlockChangeRecord1_8> records) {
        int blockState = blockConnectionProvider.getBlockData(user, pos.getX(), pos.getY(), pos.getZ());
        ConnectionHandler handler = getConnectionHandler(blockState);
        if(handler == null) return;

        int newBlockState = handler.connect(user, pos, blockState);
        records.add(new BlockChangeRecord1_8(pos.getX() & 0xF, pos.getY(), pos.getZ() & 0xF, newBlockState));
    }

    public static void updateBlockStorage(UserConnection userConnection, int x, int y, int z, int blockState) {
        if(!needStoreBlocks()) return;
        if(ConnectionData.isWelcome(blockState)) {
            blockConnectionProvider.storeBlock(userConnection, x, y, z, blockState);
        } else {
            blockConnectionProvider.removeBlock(userConnection, x, y, z);
        }
    }

    public static void clearBlockStorage(UserConnection connection) {
        if (!needStoreBlocks()) return;
        blockConnectionProvider.clearStorage(connection);
    }

    public static boolean needStoreBlocks() {
        return blockConnectionProvider.storesBlocks();
    }

    public static void connectBlocks(UserConnection user, Chunk chunk) {
        long xOff = chunk.getX() << 4;
        long zOff = chunk.getZ() << 4;

        for (int i = 0; i < chunk.getSections().length; i++) {
            ChunkSection section = chunk.getSections()[i];
            if (section == null) continue;

            boolean willConnect = false;

            for (int p = 0; p < section.getPaletteSize(); p++) {
                int id = section.getPaletteEntry(p);
                if (ConnectionData.connects(id)) {
                    willConnect = true;
                    break;
                }
            }
            if (!willConnect) continue;

            long yOff = i << 4;

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int block = section.getFlatBlock(x, y, z);

                        ConnectionHandler handler = ConnectionData.getConnectionHandler(block);
                        if (handler != null) {
                            block = handler.connect(user, new Position(
                                    (int) (xOff + x),
                                    (short) (yOff + y),
                                    (int) (zOff + z)
                            ), block);
                            section.setFlatBlock(x, y, z, block);
                        }
                    }
                }
            }
        }
    }

    public static void init() {
        if (!Via.getConfig().isServersideBlockConnections()) return;

        Via.getPlatform().getLogger().info("Loading block connection mappings ...");
        JsonObject mapping1_13 = MappingDataLoader.loadData("mapping-1.13.json", true);
        JsonObject blocks1_13 = mapping1_13.getAsJsonObject("blockstates");
        for (Entry<String, JsonElement> blockState : blocks1_13.entrySet()) {
            int id = Integer.parseInt(blockState.getKey());
            String key = blockState.getValue().getAsString();
            idToKey.put(id, key);
            keyToId.put(key, id);
        }

        connectionHandlerMap = new Int2ObjectOpenHashMap<>(3650, 1F);

        if (!Via.getConfig().isReduceBlockStorageMemory()) {
            blockConnectionData = new Int2ObjectOpenHashMap<>(1146, 1F);
            JsonObject mappingBlockConnections = MappingDataLoader.loadData("blockConnections.json");
            for (Entry<String, JsonElement> entry : mappingBlockConnections.entrySet()) {
                int id = keyToId.get(entry.getKey());
                BlockData blockData = new BlockData();
                for (Entry<String, JsonElement> type : entry.getValue().getAsJsonObject().entrySet()) {
                    String name = type.getKey();
                    JsonObject object = type.getValue().getAsJsonObject();
                    boolean[] data = new boolean[6];
                    for (BlockFace value : BlockFace.values()) {
                        String face = value.toString().toLowerCase(Locale.ROOT);
                        if (object.has(face)) {
                            data[value.ordinal()] = object.getAsJsonPrimitive(face).getAsBoolean();
                        }
                    }
                    blockData.put(name, data);
                }
                if (entry.getKey().contains("stairs")) {
                    blockData.put("allFalseIfStairPre1_12", new boolean[6]);
                }
                blockConnectionData.put(id, blockData);
            }
        }

        JsonObject blockData = MappingDataLoader.loadData("blockData.json");
        JsonArray occluding = blockData.getAsJsonArray("occluding");
        for (JsonElement jsonElement : occluding) {
            occludingStates.add(keyToId.get(jsonElement.getAsString()).intValue());
        }

        List<ConnectorInitAction> initActions = new ArrayList<>();
        initActions.add(PumpkinConnectionHandler.init());
        initActions.addAll(BasicFenceConnectionHandler.init());
        initActions.add(NetherFenceConnectionHandler.init());
        initActions.addAll(WallConnectionHandler.init());
        initActions.add(MelonConnectionHandler.init());
        initActions.addAll(GlassConnectionHandler.init());
        initActions.add(ChestConnectionHandler.init());
        initActions.add(DoorConnectionHandler.init());
        initActions.add(RedstoneConnectionHandler.init());
        initActions.add(StairConnectionHandler.init());
        initActions.add(FlowerConnectionHandler.init());
        initActions.addAll(ChorusPlantConnectionHandler.init());
        initActions.add(TripwireConnectionHandler.init());
        initActions.add(SnowyGrassConnectionHandler.init());
        initActions.add(FireConnectionHandler.init());
        if (Via.getConfig().isVineClimbFix()) {
            initActions.add(VineConnectionHandler.init());
        }

        for (String key : keyToId.keySet()) {
            WrappedBlockData wrappedBlockData = WrappedBlockData.fromString(key);
            for (ConnectorInitAction action : initActions) {
                action.check(wrappedBlockData);
            }
        }

        if (Via.getConfig().getBlockConnectionMethod().equalsIgnoreCase("packet")) {
            blockConnectionProvider = new PacketBlockConnectionProvider();
            Via.getManager().getProviders().register(BlockConnectionProvider.class, blockConnectionProvider);
        }
    }

    public static boolean isWelcome(int blockState) {
        return blockConnectionData.containsKey(blockState) || connectionHandlerMap.containsKey(blockState);
    }

    public static boolean connects(int blockState) {
        return connectionHandlerMap.containsKey(blockState);
    }

    public static int connect(UserConnection user, Position position, int blockState) {
        ConnectionHandler handler = connectionHandlerMap.get(blockState);
        return handler != null ? handler.connect(user, position, blockState) : blockState;
    }

    public static ConnectionHandler getConnectionHandler(int blockstate) {
        return connectionHandlerMap.get(blockstate);
    }

    public static int getId(String key) {
        return keyToId.getOrDefault(key, -1);
    }

    public static String getKey(int id) {
        return idToKey.get(id);
    }

    @FunctionalInterface
    interface ConnectorInitAction {

        void check(WrappedBlockData blockData);
    }
}
