package viaversion.viaversion.api.type.types.minecraft;

import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.type.Type;

import java.util.List;

public abstract class MetaListTypeTemplate extends Type<List<Metadata>> {

    protected MetaListTypeTemplate() {
        super("MetaData List", List.class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return MetaListTypeTemplate.class;
    }
}
