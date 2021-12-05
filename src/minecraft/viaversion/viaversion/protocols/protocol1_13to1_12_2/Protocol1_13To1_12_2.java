package viaversion.viaversion.protocols.protocol1_13to1_12_2;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.md_5.bungee.api.ChatColor;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_13Types;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.platform.providers.ViaProviders;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.remapper.ValueCreator;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.api.rewriters.SoundRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.packets.State;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.PacketBlockConnectionProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticMappings;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.metadata.MetadataRewriter1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.EntityPackets;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockConnectionStorage;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import viaversion.viaversion.util.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Protocol1_13To1_12_2 extends Protocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13> {

    public static final MappingData MAPPINGS = new MappingData();

    public Protocol1_13To1_12_2() {
        super(ClientboundPackets1_12_1.class, ClientboundPackets1_13.class, ServerboundPackets1_12_1.class, ServerboundPackets1_13.class);
    }

    public static final PacketHandler POS_TO_3_INT = wrapper -> {
        Position position = wrapper.read(Type.POSITION);
        wrapper.write(Type.INT, position.getX());
        wrapper.write(Type.INT, (int) position.getY());
        wrapper.write(Type.INT, position.getZ());
    };

    private static final PacketHandler SEND_DECLARE_COMMANDS_AND_TAGS =
            w -> {
                // Send fake declare commands
                w.create(0x11, new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) {
                        wrapper.write(Type.VAR_INT, 2); // Size
                        // Write root node
                        wrapper.write(Type.VAR_INT, 0); // Mark as command
                        wrapper.write(Type.VAR_INT, 1); // 1 child
                        wrapper.write(Type.VAR_INT, 1); // Child is at 1

                        // Write arg node
                        wrapper.write(Type.VAR_INT, 0x02 | 0x04 | 0x10); // Mark as command
                        wrapper.write(Type.VAR_INT, 0); // No children
                        // Extra data
                        wrapper.write(Type.STRING, "args"); // Arg name
                        wrapper.write(Type.STRING, "brigadier:string");
                        wrapper.write(Type.VAR_INT, 2); // Greedy
                        wrapper.write(Type.STRING, "minecraft:ask_server"); // Ask server

                        wrapper.write(Type.VAR_INT, 0); // Root node index
                    }
                }).send(Protocol1_13To1_12_2.class);

                // Send tags packet
                w.create(0x55, new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.VAR_INT, MAPPINGS.getBlockTags().size()); // block tags
                        for (Map.Entry<String, Integer[]> tag : MAPPINGS.getBlockTags().entrySet()) {
                            wrapper.write(Type.STRING, tag.getKey());
                            // Needs copy as other protocols may modify it
                            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, toPrimitive(tag.getValue()));
                        }
                        wrapper.write(Type.VAR_INT, MAPPINGS.getItemTags().size()); // item tags
                        for (Map.Entry<String, Integer[]> tag : MAPPINGS.getItemTags().entrySet()) {
                            wrapper.write(Type.STRING, tag.getKey());
                            // Needs copy as other protocols may modify it
                            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, toPrimitive(tag.getValue()));
                        }
                        wrapper.write(Type.VAR_INT, MAPPINGS.getFluidTags().size()); // fluid tags
                        for (Map.Entry<String, Integer[]> tag : MAPPINGS.getFluidTags().entrySet()) {
                            wrapper.write(Type.STRING, tag.getKey());
                            // Needs copy as other protocols may modify it
                            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, toPrimitive(tag.getValue()));
                        }
                    }
                }).send(Protocol1_13To1_12_2.class);
            };

    // These are arbitrary rewrite values, it just needs an invalid color code character.
    protected static final Map<ChatColor, Character> SCOREBOARD_TEAM_NAME_REWRITE = new HashMap<>();
    private static final Set<ChatColor> FORMATTING_CODES = Sets.newHashSet(ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH,
            ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);

    static {
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.BLACK, 'g');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_BLUE, 'h');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_GREEN, 'i');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_AQUA, 'j');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_RED, 'p');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_PURPLE, 'q');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.GOLD, 's');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.GRAY, 't');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.DARK_GRAY, 'u');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.BLUE, 'v');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.GREEN, 'w');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.AQUA, 'x');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.RED, 'y');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.LIGHT_PURPLE, 'z');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.YELLOW, '!');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.WHITE, '?');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.MAGIC, '#');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.BOLD, '(');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.STRIKETHROUGH, ')');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.UNDERLINE, ':');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.ITALIC, ';');
        SCOREBOARD_TEAM_NAME_REWRITE.put(ChatColor.RESET, '/');
    }

    @Override
    protected void registerPackets() {
        MetadataRewriter metadataRewriter = new MetadataRewriter1_13To1_12_2(this);

        // Register grouped packet changes
        EntityPackets.register(this);
        WorldPackets.register(this);
        InventoryPackets.register(this);

        registerOutgoing(State.LOGIN, 0x0, 0x0, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT)));
            }
        });

        registerOutgoing(State.STATUS, 0x00, 0x00, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String response = wrapper.get(Type.STRING, 0);
                        try {
                            JsonObject json = GsonUtil.getGson().fromJson(response, JsonObject.class);
                            if (json.has("favicon")) {
                                json.addProperty("favicon", json.get("favicon").getAsString().replace("\n", ""));
                            }
                            wrapper.set(Type.STRING, 0, GsonUtil.getGson().toJson(json));
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // New packet 0x04 - Login Plugin Message

        // Statistics
        registerOutgoing(ClientboundPackets1_12_1.STATISTICS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int size = wrapper.read(Type.VAR_INT);
                        List<StatisticData> remappedStats = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            String name = wrapper.read(Type.STRING);
                            String[] split = name.split("\\.");
                            int categoryId = 0;
                            int newId = -1;
                            int value = wrapper.read(Type.VAR_INT);
                            if (split.length == 2) {
                                // Custom types
                                categoryId = 8;
                                Integer newIdRaw = StatisticMappings.CUSTOM_STATS.get(name);
                                if (newIdRaw != null) {
                                    newId = newIdRaw;
                                } else {
                                    Via.getPlatform().getLogger().warning("Could not find 1.13 -> 1.12.2 statistic mapping for " + name);
                                }
                            } else if (split.length > 2) {
                                String category = split[1];
                                //TODO convert string ids (blocks, items, entities)
                                switch (category) {
                                    case "mineBlock":
                                        categoryId = 0;
                                        break;
                                    case "craftItem":
                                        categoryId = 1;
                                        break;
                                    case "useItem":
                                        categoryId = 2;
                                        break;
                                    case "breakItem":
                                        categoryId = 3;
                                        break;
                                    case "pickup":
                                        categoryId = 4;
                                        break;
                                    case "drop":
                                        categoryId = 5;
                                        break;
                                    case "killEntity":
                                        categoryId = 6;
                                        break;
                                    case "entityKilledBy":
                                        categoryId = 7;
                                        break;
                                }
                            }
                            if (newId != -1)
                                remappedStats.add(new StatisticData(categoryId, newId, value));
                        }

                        wrapper.write(Type.VAR_INT, remappedStats.size()); // size
                        for (StatisticData stat : remappedStats) {
                            wrapper.write(Type.VAR_INT, stat.getCategoryId()); // category id
                            wrapper.write(Type.VAR_INT, stat.getNewId()); // statistics id
                            wrapper.write(Type.VAR_INT, stat.getValue()); // value
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.BOSSBAR, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UUID);
                map(Type.VAR_INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int action = wrapper.get(Type.VAR_INT, 0);
                        if (action == 0 || action == 3) {
                            ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT));
                        }
                    }
                });
            }
        });
        registerOutgoing(ClientboundPackets1_12_1.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT)));
            }
        });
        registerOutgoing(ClientboundPackets1_12_1.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.VAR_INT, wrapper.user().get(TabCompleteTracker.class).getTransactionId());

                        String input = wrapper.user().get(TabCompleteTracker.class).getInput();
                        // Start & End
                        int index;
                        int length;
                        // If no input or new word (then it's the start)
                        if (input.endsWith(" ") || input.isEmpty()) {
                            index = input.length();
                            length = 0;
                        } else {
                            // Otherwise find the last space (+1 as we include it)
                            int lastSpace = input.lastIndexOf(' ') + 1;
                            index = lastSpace;
                            length = input.length() - lastSpace;
                        }
                        // Write index + length
                        wrapper.write(Type.VAR_INT, index);
                        wrapper.write(Type.VAR_INT, length);

                        int count = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < count; i++) {
                            String suggestion = wrapper.read(Type.STRING);
                            // If we're at the start then handle removing slash
                            if (suggestion.startsWith("/") && index == 0) {
                                suggestion = suggestion.substring(1);
                            }
                            wrapper.write(Type.STRING, suggestion);
                            wrapper.write(Type.BOOLEAN, false);
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // Id
                map(Type.STRING); // Window type
                handler(wrapper -> ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT))); // Title
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.COOLDOWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int item = wrapper.read(Type.VAR_INT);
                        int ticks = wrapper.read(Type.VAR_INT);
                        wrapper.cancel();
                        if (item == 383) { // Spawn egg
                            for (int i = 0; i < 44; i++) {
                                Integer newItem = getMappingData().getItemMappings().get(item << 16 | i);
                                if (newItem != null) {
                                    PacketWrapper packet = wrapper.create(0x18);
                                    packet.write(Type.VAR_INT, newItem);
                                    packet.write(Type.VAR_INT, ticks);
                                    packet.send(Protocol1_13To1_12_2.class);
                                } else {
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < 16; i++) {
                                int newItem = getMappingData().getItemMappings().get(item << 4 | i);
                                if (newItem != -1) {
                                    PacketWrapper packet = wrapper.create(0x18);
                                    packet.write(Type.VAR_INT, newItem);
                                    packet.write(Type.VAR_INT, ticks);
                                    packet.send(Protocol1_13To1_12_2.class);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.DISCONNECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT)));
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Effect Id
                map(Type.POSITION); // Location
                map(Type.INT); // Data
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int id = wrapper.get(Type.INT, 0);
                        int data = wrapper.get(Type.INT, 1);
                        if (id == 1010) { // Play record
                            wrapper.set(Type.INT, 1, getMappingData().getItemMappings().get(data << 4));
                        } else if (id == 2001) { // Block break + block break sound
                            int blockId = data & 0xFFF;
                            int blockData = data >> 12;
                            wrapper.set(Type.INT, 1, WorldPackets.toNewId(blockId << 4 | blockData));
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Entity ID
                map(Type.UNSIGNED_BYTE); // 1 - Gamemode
                map(Type.INT); // 2 - Dimension

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        // Store the player
                        int entityId = wrapper.get(Type.INT, 0);
                        wrapper.user().get(EntityTracker1_13.class).addEntity(entityId, Entity1_13Types.EntityType.PLAYER);

                        ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 1);
                        clientChunks.setEnvironment(dimensionId);
                    }
                });
                handler(SEND_DECLARE_COMMANDS_AND_TAGS);
            }
        });


        registerOutgoing(ClientboundPackets1_12_1.CRAFT_RECIPE_RESPONSE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE);
                handler(wrapper -> wrapper.write(Type.STRING, "viaversion:legacy/" + wrapper.read(Type.VAR_INT)));
            }
        });
        registerOutgoing(ClientboundPackets1_12_1.COMBAT_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Event
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.VAR_INT, 0) == 2) { // Entity dead
                            wrapper.passthrough(Type.VAR_INT); // Player id
                            wrapper.passthrough(Type.INT); // Entity id
                            ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT));
                        }
                    }
                });
            }
        });
        registerOutgoing(ClientboundPackets1_12_1.MAP_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Map id
                map(Type.BYTE); // 1 - Scale
                map(Type.BOOLEAN); // 2 - Tracking Position
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int iconCount = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < iconCount; i++) {
                            byte directionAndType = wrapper.read(Type.BYTE);
                            int type = (directionAndType & 0xF0) >> 4;
                            wrapper.write(Type.VAR_INT, type);
                            wrapper.passthrough(Type.BYTE); // Icon X
                            wrapper.passthrough(Type.BYTE); // Icon Z
                            byte direction = (byte) (directionAndType & 0x0F);
                            wrapper.write(Type.BYTE, direction);
                            wrapper.write(Type.OPTIONAL_COMPONENT, null); // Display Name
                        }
                    }
                });
            }
        });
        registerOutgoing(ClientboundPackets1_12_1.UNLOCK_RECIPES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // action
                map(Type.BOOLEAN); // crafting book open
                map(Type.BOOLEAN); // crafting filter active
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.BOOLEAN, false); // smelting book open
                        wrapper.write(Type.BOOLEAN, false); // smelting filter active
                    }
                });
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int action = wrapper.get(Type.VAR_INT, 0);
                        for (int i = 0; i < (action == 0 ? 2 : 1); i++) {
                            int[] ids = wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                            String[] stringIds = new String[ids.length];
                            for (int j = 0; j < ids.length; j++) {
                                stringIds[j] = "viaversion:legacy/" + ids[j];
                            }
                            wrapper.write(Type.STRING_ARRAY, stringIds);
                        }
                        if (action == 0) {
                            wrapper.create(0x54, new ValueCreator() { // Declare recipes
                                @Override
                                public void write(PacketWrapper wrapper) throws Exception {
                                    wrapper.write(Type.VAR_INT, RecipeData.recipes.size());
                                    for (Map.Entry<String, RecipeData.Recipe> entry : RecipeData.recipes.entrySet()) {
                                        wrapper.write(Type.STRING, entry.getKey()); // Id
                                        wrapper.write(Type.STRING, entry.getValue().getType());
                                        switch (entry.getValue().getType()) {
                                            case "crafting_shapeless": {
                                                wrapper.write(Type.STRING, entry.getValue().getGroup());
                                                wrapper.write(Type.VAR_INT, entry.getValue().getIngredients().length);
                                                for (Item[] ingredient : entry.getValue().getIngredients()) {
                                                    Item[] clone = ingredient.clone(); // Clone because array and item is mutable
                                                    for (int i = 0; i < clone.length; i++) {
                                                        if (clone[i] == null) continue;
                                                        clone[i] = new Item(clone[i]);
                                                    }
                                                    wrapper.write(Type.FLAT_ITEM_ARRAY_VAR_INT, clone);
                                                }
                                                wrapper.write(Type.FLAT_ITEM, new Item(entry.getValue().getResult()));
                                                break;
                                            }
                                            case "crafting_shaped": {
                                                wrapper.write(Type.VAR_INT, entry.getValue().getWidth());
                                                wrapper.write(Type.VAR_INT, entry.getValue().getHeight());
                                                wrapper.write(Type.STRING, entry.getValue().getGroup());
                                                for (Item[] ingredient : entry.getValue().getIngredients()) {
                                                    Item[] clone = ingredient.clone(); // Clone because array and item is mutable
                                                    for (int i = 0; i < clone.length; i++) {
                                                        if (clone[i] == null) continue;
                                                        clone[i] = new Item(clone[i]);
                                                    }
                                                    wrapper.write(Type.FLAT_ITEM_ARRAY_VAR_INT, clone);
                                                }
                                                wrapper.write(Type.FLAT_ITEM, new Item(entry.getValue().getResult()));
                                                break;
                                            }
                                            case "smelting": {
                                                wrapper.write(Type.STRING, entry.getValue().getGroup());
                                                Item[] clone = entry.getValue().getIngredient().clone(); // Clone because array and item is mutable
                                                for (int i = 0; i < clone.length; i++) {
                                                    if (clone[i] == null) continue;
                                                    clone[i] = new Item(clone[i]);
                                                }
                                                wrapper.write(Type.FLAT_ITEM_ARRAY_VAR_INT, clone);
                                                wrapper.write(Type.FLAT_ITEM, new Item(entry.getValue().getResult()));
                                                wrapper.write(Type.FLOAT, entry.getValue().getExperience());
                                                wrapper.write(Type.VAR_INT, entry.getValue().getCookingTime());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }).send(Protocol1_13To1_12_2.class, true, true);
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Dimension ID
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);

                        if (Via.getConfig().isServersideBlockConnections()) {
                            ConnectionData.clearBlockStorage(wrapper.user());
                        }
                    }
                });
                handler(SEND_DECLARE_COMMANDS_AND_TAGS);
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // 0 - Objective name
                map(Type.BYTE); // 1 - Mode
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        byte mode = wrapper.get(Type.BYTE, 0);
                        // On create or update
                        if (mode == 0 || mode == 2) {
                            String value = wrapper.read(Type.STRING); // Value
                            wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(value));

                            String type = wrapper.read(Type.STRING);
                            // integer or hearts
                            wrapper.write(Type.VAR_INT, type.equals("integer") ? 0 : 1);
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.TEAMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // 0 - Team Name
                map(Type.BYTE); // 1 - Mode

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        byte action = wrapper.get(Type.BYTE, 0);

                        if (action == 0 || action == 2) {
                            String displayName = wrapper.read(Type.STRING); // Display Name
                            wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(displayName));

                            String prefix = wrapper.read(Type.STRING); // Prefix moved
                            String suffix = wrapper.read(Type.STRING); // Suffix moved

                            wrapper.passthrough(Type.BYTE); // Flags

                            wrapper.passthrough(Type.STRING); // Name Tag Visibility
                            wrapper.passthrough(Type.STRING); // Collision rule

                            // Handle new colors
                            int colour = wrapper.read(Type.BYTE).intValue();
                            if (colour == -1) {
                                colour = 21; // -1 changed to 21
                            }

                            if (Via.getConfig().is1_13TeamColourFix()) {
                                ChatColor lastColor = getLastColor(prefix);
                                colour = lastColor.ordinal();
                                suffix = lastColor.toString() + suffix;
                            }

                            wrapper.write(Type.VAR_INT, colour);

                            wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(prefix)); // Prefix
                            wrapper.write(Type.COMPONENT, ChatRewriter.legacyTextToJson(suffix)); // Suffix
                        }

                        if (action == 0 || action == 3 || action == 4) {
                            String[] names = wrapper.read(Type.STRING_ARRAY); // Entities
                            for (int i = 0; i < names.length; i++) {
                                names[i] = rewriteTeamMemberName(names[i]);
                            }
                            wrapper.write(Type.STRING_ARRAY, names);
                        }
                    }
                });

            }
        });
        registerOutgoing(ClientboundPackets1_12_1.UPDATE_SCORE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String displayName = wrapper.read(Type.STRING); // Display Name
                        displayName = rewriteTeamMemberName(displayName);
                        wrapper.write(Type.STRING, displayName);

                        byte action = wrapper.read(Type.BYTE);
                        wrapper.write(Type.BYTE, action);
                        wrapper.passthrough(Type.STRING); // Objective Name
                        if (action != 1) {
                            wrapper.passthrough(Type.VAR_INT); // Value
                        }
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.TITLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Action
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int action = wrapper.get(Type.VAR_INT, 0);
                        if (action >= 0 && action <= 2) {
                            ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT));
                        }
                    }
                });
            }
        });
        // New 0x4C - Stop Sound

        new SoundRewriter(this).registerSound(ClientboundPackets1_12_1.SOUND);

        registerOutgoing(ClientboundPackets1_12_1.TAB_LIST, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT));
                        ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT));
                    }
                });
            }
        });

        registerOutgoing(ClientboundPackets1_12_1.ADVANCEMENTS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.passthrough(Type.BOOLEAN); // Reset/clear
                        int size = wrapper.passthrough(Type.VAR_INT); // Mapping size

                        for (int i = 0; i < size; i++) {
                            wrapper.passthrough(Type.STRING); // Identifier

                            // Parent
                            if (wrapper.passthrough(Type.BOOLEAN))
                                wrapper.passthrough(Type.STRING);

                            // Display data
                            if (wrapper.passthrough(Type.BOOLEAN)) {
                                ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT)); // Title
                                ChatRewriter.processTranslate(wrapper.passthrough(Type.COMPONENT)); // Description
                                Item icon = wrapper.read(Type.ITEM);
                                InventoryPackets.toClient(icon);
                                wrapper.write(Type.FLAT_ITEM, icon); // Translate item to flat item
                                wrapper.passthrough(Type.VAR_INT); // Frame type
                                int flags = wrapper.passthrough(Type.INT); // Flags
                                if ((flags & 1) != 0) {
                                    wrapper.passthrough(Type.STRING); // Background texture
                                }
                                wrapper.passthrough(Type.FLOAT); // X
                                wrapper.passthrough(Type.FLOAT); // Y
                            }

                            wrapper.passthrough(Type.STRING_ARRAY); // Criteria

                            int arrayLength = wrapper.passthrough(Type.VAR_INT);
                            for (int array = 0; array < arrayLength; array++) {
                                wrapper.passthrough(Type.STRING_ARRAY); // String array
                            }
                        }
                    }
                });
            }
        });


        // Incoming packets
        // New packet 0x02 - Login Plugin Message
        cancelIncoming(State.LOGIN, 0x02);

        // New 0x01 - Query Block NBT
        cancelIncoming(ServerboundPackets1_13.QUERY_BLOCK_NBT);

        // Tab-Complete
        registerIncoming(ServerboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        // Disable auto-complete if configured
                        if (Via.getConfig().isDisable1_13AutoComplete()) {
                            wrapper.cancel();
                        }
                        int tid = wrapper.read(Type.VAR_INT);
                        // Save transaction id
                        wrapper.user().get(TabCompleteTracker.class).setTransactionId(tid);
                    }
                });
                // Prepend /
                map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                    @Override
                    public String transform(PacketWrapper wrapper, String inputValue) {
                        wrapper.user().get(TabCompleteTracker.class).setInput(inputValue);
                        return "/" + inputValue;
                    }
                });
                // Fake the end of the packet
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.BOOLEAN, false);
                        wrapper.write(Type.OPTIONAL_POSITION, null);
                        if (!wrapper.isCancelled() && Via.getConfig().get1_13TabCompleteDelay() > 0) {
                            TabCompleteTracker tracker = wrapper.user().get(TabCompleteTracker.class);
                            wrapper.cancel();
                            tracker.setTimeToSend(System.currentTimeMillis() + Via.getConfig().get1_13TabCompleteDelay() * 50L);
                            tracker.setLastTabComplete(wrapper.get(Type.STRING, 0));
                        }
                    }
                });
            }
        });

        // New 0x0A - Edit book -> Plugin Message
        registerIncoming(ServerboundPackets1_13.EDIT_BOOK, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item item = wrapper.read(Type.FLAT_ITEM);
                        boolean isSigning = wrapper.read(Type.BOOLEAN);

                        InventoryPackets.toServer(item);

                        wrapper.write(Type.STRING, isSigning ? "MC|BSign" : "MC|BEdit"); // Channel
                        wrapper.write(Type.ITEM, item);
                    }
                });
            }
        });

        // New 0x0C - Query Entity NBT
        cancelIncoming(ServerboundPackets1_13.ENTITY_NBT_REQUEST);

        // New 0x15 - Pick Item -> Plugin Message
        registerIncoming(ServerboundPackets1_13.PICK_ITEM, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.STRING, "MC|PickItem"); // Channel
                    }
                });
            }
        });

        registerIncoming(ServerboundPackets1_13.CRAFT_RECIPE_REQUEST, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // Window id

                handler(wrapper -> {
                    String s = wrapper.read(Type.STRING);
                    Integer id;
                    if (s.length() < 19 || (id = Ints.tryParse(s.substring(18))) == null) {
                        wrapper.cancel();
                        return;
                    }

                    wrapper.write(Type.VAR_INT, id);
                });
            }
        });

        registerIncoming(ServerboundPackets1_13.RECIPE_BOOK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Type

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int type = wrapper.get(Type.VAR_INT, 0);

                        if (type == 0) {
                            String s = wrapper.read(Type.STRING);
                            Integer id;
                            // Custom recipes
                            if (s.length() < 19 || (id = Ints.tryParse(s.substring(18))) == null) {
                                wrapper.cancel();
                                return;
                            }

                            wrapper.write(Type.INT, id);
                        }
                        if (type == 1) {
                            wrapper.passthrough(Type.BOOLEAN); // Crafting Recipe Book Open
                            wrapper.passthrough(Type.BOOLEAN); // Crafting Recipe Filter Active
                            wrapper.read(Type.BOOLEAN); // Smelting Recipe Book Open | IGNORE NEW 1.13 FIELD
                            wrapper.read(Type.BOOLEAN); // Smelting Recipe Filter Active | IGNORE NEW 1.13 FIELD
                        }
                    }
                });
            }
        });

        // New 0x1C - Name Item -> Plugin Message
        registerIncoming(ServerboundPackets1_13.RENAME_ITEM, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(wrapper -> {
                    wrapper.write(Type.STRING, "MC|ItemName"); // Channel
                });
            }
        });

        // New 0x1F - Select Trade -> Plugin Message
        registerIncoming(ServerboundPackets1_13.SELECT_TRADE, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(wrapper -> {
                    wrapper.write(Type.STRING, "MC|TrSel"); // Channel
                });
                map(Type.VAR_INT, Type.INT); // Slot
            }
        });

        // New 0x20 - Set Beacon Effect -> Plugin Message
        registerIncoming(ServerboundPackets1_13.SET_BEACON_EFFECT, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(wrapper -> {
                    wrapper.write(Type.STRING, "MC|Beacon"); // Channel
                });
                map(Type.VAR_INT, Type.INT); // Primary Effect
                map(Type.VAR_INT, Type.INT); // Secondary Effect
            }
        });

        // New 0x22 - Update Command Block -> Plugin Message
        registerIncoming(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(wrapper -> wrapper.write(Type.STRING, "MC|AutoCmd"));
                handler(POS_TO_3_INT);
                map(Type.STRING); // Command
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int mode = wrapper.read(Type.VAR_INT);
                        byte flags = wrapper.read(Type.BYTE);

                        String stringMode = mode == 0 ? "SEQUENCE"
                                : mode == 1 ? "AUTO"
                                : "REDSTONE";

                        wrapper.write(Type.BOOLEAN, (flags & 0x1) != 0); // Track output
                        wrapper.write(Type.STRING, stringMode);
                        wrapper.write(Type.BOOLEAN, (flags & 0x2) != 0); // Is conditional
                        wrapper.write(Type.BOOLEAN, (flags & 0x4) != 0); // Automatic
                    }
                });
            }
        });

        // New 0x23 - Update Command Block Minecart -> Plugin Message
        registerIncoming(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK_MINECART, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.STRING, "MC|AdvCmd");
                        wrapper.write(Type.BYTE, (byte) 1); // Type 1 for Entity
                    }
                });
                map(Type.VAR_INT, Type.INT); // Entity Id
            }
        });

        // 0x1B -> 0x24 in InventoryPackets

        // New 0x25 - Update Structure Block -> Message Channel
        registerIncoming(ServerboundPackets1_13.UPDATE_STRUCTURE_BLOCK, ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(wrapper -> {
                    wrapper.write(Type.STRING, "MC|Struct"); // Channel
                });
                handler(POS_TO_3_INT);
                map(Type.VAR_INT, new ValueTransformer<Integer, Byte>(Type.BYTE) { // Action
                    @Override
                    public Byte transform(PacketWrapper wrapper, Integer action) throws Exception {
                        return (byte) (action + 1);
                    }
                }); // Action
                map(Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) {
                    @Override
                    public String transform(PacketWrapper wrapper, Integer mode) throws Exception {
                        return mode == 0 ? "SAVE"
                                : mode == 1 ? "LOAD"
                                : mode == 2 ? "CORNER"
                                : "DATA";
                    }
                });
                map(Type.STRING); // Name
                map(Type.BYTE, Type.INT); // Offset X
                map(Type.BYTE, Type.INT); // Offset Y
                map(Type.BYTE, Type.INT); // Offset Z
                map(Type.BYTE, Type.INT); // Size X
                map(Type.BYTE, Type.INT); // Size Y
                map(Type.BYTE, Type.INT); // Size Z
                map(Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) { // Mirror
                    @Override
                    public String transform(PacketWrapper wrapper, Integer mirror) throws Exception {
                        return mirror == 0 ? "NONE"
                                : mirror == 1 ? "LEFT_RIGHT"
                                : "FRONT_BACK";
                    }
                });
                map(Type.VAR_INT, new ValueTransformer<Integer, String>(Type.STRING) { // Rotation
                    @Override
                    public String transform(PacketWrapper wrapper, Integer rotation) throws Exception {
                        return rotation == 0 ? "NONE"
                                : rotation == 1 ? "CLOCKWISE_90"
                                : rotation == 2 ? "CLOCKWISE_180"
                                : "COUNTERCLOCKWISE_90";
                    }
                });
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        float integrity = wrapper.read(Type.FLOAT);
                        long seed = wrapper.read(Type.VAR_LONG);
                        byte flags = wrapper.read(Type.BYTE);

                        wrapper.write(Type.BOOLEAN, (flags & 0x1) != 0); // Ignore Entities
                        wrapper.write(Type.BOOLEAN, (flags & 0x2) != 0); // Show air
                        wrapper.write(Type.BOOLEAN, (flags & 0x4) != 0); // Show bounding box
                        wrapper.write(Type.FLOAT, integrity);
                        wrapper.write(Type.VAR_LONG, seed);
                    }
                });
            }
        });
    }

    @Override
    protected void onMappingDataLoaded() {
        ConnectionData.init();
        RecipeData.init();
        BlockIdData.init();
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new EntityTracker1_13(userConnection));
        userConnection.put(new TabCompleteTracker(userConnection));
        if (!userConnection.has(ClientWorld.class))
            userConnection.put(new ClientWorld(userConnection));
        userConnection.put(new BlockStorage(userConnection));
        if (Via.getConfig().isServersideBlockConnections()) {
            if (Via.getManager().getProviders().get(BlockConnectionProvider.class) instanceof PacketBlockConnectionProvider) {
                userConnection.put(new BlockConnectionStorage(userConnection));
            }
        }
    }

    @Override
    protected void register(ViaProviders providers) {
        providers.register(BlockEntityProvider.class, new BlockEntityProvider());
        providers.register(PaintingProvider.class, new PaintingProvider());
    }

    // Based on method from https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/ChatColor.java
    public ChatColor getLastColor(String input) {
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);
                if (color != null && !FORMATTING_CODES.contains(color)) {
                    return color;
                }
            }
        }

        return ChatColor.RESET;
    }

    protected String rewriteTeamMemberName(String name) {
        // The Display Name is just colours which overwrites the suffix
        // It also overwrites for ANY colour in name but most plugins
        // will just send colour as 'invisible' character
        if (ChatColor.stripColor(name).isEmpty()) {
            StringBuilder newName = new StringBuilder();
            for (int i = 1; i < name.length(); i += 2) {
                char colorChar = name.charAt(i);
                Character rewrite = SCOREBOARD_TEAM_NAME_REWRITE.get(ChatColor.getByChar(colorChar));
                if (rewrite == null) {
                    rewrite = colorChar;
                }
                newName.append(ChatColor.COLOR_CHAR).append(rewrite);
            }
            name = newName.toString();
        }
        return name;
    }

    public static int[] toPrimitive(Integer[] array) {
        int[] prim = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            prim[i] = array[i];
        }
        return prim;
    }

    @Override
    public MappingData getMappingData() {
        return MAPPINGS;
    }
}
