package viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.ObjectArrays;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import viaversion.viaversion.util.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BlockIdData {
    public static final String[] PREVIOUS = new String[0];
    public static Map<String, String[]> blockIdMapping;
    public static Map<String, String[]> fallbackReverseMapping;
    public static Int2ObjectMap<String> numberIdToString;

    public static void init() {
        InputStream stream = MappingData.class.getClassLoader()
                .getResourceAsStream("assets/viaversion/data/blockIds1.12to1.13.json");
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            Map<String, String[]> map = GsonUtil.getGson().fromJson(
                    reader,
                    new TypeToken<Map<String, String[]>>() {
                    }.getType());
            blockIdMapping = new HashMap<>(map);
            fallbackReverseMapping = new HashMap<>();
            for (Map.Entry<String, String[]> entry : blockIdMapping.entrySet()) {
                for (String val : entry.getValue()) {
                    String[] previous = fallbackReverseMapping.get(val);
                    if (previous == null) previous = PREVIOUS;
                    fallbackReverseMapping.put(val, ObjectArrays.concat(previous, entry.getKey()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream blockS = MappingData.class.getClassLoader()
                .getResourceAsStream("assets/viaversion/data/blockNumberToString1.12.json");
        try (InputStreamReader blockR = new InputStreamReader(blockS)) {
            Map<Integer, String> map = GsonUtil.getGson().fromJson(
                    blockR,
                    new TypeToken<Map<Integer, String>>() {
                    }.getType()
            );
            numberIdToString = new Int2ObjectOpenHashMap<>(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Ignored
    }
}
