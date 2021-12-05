package viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import viaversion.viabackwards.ViaBackwards;
import viaversion.viabackwards.api.data.VBMappings;
import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.MappingDataLoader;
import viaversion.viaversion.api.data.Mappings;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticMappings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BackwardsMappings extends viaversion.viabackwards.api.data.BackwardsMappings {
    private final Int2ObjectMap<String> statisticMappings = new Int2ObjectOpenHashMap<>();
    private final Map<String, String> translateMappings = new HashMap<>();
    private Mappings enchantmentMappings;

    public BackwardsMappings() {
        super("1.13", "1.12", Protocol1_13To1_12_2.class, true);
    }

    @Override
    public void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
        enchantmentMappings = new VBMappings(oldMappings.getAsJsonObject("enchantments"), newMappings.getAsJsonObject("enchantments"), false);
        for (Map.Entry<String, Integer> entry : StatisticMappings.CUSTOM_STATS.entrySet()) {
            statisticMappings.put(entry.getValue().intValue(), entry.getKey());
        }
        for (Map.Entry<String, String> entry : Protocol1_13To1_12_2.MAPPINGS.getTranslateMapping().entrySet()) {
            translateMappings.put(entry.getValue(), entry.getKey());
        }
    }

    // Has lots of compat layers, so we can't use the default Via method
    private static void mapIdentifiers(short[] output, JsonObject newIdentifiers, JsonObject oldIdentifiers, JsonObject mapping) {
        Object2IntMap newIdentifierMap = MappingDataLoader.indexedObjectToMap(oldIdentifiers);
        for (Map.Entry<String, JsonElement> entry : newIdentifiers.entrySet()) {
            String key = entry.getValue().getAsString();
            int value = newIdentifierMap.getInt(key);
            short hardId = -1;
            if (value == -1) {
                JsonPrimitive replacement = mapping.getAsJsonPrimitive(key);
                int propertyIndex;
                if (replacement == null && (propertyIndex = key.indexOf('[')) != -1) {
                    replacement = mapping.getAsJsonPrimitive(key.substring(0, propertyIndex));
                }
                if (replacement != null) {
                    if (replacement.getAsString().startsWith("id:")) {
                        String id = replacement.getAsString().replace("id:", "");
                        hardId = Short.parseShort(id);
                        value = newIdentifierMap.getInt(oldIdentifiers.getAsJsonPrimitive(id).getAsString());
                    } else {
                        value = newIdentifierMap.getInt(replacement.getAsString());
                    }
                }
                if (value == -1) {
                    if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                        if (replacement != null) {
                            ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + "/" + replacement.getAsString() + " :( ");
                        } else {
                            ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( ");
                        }
                    }
                    continue;
                }
            }
            output[Integer.parseInt(entry.getKey())] = hardId != -1 ? hardId : (short) value;
        }
    }

    @Override
    @Nullable
    protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (key.equals("blockstates")) {
            short[] oldToNew = new short[8582];
            Arrays.fill(oldToNew, (short) -1);
            mapIdentifiers(oldToNew, oldMappings.getAsJsonObject("blockstates"), newMappings.getAsJsonObject("blocks"), diffMappings.getAsJsonObject("blockstates"));
            return new Mappings(oldToNew);
        } else {
            return super.loadFromObject(oldMappings, newMappings, diffMappings, key);
        }
    }

    @Override
    protected int checkValidity(int id, int mappedId, String type) {
        // Don't warn for missing ids here
        return mappedId;
    }

    public Int2ObjectMap<String> getStatisticMappings() {
        return statisticMappings;
    }

    public Map<String, String> getTranslateMappings() {
        return translateMappings;
    }

    public Mappings getEnchantmentMappings() {
        return enchantmentMappings;
    }
}
