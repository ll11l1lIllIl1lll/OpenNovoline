package viaversion.viaversion.api.minecraft.metadata;

import viaversion.viaversion.api.type.Type;

public interface MetaType {

    /**
     * Get the write/read type
     *
     * @return Type instance
     */
    Type getType();

    /**
     * Get type id from the specific MetaDataType
     *
     * @return Type id as an integer
     */
    int getTypeID();
}
