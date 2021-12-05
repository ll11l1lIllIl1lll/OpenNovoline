package net.minecraft.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenStoneBeach extends BiomeGenBase {
   public BiomeGenStoneBeach(int var1) {
      super(var1);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.stone.getDefaultState();
      this.fillerBlock = Blocks.stone.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 0;
      this.theBiomeDecorator.reedsPerChunk = 0;
      this.theBiomeDecorator.cactiPerChunk = 0;
   }
}
