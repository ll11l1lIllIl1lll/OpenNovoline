package viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import viaversion.viaversion.api.type.Type;

public class CompressedNBTType extends Type<CompoundTag> {

	public CompressedNBTType() {
		super("CompoundTag", CompoundTag.class);
	}

	@Override
	public CompoundTag read(ByteBuf buffer) {
		short length = buffer.readShort();
		if(length <= 0) {
			return null;
		}

		byte[] compressed = new byte[length];
		buffer.readBytes(compressed);
		byte[] uncompressed = decompress(compressed);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(uncompressed);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		try {
			return (CompoundTag) NBTIO.readTag((DataInput) dataInputStream);
		} catch(Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				dataInputStream.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
		if(nbt == null) {
			buffer.writeShort(-1);
		} else {
			ByteBuf buf = buffer.alloc().buffer();
			ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buf);
			DataOutputStream dataOutputStream = new DataOutputStream(bytebufStream);
			NBTIO.writeTag((DataOutput) dataOutputStream, nbt);
			dataOutputStream.close();
			byte[] uncompressed = new byte[buf.readableBytes()];
			buf.readBytes(uncompressed);
			buf.release();
			byte[] compressed = compress(uncompressed);
			buffer.writeShort(compressed.length);
			buffer.writeBytes(compressed);
		}
	}

	public static byte[] compress(byte[] content) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(content);
			gzipOutputStream.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] decompress(byte[] contentBytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(contentBytes));
			while(stream.available() > 0) {
				out.write(stream.read());
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}
}
