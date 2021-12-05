package viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.chunks;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.Protocol1_9_1_2To1_9_3_4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockEntity {
    private static final Map<String, Integer> types = new HashMap<>();

    static {
        types.put("MobSpawner", 1);
        types.put("Control", 2);
        types.put("Beacon", 3);
        types.put("Skull", 4);
        types.put("FlowerPot", 5);
        types.put("Banner", 6);
        types.put("UNKNOWN", 7);
        types.put("EndGateway", 8);
        types.put("Sign", 9);
    }

    public static void handle(List<CompoundTag> tags, UserConnection connection) {
        for (CompoundTag tag : tags) {
            try {
                if (!tag.contains("id"))
                    throw new Exception("NBT tag not handled because the id key is missing");

                String id = (String) tag.get("id").getValue();
                if (!types.containsKey(id))
                    throw new Exception("Not handled id: " + id);

                int newId = types.get(id);
                if (newId == -1)
                    continue;

                int x = (int) tag.get("x").getValue();
                int y = (int) tag.get("y").getValue();
                int z = (int) tag.get("z").getValue();

                Position pos = new Position(x, (short) y, z);

                updateBlockEntity(pos, (short) newId, tag, connection);
            } catch (Exception e) {
                if (Via.getManager().isDebug()) {
                    Via.getPlatform().getLogger().warning("Block Entity: " + e.getMessage() + ": " + tag);
                }
            }
        }
    }

    private static void updateBlockEntity(Position pos, short id, CompoundTag tag, UserConnection connection) throws Exception {
        PacketWrapper wrapper = new PacketWrapper(0x09, null, connection);
        wrapper.write(Type.POSITION, pos);
        wrapper.write(Type.UNSIGNED_BYTE, id);
        wrapper.write(Type.NBT, tag);
        wrapper.send(Protocol1_9_1_2To1_9_3_4.class, false);
    }
}
