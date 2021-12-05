package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumWorldBlockLayer;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Material var1, boolean var2) {
      super(var1, var2);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int quantityDropped(Random var1) {
      return 0;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public boolean isFullCube() {
      return false;
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
