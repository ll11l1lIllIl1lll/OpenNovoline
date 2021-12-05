package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
   protected BlockStaticLiquid(Material var1) {
      super(var1);
      this.setTickRandomly(false);
      if(var1 == Material.lava) {
         this.setTickRandomly(true);
      }

   }

   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if(!this.checkForMixing(var1, var2, var3)) {
         this.updateLiquid(var1, var2, var3);
      }

   }

   private void updateLiquid(World var1, BlockPos var2, IBlockState var3) {
      BlockDynamicLiquid var4 = getFlowingBlock(this.blockMaterial);
      var1.setBlockState(var2, var4.getDefaultState().withProperty(P, var3.getValue(P)), 2);
      var1.scheduleUpdate(var2, var4, this.tickRate(var1));
   }

   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if(this.blockMaterial == Material.lava && var1.getGameRules().getBoolean("doFireTick")) {
         int var5 = var4.nextInt(3);
         BlockPos var6 = var2;

         for(int var7 = 0; var7 < var5; ++var7) {
            var6 = var6.a(var4.nextInt(3) - 1, 1, var4.nextInt(3) - 1);
            Block var8 = var1.getBlockState(var6).getBlock();
            if(var8.blockMaterial == Material.air) {
               if(this.isSurroundingBlockFlammable(var1, var6)) {
                  var1.setBlockState(var6, Blocks.fire.getDefaultState());
                  return;
               }
            } else if(var8.blockMaterial.blocksMovement()) {
               return;
            }
         }
      }

   }

   protected boolean isSurroundingBlockFlammable(World var1, BlockPos var2) {
      for(EnumFacing var6 : EnumFacing.values()) {
         if(this.getCanBlockBurn(var1, var2.offset(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getBlock().getMaterial().getCanBurn();
   }
}
