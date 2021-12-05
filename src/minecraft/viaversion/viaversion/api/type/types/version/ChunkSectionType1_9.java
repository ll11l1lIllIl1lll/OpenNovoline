package viaversion.viaversion.api.type.types.version;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.util.CompactArrayUtil;

public class ChunkSectionType1_9 extends Type<ChunkSection> {
    private static final int GLOBAL_PALETTE = 13;

    public ChunkSectionType1_9() {
        super("Chunk Section Type", ChunkSection.class);
    }

    @Override
    public ChunkSection read(ByteBuf buffer) throws Exception {
        // Reaad bits per block
        int bitsPerBlock = buffer.readUnsignedByte();
        int originalBitsPerBlock = bitsPerBlock;

        if (bitsPerBlock == 0) {
            bitsPerBlock = GLOBAL_PALETTE;
        }
        if (bitsPerBlock < 4) {
            bitsPerBlock = 4;
        }
        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        // Read palette
        int paletteLength = Type.VAR_INT.readPrimitive(buffer);
        ChunkSection chunkSection = bitsPerBlock != GLOBAL_PALETTE ? new ChunkSection(paletteLength) : new ChunkSection();
        for (int i = 0; i < paletteLength; i++) {
            if (bitsPerBlock != GLOBAL_PALETTE) {
                chunkSection.addPaletteEntry(Type.VAR_INT.readPrimitive(buffer));
            } else {
                Type.VAR_INT.readPrimitive(buffer);
            }
        }

        // Read blocks
        long[] blockData = new long[Type.VAR_INT.readPrimitive(buffer)];
        if (blockData.length > 0) {
            int expectedLength = (int) Math.ceil(ChunkSection.SIZE * bitsPerBlock / 64.0);
            if (blockData.length != expectedLength) {
                throw new IllegalStateException("Block data length (" + blockData.length + ") does not match expected length (" + expectedLength + ")! bitsPerBlock=" + bitsPerBlock + ", originalBitsPerBlock=" + originalBitsPerBlock);
            }

            for (int i = 0; i < blockData.length; i++) {
                blockData[i] = buffer.readLong();
            }
            CompactArrayUtil.iterateCompactArray(bitsPerBlock, ChunkSection.SIZE, blockData,
                    bitsPerBlock == GLOBAL_PALETTE ? chunkSection::setFlatBlock
                            : chunkSection::setPaletteIndex);
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

        long maxEntryValue = (1L << bitsPerBlock) - 1;
        buffer.writeByte(bitsPerBlock);

        // Write pallet (or not)
        if (bitsPerBlock != GLOBAL_PALETTE) {
            Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteSize());
            for (int i = 0; i < chunkSection.getPaletteSize(); i++) {
                Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteEntry(i));
            }
        } else {
            Type.VAR_INT.writePrimitive(buffer, 0);
        }

        long[] data = CompactArrayUtil.createCompactArray(bitsPerBlock, ChunkSection.SIZE,
                bitsPerBlock == GLOBAL_PALETTE ? chunkSection::getFlatBlock : chunkSection::getPaletteIndex);
        Type.VAR_INT.writePrimitive(buffer, data.length);
        for (long l : data) {
            buffer.writeLong(l);
        }
    }
}
