package viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.MappingDataLoader;
import viaversion.viaversion.api.data.Mappings;
import viaversion.viaversion.util.GsonUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MappingData extends viaversion.viaversion.api.data.MappingData {
    private final Map<String, Integer[]> blockTags = new HashMap<>();
    private final Map<String, Integer[]> itemTags = new HashMap<>();
    private final Map<String, Integer[]> fluidTags = new HashMap<>();
    private final BiMap<Short, String> oldEnchantmentsIds = HashBiMap.create();
    private final Map<String, String> translateMapping = new HashMap<>();
    private final Map<String, String> mojangTranslation = new HashMap<>();
    private final BiMap<String, String> channelMappings = HashBiMap.create(); // 1.12->1.13
    private Mappings enchantmentMappings;

    public MappingData() {
        super("1.12", "1.13");
    }

    @Override
    public void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
        loadTags(blockTags, newMappings.getAsJsonObject("block_tags"));
        loadTags(itemTags, newMappings.getAsJsonObject("item_tags"));
        loadTags(fluidTags, newMappings.getAsJsonObject("fluid_tags"));

        loadEnchantments(oldEnchantmentsIds, oldMappings.getAsJsonObject("enchantments"));
        enchantmentMappings = new Mappings(72, oldMappings.getAsJsonObject("enchantments"), newMappings.getAsJsonObject("enchantments"));

        // Map minecraft:snow[layers=1] of 1.12 to minecraft:snow[layers=2] in 1.13
        if (Via.getConfig().isSnowCollisionFix()) {
            blockMappings.getOldToNew()[1248] = 3416;
        }

        // Remap infested blocks, as they are instantly breakabale in 1.13+ and can't be broken by those clients on older servers
        if (Via.getConfig().isInfestedBlocksFix()) {
            short[] oldToNew = blockMappings.getOldToNew();
            oldToNew[1552] = 1; // stone
            oldToNew[1553] = 14; // cobblestone
            oldToNew[1554] = 3983; // stone bricks
            oldToNew[1555] = 3984; // mossy stone bricks
            oldToNew[1556] = 3985; // cracked stone bricks
            oldToNew[1557] = 3986; // chiseled stone bricks
        }

        JsonObject object = MappingDataLoader.loadFromDataDir("channelmappings-1.13.json");
        if (object != null) {
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String oldChannel = entry.getKey();
                String newChannel = entry.getValue().getAsString();
                if (!isValid1_13Channel(newChannel)) {
                    Via.getPlatform().getLogger().warning("Channel '" + newChannel + "' is not a valid 1.13 plugin channel, please check your configuration!");
                    continue;
                }
                channelMappings.put(oldChannel, newChannel);
            }
        }

        Map<String, String> translateData = GsonUtil.getGson().fromJson(
                new InputStreamReader(MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/mapping-lang-1.12-1.13.json")),
                new TypeToken<Map<String, String>>() {
                }.getType());
        try {
            String[] lines;
            try (Reader reader = new InputStreamReader(MappingData.class.getClassLoader()
                    .getResourceAsStream("mojang-translations/en_US.properties"), StandardCharsets.UTF_8)) {
                lines = CharStreams.toString(reader).split("\n");
            }
            for (String line : lines) {
                if (line.isEmpty()) continue;

                String[] keyAndTranslation = line.split("=", 2);
                if (keyAndTranslation.length != 2) continue;

                String key = keyAndTranslation[0];
                if (!translateData.containsKey(key)) {
                    String translation = keyAndTranslation[1].replaceAll("%(\\d\\$)?d", "%$1s");
                    mojangTranslation.put(key, translation);
                } else {
                    String dataValue = translateData.get(key);
                    if (dataValue != null) {
                        translateMapping.put(key, dataValue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (key.equals("blocks")) {
            // Need to use a custom size since there are larger gaps in ids
            return new Mappings(4084, oldMappings.getAsJsonObject("blocks"), newMappings.getAsJsonObject("blockstates"));
        } else {
            return super.loadFromObject(oldMappings, newMappings, diffMappings, key);
        }
    }

    public static String validateNewChannel(String newId) {
        if (!isValid1_13Channel(newId)) {
            return null; // Not valid
        }
        int separatorIndex = newId.indexOf(':');
        // Vanilla parses ``:`` and ```` as ``minecraft:`` (also ensure there's enough space)
        if ((separatorIndex == -1 || separatorIndex == 0) && newId.length() <= 10) {
            newId = "minecraft:" + newId;
        }
        return newId;
    }

    public static boolean isValid1_13Channel(String channelId) {
        return channelId.matches("([0-9a-z_.-]+):([0-9a-z_/.-]+)");
    }

    private void loadTags(Map<String, Integer[]> output, JsonObject newTags) {
        for (Map.Entry<String, JsonElement> entry : newTags.entrySet()) {
            JsonArray ids = entry.getValue().getAsJsonArray();
            Integer[] idsArray = new Integer[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                idsArray[i] = ids.get(i).getAsInt();
            }
            output.put(entry.getKey(), idsArray);
        }
    }

    private void loadEnchantments(Map<Short, String> output, JsonObject enchantments) {
        for (Map.Entry<String, JsonElement> enchantment : enchantments.entrySet()) {
            output.put(Short.parseShort(enchantment.getKey()), enchantment.getValue().getAsString());
        }
    }

    public Map<String, Integer[]> getBlockTags() {
        return blockTags;
    }

    public Map<String, Integer[]> getItemTags() {
        return itemTags;
    }

    public Map<String, Integer[]> getFluidTags() {
        return fluidTags;
    }

    public BiMap<Short, String> getOldEnchantmentsIds() {
        return oldEnchantmentsIds;
    }

    public Map<String, String> getTranslateMapping() {
        return translateMapping;
    }

    public Map<String, String> getMojangTranslation() {
        return mojangTranslation;
    }

    public BiMap<String, String> getChannelMappings() {
        return channelMappings;
    }

    public Mappings getEnchantmentMappings() {
        return enchantmentMappings;
    }
}
