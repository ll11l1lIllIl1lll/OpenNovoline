package viaversion.viaversion.api.type.types.version;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.util.CompactArrayUtil;

public class ChunkSectionType1_16 extends Type<ChunkSection> {
    private static final int GLOBAL_PALETTE = 15;

    public ChunkSectionType1_16() {
        super("Chunk Section Type", ChunkSection.class);
    }

    @Override
    public ChunkSection read(ByteBuf buffer) throws Exception {
        // Reaad bits per block
        int bitsPerBlock = buffer.readUnsignedByte();
        int originalBitsPerBlock = bitsPerBlock;

        if (bitsPerBlock == 0 || bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        // Read palette
        ChunkSection chunkSection;
        if (bitsPerBlock != GLOBAL_PALETTE) {
            int paletteLength = Type.VAR_INT.readPrimitive(buffer);
            chunkSection = new ChunkSection(paletteLength);
            for (int i = 0; i < paletteLength; i++) {
                chunkSection.addPaletteEntry(Type.VAR_INT.readPrimitive(buffer));
            }
        } else {
            chunkSection = new ChunkSection();
        }

        // Read blocks
        long[] blockData = new long[Type.VAR_INT.readPrimitive(buffer)];
        if (blockData.length > 0) {
            char valuesPerLong = (char) (64 / bitsPerBlock);
            int expectedLength = (ChunkSection.SIZE + valuesPerLong - 1) / valuesPerLong;
            if (blockData.length != expectedLength) {
                throw new IllegalStateException("Block data length (" + blockData.length + ") does not match expected length (" + expectedLength + ")! bitsPerBlock=" + bitsPerBlock + ", originalBitsPerBlock=" + originalBitsPerBlock);
            }

            for (int i = 0; i < blockData.length; i++) {
                blockData[i] = buffer.readLong();
            }
            CompactArrayUtil.iterateCompactArrayWithPadding(bitsPerBlock, ChunkSection.SIZE, blockData,
                    bitsPerBlock == GLOBAL_PALETTE ? chunkSection::setFlatBlock : chunkSection::setPaletteIndex);
        }

        return chunkSection;
    }

    @Override
    public void write(ByteBuf buffer, ChunkSection chunkSection) throws Exception {
        int bitsPerBlock = 4;
        while (chunkSection.getPaletteSize() > 1 << bitsPerBlock) {
            bitsPerBlock += 1;
        }

        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        buffer.writeByte(bitsPerBlock);

        // Write pallet (or not)
        if (bitsPerBlock != GLOBAL_PALETTE) {
            Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteSize());
            for (int i = 0; i < chunkSection.getPaletteSize(); i++) {
                Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteEntry(i));
            }
        }

        long[] data = CompactArrayUtil.createCompactArrayWithPadding(bitsPerBlock, ChunkSection.SIZE,
                bitsPerBlock == GLOBAL_PALETTE ? chunkSection::getFlatBlock : chunkSection::getPaletteIndex);
        Type.VAR_INT.writePrimitive(buffer, data.length);
        for (long l : data) {
            buffer.writeLong(l);
        }
    }
}
