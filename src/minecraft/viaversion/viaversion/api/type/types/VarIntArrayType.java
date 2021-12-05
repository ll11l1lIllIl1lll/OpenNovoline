package viaversion.viaversion.api.type.types;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.type.Type;

public class VarIntArrayType extends Type<int[]> {
    public VarIntArrayType() {
        super("int[]", int[].class);
    }

    @Override
    public int[] read(ByteBuf buffer) throws Exception {
        int length = Type.VAR_INT.readPrimitive(buffer);
        Preconditions.checkArgument(buffer.isReadable(length)); // Sanity check, at least 1 byte will be used for each varint
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Type.VAR_INT.readPrimitive(buffer);
        }
        return array;
    }

    @Override
    public void write(ByteBuf buffer, int[] object) throws Exception {
        Type.VAR_INT.writePrimitive(buffer, object.length);
        for (int i : object) {
            Type.VAR_INT.writePrimitive(buffer, i);
        }
    }
}
