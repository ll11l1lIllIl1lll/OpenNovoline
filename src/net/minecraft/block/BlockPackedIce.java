package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockPackedIce extends Block {
   public BlockPackedIce() {
      super(Material.packedIce);
      this.slipperiness = 0.98F;
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int quantityDropped(Random var1) {
      return 0;
   }
}
