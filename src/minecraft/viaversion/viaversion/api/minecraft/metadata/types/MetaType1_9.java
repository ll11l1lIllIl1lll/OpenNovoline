package viaversion.viaversion.api.minecraft.metadata.types;

import viaversion.viaversion.api.minecraft.metadata.MetaType;
import viaversion.viaversion.api.type.Type;

public enum MetaType1_9 implements MetaType {
    Byte(0, Type.BYTE),
    VarInt(1, Type.VAR_INT),
    Float(2, Type.FLOAT),
    String(3, Type.STRING),
    Chat(4, Type.COMPONENT),
    Slot(5, Type.ITEM),
    Boolean(6, Type.BOOLEAN),
    Vector3F(7, Type.ROTATION),
    Position(8, Type.POSITION),
    OptPosition(9, Type.OPTIONAL_POSITION),
    Direction(10, Type.VAR_INT),
    OptUUID(11, Type.OPTIONAL_UUID),
    BlockID(12, Type.VAR_INT),
    Discontinued(99, null);

    private final int typeID;
    private final Type type;

    MetaType1_9(int typeID, Type type) {
        this.typeID = typeID;
        this.type = type;
    }

    public static MetaType1_9 byId(int id) {
        return values()[id];
    }

    @Override
    public int getTypeID() {
        return typeID;
    }

    @Override
    public Type getType() {
        return type;
    }
}
