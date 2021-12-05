package viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.google.gson.JsonElement;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.minecraft.chunks.Chunk;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.chunks.FakeTileEntity;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;

import java.util.List;

public class Protocol1_9_3To1_9_1_2 extends Protocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3> {

    public static final ValueTransformer<Short, Short> ADJUST_PITCH = new ValueTransformer<Short, Short>(Type.UNSIGNED_BYTE, Type.UNSIGNED_BYTE) {
        @Override
        public Short transform(PacketWrapper wrapper, Short inputValue) throws Exception {
            return (short) Math.round(inputValue / 63.5F * 63.0F);
        }
    };

    public Protocol1_9_3To1_9_1_2() {
        super(ClientboundPackets1_9.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9.class, ServerboundPackets1_9_3.class);
    }

    @Override
    protected void registerPackets() {
        // Sign update packet
        registerOutgoing(ClientboundPackets1_9.UPDATE_SIGN, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        //read data
                        Position position = wrapper.read(Type.POSITION);
                        JsonElement[] lines = new JsonElement[4];
                        for (int i = 0; i < 4; i++) {
                            lines[i] = wrapper.read(Type.COMPONENT);
                        }

                        wrapper.clearInputBuffer();

                        //write data
                        wrapper.setId(0x09); //Update block entity
                        wrapper.write(Type.POSITION, position); //Block location
                        wrapper.write(Type.UNSIGNED_BYTE, (short) 9); //Action type (9 update sign)

                        //Create nbt
                        CompoundTag tag = new CompoundTag("");
                        tag.put(new StringTag("id", "Sign"));
                        tag.put(new IntTag("x", position.getX()));
                        tag.put(new IntTag("y", position.getY()));
                        tag.put(new IntTag("z", position.getZ()));
                        for (int i = 0; i < lines.length; i++) {
                            tag.put(new StringTag("Text" + (i + 1), lines[i].toString()));
                        }

                        wrapper.write(Type.NBT, tag);
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_9.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);

                        Chunk1_9_1_2Type type = new Chunk1_9_1_2Type(clientWorld);
                        Chunk chunk = wrapper.passthrough(type);

                        List<CompoundTag> tags = chunk.getBlockEntities();
                        for (int i = 0; i < chunk.getSections().length; i++) {
                            ChunkSection section = chunk.getSections()[i];
                            if (section == null)
                                continue;

                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    for (int x = 0; x < 16; x++) {
                                        int block = section.getBlockId(x, y, z);
                                        if (FakeTileEntity.hasBlock(block)) {
                                            tags.add(FakeTileEntity.getFromBlock(x + (chunk.getX() << 4), y + (i << 4), z + (chunk.getZ() << 4), block));
                                        }
                                    }
                                }
                            }
                        }

                        // Send (for clients on this version)
                        wrapper.write(Type.NBT_ARRAY, chunk.getBlockEntities().toArray(new CompoundTag[0]));
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_9.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Entity ID
                map(Type.UNSIGNED_BYTE); // 1 - Gamemode
                map(Type.INT); // 2 - Dimension

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 1);
                        clientChunks.setEnvironment(dimensionId);
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_9.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Dimension ID
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);
                    }
                });
            }
        });

        // Sound effect
        registerOutgoing(ClientboundPackets1_9.SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Sound name
                map(Type.VAR_INT); // 1 - Sound Category
                map(Type.INT); // 2 - x
                map(Type.INT); // 3 - y
                map(Type.INT); // 4 - z
                map(Type.FLOAT); // 5 - Volume
                map(ADJUST_PITCH); // 6 - Pitch
            }
        });
    }

    @Override
    public void init(UserConnection user) {
        if (!user.has(ClientWorld.class)) {
            user.put(new ClientWorld(user));
        }
    }
}
