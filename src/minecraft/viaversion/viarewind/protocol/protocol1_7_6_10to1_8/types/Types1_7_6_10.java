package viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.type.Type;

import java.util.List;

public class Types1_7_6_10 {

    public static final Type<CompoundTag> COMPRESSED_NBT = new CompressedNBTType();
    public static final Type<Item[]> ITEM_ARRAY = new ItemArrayType(false);
    public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY = new ItemArrayType(true);
    public static final Type<Item> ITEM = new ItemType(false);
    public static final Type<Item> COMPRESSED_NBT_ITEM = new ItemType(true);
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataListType();
    public static final Type<Metadata> METADATA = new MetadataType();
    public static final Type<CompoundTag> NBT = new NBTType();
    public static final Type<int[]> INT_ARRAY = new IntArrayType();
}
