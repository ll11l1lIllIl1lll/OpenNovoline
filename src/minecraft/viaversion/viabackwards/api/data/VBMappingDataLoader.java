package viaversion.viabackwards.api.data;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import viaversion.viabackwards.ViaBackwards;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.MappingDataLoader;
import viaversion.viaversion.util.GsonUtil;

public class VBMappingDataLoader {

    public static JsonObject loadFromDataDir(String name) {
        File file = new File(ViaBackwards.getPlatform().getDataFolder(), name);
        if (!file.exists()) return loadData(name);

        // Load the file from the platform's directory if present
        try (FileReader reader = new FileReader(file)) {
            return GsonUtil.getGson().fromJson(reader, JsonObject.class);
        } catch (JsonSyntaxException e) {
            ViaBackwards.getPlatform().getLogger().warning(name + " is badly formatted!");
            e.printStackTrace();
            ViaBackwards.getPlatform().getLogger().warning("Falling back to resource's file!");
            return loadData(name);
        } catch (IOException | JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject loadData(String name) {
        InputStream stream = VBMappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viabackwards/data/" + name);

        try(InputStreamReader reader = new InputStreamReader(stream)) {
            return GsonUtil.getGson().fromJson(reader, JsonObject.class);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void mapIdentifiers(short[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, JsonObject diffIdentifiers) {
        mapIdentifiers(output, oldIdentifiers, newIdentifiers, diffIdentifiers, true);
    }

    public static void mapIdentifiers(short[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap newIdentifierMap = MappingDataLoader.indexedObjectToMap(newIdentifiers);
        for (Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            String key = entry.getValue().getAsString();
            int mappedId = newIdentifierMap.getInt(key);
            if (mappedId == -1) {
                if (diffIdentifiers != null) {
                    // Search in diff mappings
                    JsonPrimitive diffValueJson = diffIdentifiers.getAsJsonPrimitive(key);
                    String diffValue = diffValueJson != null ? diffValueJson.getAsString() : null;

                    int dataIndex;
                    if (diffValue == null && (dataIndex = key.indexOf('[')) != -1
                            && (diffValueJson = diffIdentifiers.getAsJsonPrimitive(key.substring(0, dataIndex))) != null) {
                        // Check for wildcard mappings
                        diffValue = diffValueJson.getAsString();

                        // Keep original properties if value ends with [
                        if (diffValue.endsWith("[")) {
                            diffValue += key.substring(dataIndex + 1);
                        }
                    }

                    if (diffValue != null) {
                        mappedId = newIdentifierMap.getInt(diffValue);
                    }
                }

                if (mappedId == -1) {
                    // Nothing found :(
                    if (warnOnMissing && !Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                        ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( ");
                    }
                    continue;
                }
            }

            output[Integer.parseInt(entry.getKey())] = (short) mappedId;
        }
    }

    public static Map<String, String> objectToMap(JsonObject object) {
        Map<String, String> mappings = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            if (key.indexOf(':') == -1) {
                key = "minecraft:" + key;
            }
            String value = entry.getValue().getAsString();
            if (value.indexOf(':') == -1) {
                value = "minecraft:" + value;
            }
            mappings.put(key, value);
        }
        return mappings;
    }

    public static Int2ObjectMap<MappedItem> loadItemMappings(JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping) {
        Int2ObjectMap<MappedItem> itemMapping = new Int2ObjectOpenHashMap<>(diffMapping.size(), 1F);
        Object2IntMap newIdenfierMap = MappingDataLoader.indexedObjectToMap(newMapping);
        Object2IntMap oldIdenfierMap = MappingDataLoader.indexedObjectToMap(oldMapping);

        for (Map.Entry<String, JsonElement> entry : diffMapping.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            String mappedIdName = object.getAsJsonPrimitive("id").getAsString();
            int mappedId = newIdenfierMap.getInt(mappedIdName);

            if (mappedId == -1) {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    ViaBackwards.getPlatform().getLogger().warning("No key for " + mappedIdName + " :( ");
                }
                continue;
            }

            int oldId = oldIdenfierMap.getInt(entry.getKey());

            if (oldId == -1) {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    ViaBackwards.getPlatform().getLogger().warning("No old entry for " + mappedIdName + " :( ");
                }
                continue;
            }

            String name = object.getAsJsonPrimitive("name").getAsString();
            itemMapping.put(oldId, new MappedItem(mappedId, name));
        }

        return itemMapping;
    }
}
