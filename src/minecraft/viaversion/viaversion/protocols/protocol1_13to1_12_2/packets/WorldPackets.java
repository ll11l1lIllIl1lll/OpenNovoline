package viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockChangeRecord;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.minecraft.chunks.Chunk;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.Particle;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

import java.util.List;
import java.util.Optional;

public class WorldPackets {
    private static final IntSet VALID_BIOMES = new IntOpenHashSet(70, 1F);

    static {
        // Client will crash if it receives a invalid biome id
        for (int i = 0; i < 50; i++) {
            VALID_BIOMES.add(i);
        }
        VALID_BIOMES.add(127);
        for (int i = 129; i <= 134; i++) {
            VALID_BIOMES.add(i);
        }
        VALID_BIOMES.add(140);
        VALID_BIOMES.add(149);
        VALID_BIOMES.add(151);
        for (int i = 155; i <= 158; i++) {
            VALID_BIOMES.add(i);
        }
        for (int i = 160; i <= 167; i++) {
            VALID_BIOMES.add(i);
        }
    }

    public static void register(Protocol protocol) {
        // Outgoing packets
        protocol.registerOutgoing(ClientboundPackets1_12_1.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Entity UUID

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        PaintingProvider provider = Via.getManager().getProviders().get(PaintingProvider.class);
                        String motive = wrapper.read(Type.STRING);

                        Optional<Integer> id = provider.getIntByIdentifier(motive);

                        if (!id.isPresent() && (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())) {
                            Via.getPlatform().getLogger().warning("Could not find painting motive: " + motive + " falling back to default (0)");
                        }
                        wrapper.write(Type.VAR_INT, id.orElse(0));
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION); // 0 - Location
                map(Type.UNSIGNED_BYTE); // 1 - Action
                map(Type.NBT); // 2 - NBT data

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position position = wrapper.get(Type.POSITION, 0);
                        short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                        CompoundTag tag = wrapper.get(Type.NBT, 0);

                        BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
                        int newId = provider.transform(wrapper.user(), position, tag, true);

                        if (newId != -1) {
                            BlockStorage storage = wrapper.user().get(BlockStorage.class);
                            BlockStorage.ReplacementData replacementData = storage.get(position);
                            if (replacementData != null) {
                                replacementData.setReplacement(newId);
                            }
                        }

                        if (action == 5) // Set type of flower in flower pot
                            wrapper.cancel(); // Removed
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.BLOCK_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION); // Location
                map(Type.UNSIGNED_BYTE); // Action Id
                map(Type.UNSIGNED_BYTE); // Action param
                map(Type.VAR_INT); // Block Id - /!\ NOT BLOCK STATE ID
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position pos = wrapper.get(Type.POSITION, 0);
                        short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                        short param = wrapper.get(Type.UNSIGNED_BYTE, 1);
                        int blockId = wrapper.get(Type.VAR_INT, 0);

                        if (blockId == 25)
                            blockId = 73;
                        else if (blockId == 33)
                            blockId = 99;
                        else if (blockId == 29)
                            blockId = 92;
                        else if (blockId == 54)
                            blockId = 142;
                        else if (blockId == 146)
                            blockId = 305;
                        else if (blockId == 130)
                            blockId = 249;
                        else if (blockId == 138)
                            blockId = 257;
                        else if (blockId == 52)
                            blockId = 140;
                        else if (blockId == 209)
                            blockId = 472;
                        else if (blockId >= 219 && blockId <= 234)
                            blockId = blockId - 219 + 483;

                        if (blockId == 73) { // Note block
                            PacketWrapper blockChange = wrapper.create(0x0B); // block change
                            blockChange.write(Type.POSITION, new Position(pos)); // Clone because position is mutable
                            blockChange.write(Type.VAR_INT, 249 + (action * 24 * 2) + (param * 2));
                            blockChange.send(Protocol1_13To1_12_2.class, true, true);
                        }
                        wrapper.set(Type.VAR_INT, 0, blockId);
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION);
                map(Type.VAR_INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position position = wrapper.get(Type.POSITION, 0);
                        int newId = toNewId(wrapper.get(Type.VAR_INT, 0));

                        UserConnection userConnection = wrapper.user();
                        if (Via.getConfig().isServersideBlockConnections()) {

                            ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), newId);
                            newId = ConnectionData.connect(userConnection, position, newId);
                        }
                        wrapper.set(Type.VAR_INT, 0, checkStorage(wrapper.user(), position, newId));
                        if (Via.getConfig().isServersideBlockConnections()) {
                            // Workaround for packet order issue
                            wrapper.send(Protocol1_13To1_12_2.class, true, true);
                            wrapper.cancel();
                            ConnectionData.update(userConnection, position);
                        }

                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Chunk X
                map(Type.INT); // 1 - Chunk Z
                map(Type.BLOCK_CHANGE_RECORD_ARRAY); // 2 - Records
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int chunkX = wrapper.get(Type.INT, 0);
                        int chunkZ = wrapper.get(Type.INT, 1);
                        UserConnection userConnection = wrapper.user();
                        BlockChangeRecord[] records = wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
                        // Convert ids
                        for (BlockChangeRecord record : records) {
                            int newBlock = toNewId(record.getBlockId());
                            Position position = new Position(
                                    record.getSectionX() + (chunkX * 16),
                                    record.getY(),
                                    record.getSectionZ() + (chunkZ * 16));

                            if (Via.getConfig().isServersideBlockConnections()) {
                                ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), newBlock);
                            }
                            record.setBlockId(checkStorage(wrapper.user(), position, newBlock));
                        }

                        if (Via.getConfig().isServersideBlockConnections()) {
                            for (BlockChangeRecord record : records) {
                                int blockState = record.getBlockId();

                                Position position = new Position(
                                        record.getSectionX() + (chunkX * 16),
                                        record.getY(),
                                        record.getSectionZ() + (chunkZ * 16));

                                ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
                                if (handler != null) {
                                    blockState = handler.connect(userConnection, position, blockState);
                                    record.setBlockId(blockState);
                                }
                            }
                            // Workaround for packet order issue
                            wrapper.send(Protocol1_13To1_12_2.class, true, true);
                            wrapper.cancel();

                            for (BlockChangeRecord record : records) {
                                Position position = new Position(
                                        record.getSectionX() + (chunkX * 16),
                                        record.getY(),
                                        record.getSectionZ() + (chunkZ * 16));
                                ConnectionData.update(userConnection, position);
                            }
                        }

                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.EXPLOSION, new PacketRemapper() {
            @Override
            public void registerMap() {
                if (!Via.getConfig().isServersideBlockConnections())
                    return;

                map(Type.FLOAT); // X
                map(Type.FLOAT); // Y
                map(Type.FLOAT); // Z
                map(Type.FLOAT); // Radius
                map(Type.INT); // Record Count

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        UserConnection userConnection = wrapper.user();
                        int x = (int) Math.floor(wrapper.get(Type.FLOAT, 0));
                        int y = (int) Math.floor(wrapper.get(Type.FLOAT, 1));
                        int z = (int) Math.floor(wrapper.get(Type.FLOAT, 2));
                        int recordCount = wrapper.get(Type.INT, 0);
                        Position[] records = new Position[recordCount];

                        for (int i = 0; i < recordCount; i++) {
                            Position position = new Position(
                                    x + wrapper.passthrough(Type.BYTE),
                                    (short) (y + wrapper.passthrough(Type.BYTE)),
                                    z + wrapper.passthrough(Type.BYTE));
                            records[i] = position;

                            // Set to air
                            ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), 0);
                        }

                        // Workaround for packet order issue
                        wrapper.send(Protocol1_13To1_12_2.class, true, true);
                        wrapper.cancel();

                        for (int i = 0; i < recordCount; i++) {
                            ConnectionData.update(userConnection, records[i]);
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.UNLOAD_CHUNK, new PacketRemapper() {
            @Override
            public void registerMap() {
                if (Via.getConfig().isServersideBlockConnections()) {
                    handler(new PacketHandler() {
                        @Override
                        public void handle(PacketWrapper wrapper) throws Exception {
                            int x = wrapper.passthrough(Type.INT);
                            int z = wrapper.passthrough(Type.INT);
                            ConnectionData.blockConnectionProvider.unloadChunk(wrapper.user(), x, z);
                        }
                    });
                }
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.NAMED_SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String newSoundId = NamedSoundRewriter.getNewId(wrapper.get(Type.STRING, 0));
                        wrapper.set(Type.STRING, 0, newSoundId);
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        BlockStorage storage = wrapper.user().get(BlockStorage.class);

                        Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                        Chunk1_13Type type1_13 = new Chunk1_13Type(clientWorld);
                        Chunk chunk = wrapper.read(type);
                        wrapper.write(type1_13, chunk);

                        for (int i = 0; i < chunk.getSections().length; i++) {
                            ChunkSection section = chunk.getSections()[i];
                            if (section == null)
                                continue;

                            for (int p = 0; p < section.getPaletteSize(); p++) {
                                int old = section.getPaletteEntry(p);
                                int newId = toNewId(old);
                                section.setPaletteEntry(p, newId);
                            }

                            boolean willSaveToStorage = false;
                            for (int p = 0; p < section.getPaletteSize(); p++) {
                                int newId = section.getPaletteEntry(p);
                                if (storage.isWelcome(newId)) {
                                    willSaveToStorage = true;
                                    break;
                                }
                            }

                            boolean willSaveConnection = false;
                            if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
                                for (int p = 0; p < section.getPaletteSize(); p++) {
                                    int newId = section.getPaletteEntry(p);
                                    if (ConnectionData.isWelcome(newId)) {
                                        willSaveConnection = true;
                                        break;
                                    }
                                }
                            }

                            if (willSaveToStorage) {
                                for (int y = 0; y < 16; y++) {
                                    for (int z = 0; z < 16; z++) {
                                        for (int x = 0; x < 16; x++) {
                                            int block = section.getFlatBlock(x, y, z);
                                            if (storage.isWelcome(block)) {
                                                storage.store(new Position(
                                                        (x + (chunk.getX() << 4)),
                                                        (short) (y + (i << 4)),
                                                        (z + (chunk.getZ() << 4))
                                                ), block);
                                            }
                                        }
                                    }
                                }
                            }

                            if (willSaveConnection) {
                                for (int y = 0; y < 16; y++) {
                                    for (int z = 0; z < 16; z++) {
                                        for (int x = 0; x < 16; x++) {
                                            int block = section.getFlatBlock(x, y, z);
                                            if (ConnectionData.isWelcome(block)) {
                                                ConnectionData.blockConnectionProvider.storeBlock(wrapper.user(), x + (chunk.getX() << 4),
                                                        y + (i << 4),
                                                        z + (chunk.getZ() << 4),
                                                        block);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Rewrite biome id 255 to plains
                        if (chunk.isBiomeData()) {
                            int latestBiomeWarn = Integer.MIN_VALUE;
                            for (int i = 0; i < 256; i++) {
                                int biome = chunk.getBiomeData()[i];
                                if (!VALID_BIOMES.contains(biome)) {
                                    if (biome != 255 // is it generated naturally? *shrug*
                                            && latestBiomeWarn != biome) {
                                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                            Via.getPlatform().getLogger().warning("Received invalid biome id " + biome);
                                        }
                                        latestBiomeWarn = biome;
                                    }
                                    chunk.getBiomeData()[i] = 1; // Plains
                                }
                            }
                        }

                        // Rewrite BlockEntities to normal blocks
                        BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
                        for (CompoundTag tag : chunk.getBlockEntities()) {
                            int newId = provider.transform(wrapper.user(), null, tag, false);
                            if (newId != -1) {
                                int x = (int) tag.get("x").getValue();
                                int y = (int) tag.get("y").getValue();
                                int z = (int) tag.get("z").getValue();

                                Position position = new Position(x, (short) y, z);
                                // Store the replacement blocks for blockupdates
                                BlockStorage.ReplacementData replacementData = storage.get(position);
                                if (replacementData != null) {
                                    replacementData.setReplacement(newId);
                                }

                                chunk.getSections()[y >> 4].setFlatBlock(x & 0xF, y & 0xF, z & 0xF, newId);
                            }
                        }

                        if (Via.getConfig().isServersideBlockConnections()) {
                            ConnectionData.connectBlocks(wrapper.user(), chunk);
                            // Workaround for packet order issue
                            wrapper.send(Protocol1_13To1_12_2.class, true, true);
                            wrapper.cancel();
                            for (int i = 0; i < chunk.getSections().length; i++) {
                                ChunkSection section = chunk.getSections()[i];
                                if (section == null) continue;
                                ConnectionData.updateChunkSectionNeighbours(wrapper.user(), chunk.getX(), chunk.getZ(), i);
                            }
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_12_1.SPAWN_PARTICLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Particle ID
                map(Type.BOOLEAN); // 1 - Long Distance
                map(Type.FLOAT); // 2 - X
                map(Type.FLOAT); // 3 - Y
                map(Type.FLOAT); // 4 - Z
                map(Type.FLOAT); // 5 - Offset X
                map(Type.FLOAT); // 6 - Offset Y
                map(Type.FLOAT); // 7 - Offset Z
                map(Type.FLOAT); // 8 - Particle Data
                map(Type.INT); // 9 - Particle Count

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int particleId = wrapper.get(Type.INT, 0);

                        // Get the data (Arrays are overrated)
                        int dataCount = 0;
                        // Particles with 1 data [BlockCrack,BlockDust,FallingDust]
                        if (particleId == 37 || particleId == 38 || particleId == 46)
                            dataCount = 1;
                            // Particles with 2 data [IconCrack]
                        else if (particleId == 36)
                            dataCount = 2;

                        Integer[] data = new Integer[dataCount];
                        for (int i = 0; i < data.length; i++)
                            data[i] = wrapper.read(Type.VAR_INT);

                        Particle particle = ParticleRewriter.rewriteParticle(particleId, data);

                        // Cancel if null or completely removed
                        if (particle == null || particle.getId() == -1) {
                            wrapper.cancel();
                            return;
                        }

                        //Handle reddust particle color
                        if (particle.getId() == 11) {
                            int count = wrapper.get(Type.INT, 1);
                            float speed = wrapper.get(Type.FLOAT, 6);
                            // Only handle for count = 0
                            if (count == 0) {
                                wrapper.set(Type.INT, 1, 1);
                                wrapper.set(Type.FLOAT, 6, 0f);

                                List<Particle.ParticleData> arguments = particle.getArguments();
                                for (int i = 0; i < 3; i++) {
                                    //RGB values are represented by the X/Y/Z offset
                                    float colorValue = wrapper.get(Type.FLOAT, i + 3) * speed;
                                    if (colorValue == 0 && i == 0) {
                                        // https://minecraft.gamepedia.com/User:Alphappy/reddust
                                        colorValue = 1;
                                    }
                                    arguments.get(i).setValue(colorValue);
                                    wrapper.set(Type.FLOAT, i + 3, 0f);
                                }
                            }
                        }

                        wrapper.set(Type.INT, 0, particle.getId());
                        for (Particle.ParticleData particleData : particle.getArguments())
                            wrapper.write(particleData.getType(), particleData.getValue());

                    }
                });
            }
        });
    }

    public static int toNewId(int oldId) {
        if (oldId < 0) {
            oldId = 0; // Some plugins use negative numbers to clear blocks, remap them to air.
        }
        int newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId);
        if (newId != -1) {
            return newId;
        }
        newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId & ~0xF); // Remove data
        if (newId != -1) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Missing block " + oldId);
            }
            return newId;
        }
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
            Via.getPlatform().getLogger().warning("Missing block completely " + oldId);
        }
        // Default stone
        return 1;
    }

    private static int checkStorage(UserConnection user, Position position, int newId) {
        BlockStorage storage = user.get(BlockStorage.class);
        if (storage.contains(position)) {
            BlockStorage.ReplacementData data = storage.get(position);

            if (data.getOriginal() == newId) {
                if (data.getReplacement() != -1) {
                    return data.getReplacement();
                }
            } else {
                storage.remove(position);
                // Check if the new id has to be stored
                if (storage.isWelcome(newId))
                    storage.store(position, newId);
            }
        } else if (storage.isWelcome(newId))
            storage.store(position, newId);
        return newId;
    }
}
