package viaversion.viaversion.protocols.protocol1_16to1_15_2;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.rewriters.ComponentRewriter;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.api.rewriters.RegistryType;
import viaversion.viaversion.api.rewriters.SoundRewriter;
import viaversion.viaversion.api.rewriters.StatisticsRewriter;
import viaversion.viaversion.api.rewriters.TagRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.packets.State;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.data.MappingData;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.data.TranslationMappings;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.metadata.MetadataRewriter1_16To1_15_2;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.WorldPackets;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.EntityTracker1_16;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import viaversion.viaversion.util.GsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protocol1_16To1_15_2 extends Protocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16> {

    private static final UUID ZERO_UUID = new UUID(0, 0);
    public static final MappingData MAPPINGS = new MappingData();
    private TagRewriter tagRewriter;

    public Protocol1_16To1_15_2() {
        super(ClientboundPackets1_15.class, ClientboundPackets1_16.class, ServerboundPackets1_14.class, ServerboundPackets1_16.class);
    }

    @Override
    protected void registerPackets() {
        MetadataRewriter metadataRewriter = new MetadataRewriter1_16To1_15_2(this);

        EntityPackets.register(this);
        WorldPackets.register(this);
        InventoryPackets.register(this);

        tagRewriter = new TagRewriter(this, metadataRewriter::getNewEntityId);
        tagRewriter.register(ClientboundPackets1_15.TAGS);

        new StatisticsRewriter(this, metadataRewriter::getNewEntityId).register(ClientboundPackets1_15.STATISTICS);

        // Login Success
        registerOutgoing(State.LOGIN, 0x02, 0x02, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    // Transform string to int array
                    UUID uuid = UUID.fromString(wrapper.read(Type.STRING));
                    wrapper.write(Type.UUID_INT_ARRAY, uuid);
                });
            }
        });

        // Motd Status - line breaks are no longer allowed for player samples
        registerOutgoing(State.STATUS, 0x00, 0x00, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    String original = wrapper.passthrough(Type.STRING);
                    JsonObject object = GsonUtil.getGson().fromJson(original, JsonObject.class);
                    JsonObject players = object.getAsJsonObject("players");
                    if (players == null) return;

                    JsonArray sample = players.getAsJsonArray("sample");
                    if (sample == null) return;

                    JsonArray splitSamples = new JsonArray();
                    for (JsonElement element : sample) {
                        JsonObject playerInfo = element.getAsJsonObject();
                        String name = playerInfo.getAsJsonPrimitive("name").getAsString();
                        if (name.indexOf('\n') == -1) {
                            splitSamples.add(playerInfo);
                            continue;
                        }

                        String id = playerInfo.getAsJsonPrimitive("id").getAsString();
                        for (String s : name.split("\n")) {
                            JsonObject newSample = new JsonObject();
                            newSample.addProperty("name", s);
                            newSample.addProperty("id", id);
                            splitSamples.add(newSample);
                        }
                    }

                    // Replace data if changed
                    if (splitSamples.size() != sample.size()) {
                        players.add("sample", splitSamples);
                        wrapper.set(Type.STRING, 0, object.toString());
                    }
                });
            }
        });

        ComponentRewriter componentRewriter = new TranslationMappings(this);
        // Handle (relevant) component cases for translatable and score changes
        registerOutgoing(ClientboundPackets1_15.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.COMPONENT);
                map(Type.BYTE);
                handler(wrapper -> {
                    componentRewriter.processText(wrapper.get(Type.COMPONENT, 0));
                    wrapper.write(Type.UUID, ZERO_UUID); // Sender uuid - always send as 'system'
                });
            }
        });
        componentRewriter.registerBossBar(ClientboundPackets1_15.BOSSBAR);
        componentRewriter.registerTitle(ClientboundPackets1_15.TITLE);
        componentRewriter.registerCombatEvent(ClientboundPackets1_15.COMBAT_EVENT);

        SoundRewriter soundRewriter = new SoundRewriter(this);
        soundRewriter.registerSound(ClientboundPackets1_15.SOUND);
        soundRewriter.registerSound(ClientboundPackets1_15.ENTITY_SOUND);

        registerIncoming(ServerboundPackets1_16.INTERACT_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT); // Entity Id
                    int action = wrapper.passthrough(Type.VAR_INT);
                    if (action == 0 || action == 2) {
                        if (action == 2) {
                            // Location
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                        }

                        wrapper.passthrough(Type.VAR_INT); // Hand
                    }

                    // New boolean: Whether the client is sneaking/pressing shift
                    wrapper.read(Type.BOOLEAN);
                });
            }
        });

        if (Via.getConfig().isIgnoreLong1_16ChannelNames()) {
            registerIncoming(ServerboundPackets1_16.PLUGIN_MESSAGE, new PacketRemapper() {
                @Override
                public void registerMap() {
                    handler(wrapper -> {
                        String channel = wrapper.passthrough(Type.STRING);
                        if (channel.length() > 32) {
                            if (!Via.getConfig().isSuppressConversionWarnings()) {
                                Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel, as it is longer than 32 characters: " + channel);
                            }
                            wrapper.cancel();
                        } else if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                            String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\0");
                            List<String> checkedChannels = new ArrayList<>(channels.length);
                            for (String registeredChannel : channels) {
                                if (registeredChannel.length() > 32) {
                                    if (!Via.getConfig().isSuppressConversionWarnings()) {
                                        Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel register of '"
                                                + registeredChannel + "', as it is longer than 32 characters");
                                    }
                                    continue;
                                }

                                checkedChannels.add(registeredChannel);
                            }

                            if (checkedChannels.isEmpty()) {
                                wrapper.cancel();
                                return;
                            }

                            wrapper.write(Type.REMAINING_BYTES, Joiner.on('\0').join(checkedChannels).getBytes(StandardCharsets.UTF_8));
                        }
                    });
                }
            });
        }

        registerIncoming(ServerboundPackets1_16.PLAYER_ABILITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.BYTE);
                    // Flying and walking speed - not important anyways
                    wrapper.write(Type.FLOAT, 0.05F);
                    wrapper.write(Type.FLOAT, 0.1F);
                });
            }
        });

        cancelIncoming(ServerboundPackets1_16.GENERATE_JIGSAW);
        cancelIncoming(ServerboundPackets1_16.UPDATE_JIGSAW_BLOCK);
    }

    @Override
    protected void onMappingDataLoaded() {
        int[] wallPostOverrideTag = new int[47];
        int arrayIndex = 0;
        wallPostOverrideTag[arrayIndex++] = 140;
        wallPostOverrideTag[arrayIndex++] = 179;
        wallPostOverrideTag[arrayIndex++] = 264;
        for (int i = 153; i <= 158; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }
        for (int i = 163; i <= 168; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }
        for (int i = 408; i <= 439; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }

        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wall_post_override", wallPostOverrideTag);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:beacon_base_blocks", 133, 134, 148, 265);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:climbable", 160, 241, 658);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fire", 142);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:campfires", 679);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fence_gates", 242, 467, 468, 469, 470, 471);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:unstable_bottom_center", 242, 467, 468, 469, 470, 471);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wooden_trapdoors", 193, 194, 195, 196, 197, 198);
        tagRewriter.addTag(RegistryType.ITEM, "minecraft:wooden_trapdoors", 215, 216, 217, 218, 219, 220);
        tagRewriter.addTag(RegistryType.ITEM, "minecraft:beacon_payment_items", 529, 530, 531, 760);
        tagRewriter.addTag(RegistryType.ENTITY, "minecraft:impact_projectiles", 2, 72, 71, 37, 69, 79, 83, 15, 93);

        // The client crashes if we don't send all tags it may use
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:guarded_by_piglins");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_speed_blocks");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_fire_base_blocks");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:non_flammable_wood");
        tagRewriter.addEmptyTag(RegistryType.ITEM, "minecraft:non_flammable_wood");

        // The rest of not accessed tags added in older versions; #1830
        tagRewriter.addEmptyTags(RegistryType.BLOCK, "minecraft:bamboo_plantable_on", "minecraft:beds", "minecraft:bee_growables",
                "minecraft:beehives", "minecraft:coral_plants", "minecraft:crops", "minecraft:dragon_immune", "minecraft:flowers",
                "minecraft:portals", "minecraft:shulker_boxes", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors",
                "minecraft:underwater_bonemeals", "minecraft:wither_immune", "minecraft:wooden_fences", "minecraft:wooden_trapdoors");
        tagRewriter.addEmptyTags(RegistryType.ENTITY, "minecraft:arrows", "minecraft:beehive_inhabitors", "minecraft:raiders", "minecraft:skeletons");
        tagRewriter.addEmptyTags(RegistryType.ITEM, "minecraft:beds", "minecraft:coals", "minecraft:fences", "minecraft:flowers",
                "minecraft:lectern_books", "minecraft:music_discs", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:walls", "minecraft:wooden_fences");
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new EntityTracker1_16(userConnection));
        userConnection.put(new InventoryTracker1_16(userConnection));
    }

    @Override
    public MappingData getMappingData() {
        return MAPPINGS;
    }
}
