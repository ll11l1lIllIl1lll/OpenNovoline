package net.minecraft.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenBeach extends BiomeGenBase {
   public BiomeGenBeach(int var1) {
      super(var1);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState();
      this.fillerBlock = Blocks.sand.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 0;
      this.theBiomeDecorator.reedsPerChunk = 0;
      this.theBiomeDecorator.cactiPerChunk = 0;
   }
}
