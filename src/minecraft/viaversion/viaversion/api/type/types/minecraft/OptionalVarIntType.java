package viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.type.Type;

public class OptionalVarIntType extends Type<Integer> {
    public OptionalVarIntType() {
        super("Integer", Integer.class);
    }

    @Override
    public Integer read(ByteBuf buffer) throws Exception {
        int read = Type.VAR_INT.readPrimitive(buffer);
        if (read == 0) return null;
        return read - 1;
    }

    @Override
    public void write(ByteBuf buffer, Integer object) throws Exception {
        if (object == null) Type.VAR_INT.writePrimitive(buffer, 0);
        else Type.VAR_INT.writePrimitive(buffer, object + 1);
    }
}
