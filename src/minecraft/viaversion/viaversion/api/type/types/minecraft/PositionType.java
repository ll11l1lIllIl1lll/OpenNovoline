package viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.type.Type;

public class PositionType extends Type<Position> {
    public PositionType() {
        super("Position", Position.class);
    }

    @Override
    public Position read(ByteBuf buffer) {
        long val = buffer.readLong();
        long x = (val >> 38); // signed
        long y = (val >> 26) & 0xfff; // unsigned
        // this shifting madness is used to preserve sign
        long z = (val << 38) >> 38; // signed

        return new Position((int) x, (short) y, (int) z);
    }

    @Override
    public void write(ByteBuf buffer, Position object) {
        buffer.writeLong((((long) object.getX() & 0x3ffffff) << 38)
                | ((((long) object.getY()) & 0xfff) << 26)
                | (object.getZ() & 0x3ffffff));
    }
}
