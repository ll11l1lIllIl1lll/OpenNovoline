package net.minecraft.world.biome;

import net.nA;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenMushroomIsland extends BiomeGenBase {
   public BiomeGenMushroomIsland(int var1) {
      super(var1);
      this.theBiomeDecorator.treesPerChunk = -100;
      this.theBiomeDecorator.flowersPerChunk = -100;
      this.theBiomeDecorator.grassPerChunk = -100;
      this.theBiomeDecorator.mushroomsPerChunk = 1;
      this.theBiomeDecorator.bigMushroomsPerChunk = 1;
      this.topBlock = Blocks.mycelium.getDefaultState();
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableCreatureList.add(new nA(EntityMooshroom.class, 8, 4, 8));
   }
}
