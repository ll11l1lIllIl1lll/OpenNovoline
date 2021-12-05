package viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.minecraft.Environment;
import viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import viaversion.viaversion.api.minecraft.chunks.Chunk;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.type.PartialType;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.minecraft.BaseChunkType;
import viaversion.viaversion.api.type.types.version.Types1_9;
import viaversion.viaversion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

import java.util.ArrayList;
import java.util.BitSet;

public class Chunk1_9_1_2Type extends PartialType<Chunk, ClientWorld> {

    public Chunk1_9_1_2Type(ClientWorld clientWorld) {
        super(clientWorld, "Chunk", Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf input, ClientWorld world) throws Exception {
        boolean replacePistons = world.getUser().getProtocolInfo().getPipeline().contains(Protocol1_10To1_9_3_4.class) && Via.getConfig().isReplacePistons();
        int replacementId = Via.getConfig().getPistonReplacementId();

        int chunkX = input.readInt();
        int chunkZ = input.readInt();

        boolean groundUp = input.readBoolean();
        int primaryBitmask = Type.VAR_INT.readPrimitive(input);
        // Size (unused)
        Type.VAR_INT.readPrimitive(input);

        BitSet usedSections = new BitSet(16);
        ChunkSection[] sections = new ChunkSection[16];
        // Calculate section count from bitmask
        for (int i = 0; i < 16; i++) {
            if ((primaryBitmask & (1 << i)) != 0) {
                usedSections.set(i);
            }
        }

        // Read sections
        for (int i = 0; i < 16; i++) {
            if (!usedSections.get(i)) continue; // Section not set
            ChunkSection section = Types1_9.CHUNK_SECTION.read(input);
            sections[i] = section;
            section.readBlockLight(input);
            if (world.getEnvironment() == Environment.NORMAL) {
                section.readSkyLight(input);
            }
            if (replacePistons) {
                section.replacePaletteEntry(36, replacementId);
            }
        }

        int[] biomeData = groundUp ? new int[256] : null;
        if (groundUp) {
            for (int i = 0; i < 256; i++) {
                biomeData[i] = input.readByte() & 0xFF;
            }
        }

        return new BaseChunk(chunkX, chunkZ, groundUp, false, primaryBitmask, sections, biomeData, new ArrayList<CompoundTag>());
    }

    @Override
    public void write(ByteBuf output, ClientWorld world, Chunk chunk) throws Exception {
        output.writeInt(chunk.getX());
        output.writeInt(chunk.getZ());

        output.writeBoolean(chunk.isFullChunk());
        Type.VAR_INT.writePrimitive(output, chunk.getBitmask());

        ByteBuf buf = output.alloc().buffer();
        try {
            for (int i = 0; i < 16; i++) {
                ChunkSection section = chunk.getSections()[i];
                if (section == null) continue; // Section not set
                Types1_9.CHUNK_SECTION.write(buf, section);
                section.writeBlockLight(buf);

                if (!section.hasSkyLight()) continue; // No sky light, we're done here.
                section.writeSkyLight(buf);

            }
            buf.readerIndex(0);
            Type.VAR_INT.writePrimitive(output, buf.readableBytes() + (chunk.isBiomeData() ? 256 : 0));
            output.writeBytes(buf);
        } finally {
            buf.release(); // release buffer
        }

        // Write biome data
        if (chunk.isBiomeData()) {
            for (int biome : chunk.getBiomeData()) {
                output.writeByte((byte) biome);
            }
        }
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkType.class;
    }
}
