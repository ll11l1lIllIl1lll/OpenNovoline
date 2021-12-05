package viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.VillagerData;
import viaversion.viaversion.api.type.Type;

public class VillagerDataType extends Type<VillagerData> {
    public VillagerDataType() {
        super("VillagerData", VillagerData.class);
    }

    @Override
    public VillagerData read(ByteBuf buffer) throws Exception {
        return new VillagerData(Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer));
    }

    @Override
    public void write(ByteBuf buffer, VillagerData object) throws Exception {
        Type.VAR_INT.writePrimitive(buffer, object.getType());
        Type.VAR_INT.writePrimitive(buffer, object.getProfession());
        Type.VAR_INT.writePrimitive(buffer, object.getLevel());
    }
}
