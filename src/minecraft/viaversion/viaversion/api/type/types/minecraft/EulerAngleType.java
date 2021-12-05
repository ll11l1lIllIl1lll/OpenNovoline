package viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.EulerAngle;
import viaversion.viaversion.api.type.Type;

public class EulerAngleType extends Type<EulerAngle> {
    public EulerAngleType() {
        super("EulerAngle", EulerAngle.class);
    }

    @Override
    public EulerAngle read(ByteBuf buffer) throws Exception {
        float x = Type.FLOAT.readPrimitive(buffer);
        float y = Type.FLOAT.readPrimitive(buffer);
        float z = Type.FLOAT.readPrimitive(buffer);

        return new EulerAngle(x, y, z);
    }

    @Override
    public void write(ByteBuf buffer, EulerAngle object) throws Exception {
        Type.FLOAT.writePrimitive(buffer, object.getX());
        Type.FLOAT.writePrimitive(buffer, object.getY());
        Type.FLOAT.writePrimitive(buffer, object.getZ());
    }
}