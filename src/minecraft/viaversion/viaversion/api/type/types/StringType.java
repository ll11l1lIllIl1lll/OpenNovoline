package viaversion.viaversion.api.type.types;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.type.Type;

import java.nio.charset.StandardCharsets;

public class StringType extends Type<String> {
    // String#length() (used to limit the string in Minecraft source code) uses char[]#length
    private static final int maxJavaCharUtf8Length = Character.toString(Character.MAX_VALUE)
            .getBytes(StandardCharsets.UTF_8).length;
    private final int maxLength;

    public StringType() {
        this(Short.MAX_VALUE);
    }

    public StringType(int maxLength) {
        super("String", String.class);
        this.maxLength = maxLength;
    }

    @Override
    public String read(ByteBuf buffer) throws Exception {
        int len = Type.VAR_INT.readPrimitive(buffer);

        Preconditions.checkArgument(len <= maxLength * maxJavaCharUtf8Length,
                "Cannot receive string longer than Short.MAX_VALUE * "  + maxJavaCharUtf8Length + " bytes (got %s bytes)", len);

        String string = buffer.toString(buffer.readerIndex(), len, StandardCharsets.UTF_8);
        buffer.skipBytes(len);

        Preconditions.checkArgument(string.length() <= maxLength,
                "Cannot receive string longer than Short.MAX_VALUE characters (got %s bytes)", string.length());

        return string;
    }

    @Override
    public void write(ByteBuf buffer, String object) throws Exception {
        Preconditions.checkArgument(object.length() <= maxLength, "Cannot send string longer than Short.MAX_VALUE (got %s characters)", object.length());

        byte[] b = object.getBytes(StandardCharsets.UTF_8);
        Type.VAR_INT.writePrimitive(buffer, b.length);
        buffer.writeBytes(b);
    }
}
