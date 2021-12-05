package viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.github.steveice10.opennbt.tag.builtin.*;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;

public class BannerHandler implements BlockEntityProvider.BlockEntityHandler {
    private static final int WALL_BANNER_START = 7110; // 4 each
    private static final int WALL_BANNER_STOP = 7173;

    private static final int BANNER_START = 6854; // 16 each
    private static final int BANNER_STOP = 7109;

    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        BlockStorage storage = user.get(BlockStorage.class);
        Position position = new Position((int) getLong(tag.get("x")), (short) getLong(tag.get("y")), (int) getLong(tag.get("z")));

        if (!storage.contains(position)) {
            Via.getPlatform().getLogger().warning("Received an banner color update packet, but there is no banner! O_o " + tag);
            return -1;
        }

        int blockId = storage.get(position).getOriginal();

        Tag base = tag.get("Base");
        int color = 0;
        if (base != null) {
            color = ((Number) tag.get("Base").getValue()).intValue();
        }
        // Standing banner
        if (blockId >= BANNER_START && blockId <= BANNER_STOP) {
            blockId += ((15 - color) * 16);
            // Wall banner
        } else if (blockId >= WALL_BANNER_START && blockId <= WALL_BANNER_STOP) {
            blockId += ((15 - color) * 4);
        } else {
            Via.getPlatform().getLogger().warning("Why does this block have the banner block entity? :(" + tag);
        }

        if (tag.get("Patterns") instanceof ListTag) {
            for (Tag pattern : (ListTag) tag.get("Patterns")) {
                if (pattern instanceof CompoundTag) {
                    Tag c = ((CompoundTag) pattern).get("Color");
                    if (c instanceof IntTag) {
                        ((IntTag)c).setValue(15 - (int) c.getValue()); // Invert color id
                    }
                }
            }
        }

        Tag name = tag.get("CustomName");
        if (name instanceof StringTag) {
            ((StringTag) name).setValue(ChatRewriter.legacyTextToJsonString(((StringTag) name).getValue()));
        }

        return blockId;
    }

    private long getLong(Tag tag) {
        return ((Integer) tag.getValue()).longValue();
    }
}