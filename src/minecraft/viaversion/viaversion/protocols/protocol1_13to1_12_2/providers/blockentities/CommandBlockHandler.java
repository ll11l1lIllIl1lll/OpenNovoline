package viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.google.gson.JsonElement;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import viaversion.viaversion.util.GsonUtil;

public class CommandBlockHandler implements BlockEntityProvider.BlockEntityHandler {
    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        Tag name = tag.get("CustomName");
        if (name instanceof StringTag) {
            ((StringTag) name).setValue(ChatRewriter.legacyTextToJsonString(((StringTag) name).getValue()));
        }
        Tag out = tag.get("LastOutput");
        if (out instanceof StringTag) {
            JsonElement value = GsonUtil.getJsonParser().parse(((StringTag) out).getValue());
            ChatRewriter.processTranslate(value);
            ((StringTag) out).setValue(value.toString());
        }
        return -1;
    }
}
