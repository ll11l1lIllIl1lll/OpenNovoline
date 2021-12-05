package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
   private final short[] data = new short[65536];
   private final IBlockState b = Blocks.air.getDefaultState();

   public IBlockState getBlockState(int var1, int var2, int var3) {
      int var4 = var1 << 12 | var3 << 8 | var2;
      return this.getBlockState(var4);
   }

   public IBlockState getBlockState(int var1) {
      if(var1 < this.data.length) {
         IBlockState var2 = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(this.data[var1]);
         return var2;
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }

   public void setBlockState(int var1, int var2, int var3, IBlockState var4) {
      int var5 = var1 << 12 | var3 << 8 | var2;
      this.setBlockState(var5, var4);
   }

   public void setBlockState(int var1, IBlockState var2) {
      if(var1 < this.data.length) {
         this.data[var1] = (short)Block.BLOCK_STATE_IDS.get(var2);
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }

   private static IndexOutOfBoundsException a(IndexOutOfBoundsException var0) {
      return var0;
   }
}
