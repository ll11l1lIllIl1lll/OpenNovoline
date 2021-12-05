package viaversion.viabackwards.protocol.protocol1_13_2to1_14;

import viaversion.viabackwards.api.BackwardsProtocol;
import viaversion.viabackwards.api.data.BackwardsMappings;
import viaversion.viabackwards.api.entities.storage.EntityTracker;
import viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.BlockItemPackets1_14;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.EntityPackets1_14;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.PlayerPackets1_14;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.SoundPackets1_14;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.rewriters.StatisticsRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Protocol1_13_2To1_14 extends BackwardsProtocol<ClientboundPackets1_14, ClientboundPackets1_13, ServerboundPackets1_14, ServerboundPackets1_13> {

    public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.14", "1.13.2", Protocol1_14To1_13_2.class, true);
    private BlockItemPackets1_14 blockItemPackets;
    private EntityPackets1_14 entityPackets;

    public Protocol1_13_2To1_14() {
        super(ClientboundPackets1_14.class, ClientboundPackets1_13.class, ServerboundPackets1_14.class, ServerboundPackets1_13.class);
    }

    @Override
    protected void registerPackets() {
        executeAsyncAfterLoaded(Protocol1_14To1_13_2.class, MAPPINGS::load);

        TranslatableRewriter translatableRewriter = new TranslatableRewriter(this, "1.14");
        translatableRewriter.registerBossBar(ClientboundPackets1_14.BOSSBAR);
        translatableRewriter.registerChatMessage(ClientboundPackets1_14.CHAT_MESSAGE);
        translatableRewriter.registerCombatEvent(ClientboundPackets1_14.COMBAT_EVENT);
        translatableRewriter.registerDisconnect(ClientboundPackets1_14.DISCONNECT);
        translatableRewriter.registerTabList(ClientboundPackets1_14.TAB_LIST);
        translatableRewriter.registerTitle(ClientboundPackets1_14.TITLE);
        translatableRewriter.registerPing();

        blockItemPackets = new BlockItemPackets1_14(this, translatableRewriter);
        blockItemPackets.register();
        entityPackets = new EntityPackets1_14(this);
        entityPackets.register();
        new PlayerPackets1_14(this).register();
        new SoundPackets1_14(this).register();

        new StatisticsRewriter(this, entityPackets::getOldEntityId).register(ClientboundPackets1_14.STATISTICS);

        cancelOutgoing(ClientboundPackets1_14.UPDATE_VIEW_POSITION);
        cancelOutgoing(ClientboundPackets1_14.UPDATE_VIEW_DISTANCE);
        cancelOutgoing(ClientboundPackets1_14.ACKNOWLEDGE_PLAYER_DIGGING);

        registerOutgoing(ClientboundPackets1_14.TAGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int blockTagsSize = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < blockTagsSize; i++) {
                            wrapper.passthrough(Type.STRING);
                            int[] blockIds = wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                            for (int j = 0; j < blockIds.length; j++) {
                                int id = blockIds[j];
                                // Ignore new blocktags
                                int blockId = getMappingData().getNewBlockId(id);
                                blockIds[j] = blockId;
                            }
                        }

                        int itemTagsSize = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < itemTagsSize; i++) {
                            wrapper.passthrough(Type.STRING);
                            int[] itemIds = wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                            for (int j = 0; j < itemIds.length; j++) {
                                int itemId = itemIds[j];
                                // Ignore new itemtags
                                int oldId = getMappingData().getItemMappings().get(itemId);
                                itemIds[j] = oldId;
                            }
                        }

                        int fluidTagsSize = wrapper.passthrough(Type.VAR_INT); // fluid tags
                        for (int i = 0; i < fluidTagsSize; i++) {
                            wrapper.passthrough(Type.STRING);
                            wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                        }

                        // Eat entity tags
                        int entityTagsSize = wrapper.read(Type.VAR_INT);
                        for (int i = 0; i < entityTagsSize; i++) {
                            wrapper.read(Type.STRING);
                            wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_14.UPDATE_LIGHT, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int x = wrapper.read(Type.VAR_INT);
                        int z = wrapper.read(Type.VAR_INT);
                        int skyLightMask = wrapper.read(Type.VAR_INT);
                        int blockLightMask = wrapper.read(Type.VAR_INT);
                        int emptySkyLightMask = wrapper.read(Type.VAR_INT);
                        int emptyBlockLightMask = wrapper.read(Type.VAR_INT);

                        byte[][] skyLight = new byte[16][];
                        // we don't need void and +256 light
                        if (isSet(skyLightMask, 0)) {
                            wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                        }
                        for (int i = 0; i < 16; i++) {
                            if (isSet(skyLightMask, i + 1)) {
                                skyLight[i] = wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                            } else if (isSet(emptySkyLightMask, i + 1)) {
                                skyLight[i] = ChunkLightStorage.EMPTY_LIGHT;
                            }
                        }
                        if (isSet(skyLightMask, 17)) {
                            wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                        }

                        byte[][] blockLight = new byte[16][];
                        if (isSet(blockLightMask, 0)) {
                            wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                        }
                        for (int i = 0; i < 16; i++) {
                            if (isSet(blockLightMask, i + 1)) {
                                blockLight[i] = wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                            } else if (isSet(emptyBlockLightMask, i + 1)) {
                                blockLight[i] = ChunkLightStorage.EMPTY_LIGHT;
                            }
                        }
                        if (isSet(blockLightMask, 17)) {
                            wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                        }

                        wrapper.user().get(ChunkLightStorage.class).setStoredLight(skyLight, blockLight, x, z);
                        wrapper.cancel();
                    }

                    private boolean isSet(int mask, int i) {
                        return (mask & (1 << i)) != 0;
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection user) {
        // Register ClientWorld
        if (!user.has(ClientWorld.class)) {
            user.put(new ClientWorld(user));
        }

        // Register EntityTracker if it doesn't exist yet.
        if (!user.has(EntityTracker.class)) {
            user.put(new EntityTracker(user));
        }

        // Init protocol in EntityTracker
        user.get(EntityTracker.class).initProtocol(this);

        if (!user.has(ChunkLightStorage.class)) {
            user.put(new ChunkLightStorage(user));
        }
    }

    public BlockItemPackets1_14 getBlockItemPackets() {
        return blockItemPackets;
    }

    public EntityPackets1_14 getEntityPackets() {
        return entityPackets;
    }

    @Override
    public BackwardsMappings getMappingData() {
        return MAPPINGS;
    }
}
