package viaversion.viaversion.api.type.types.version;

import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.type.Type;

import java.util.List;

public class Types1_12 {
    /**
     * Metadata list type for 1.12
     */
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataList1_12Type();

    /**
     * Metadata type for 1.12
     */
    public static final Type<Metadata> METADATA = new Metadata1_12Type();
}
