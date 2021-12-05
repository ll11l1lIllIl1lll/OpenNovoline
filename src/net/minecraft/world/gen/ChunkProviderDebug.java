package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider {
   private static final List field_177464_a = Lists.newArrayList();
   private static final int field_177462_b;
   private static final int field_181039_c;
   private final World world;

   public ChunkProviderDebug(World var1) {
      this.world = var1;
   }

   public Chunk provideChunk(int var1, int var2) {
      ChunkPrimer var3 = new ChunkPrimer();

      for(int var4 = 0; var4 < 16; ++var4) {
         for(int var5 = 0; var5 < 16; ++var5) {
            int var6 = var1 * 16 + var4;
            int var7 = var2 * 16 + var5;
            var3.setBlockState(var4, 60, var5, Blocks.barrier.getDefaultState());
            IBlockState var8 = func_177461_b(var6, var7);
            var3.setBlockState(var4, 70, var5, var8);
         }
      }

      Chunk var9 = new Chunk(this.world, var3, var1, var2);
      var9.generateSkylightMap();
      BiomeGenBase[] var10 = this.world.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var11 = var9.getBiomeArray();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         var11[var12] = (byte)var10[var12].biomeID;
      }

      var9.generateSkylightMap();
      return var9;
   }

   public static IBlockState func_177461_b(int var0, int var1) {
      IBlockState var2 = null;
      if(var0 % 2 != 0 && var1 % 2 != 0) {
         var0 = var0 / 2;
         var1 = var1 / 2;
         if(var0 <= field_177462_b && var1 <= field_181039_c) {
            int var3 = MathHelper.abs_int(var0 * field_177462_b + var1);
            if(var3 < field_177464_a.size()) {
               var2 = (IBlockState)field_177464_a.get(var3);
            }
         }
      }

      return var2;
   }

   public boolean chunkExists(int var1, int var2) {
      return true;
   }

   public void populate(IChunkProvider var1, int var2, int var3) {
   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   public boolean saveChunks(boolean var1, IProgressUpdate var2) {
      return true;
   }

   public void saveExtraData() {
   }

   public boolean unloadQueuedChunks() {
      return false;
   }

   public boolean canSave() {
      return true;
   }

   public String makeString() {
      return "DebugLevelSource";
   }

   public List getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.world.getBiomeGenForCoords(var2);
      return var3.getSpawnableList(var1);
   }

   public BlockPos getStrongholdGen(World var1, String var2, BlockPos var3) {
      return null;
   }

   public int getLoadedChunkCount() {
      return 0;
   }

   public void recreateStructures(Chunk var1, int var2, int var3) {
   }

   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   static {
      for(Block var2 : Block.blockRegistry) {
         field_177464_a.addAll(var2.getBlockState().getValidStates());
      }

      field_177462_b = MathHelper.ceiling_float_int(MathHelper.sqrt_float((float)field_177464_a.size()));
      field_181039_c = MathHelper.ceiling_float_int((float)field_177464_a.size() / (float)field_177462_b);
   }
}
