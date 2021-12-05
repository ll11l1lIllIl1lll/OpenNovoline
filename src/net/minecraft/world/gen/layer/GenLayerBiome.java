package net.minecraft.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.ChunkProviderSettings$Factory;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBiome extends GenLayer {
   private BiomeGenBase[] f = new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains};
   private BiomeGenBase[] i = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland};
   private BiomeGenBase[] h = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga, BiomeGenBase.plains};
   private BiomeGenBase[] field_151620_f = new BiomeGenBase[]{BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.coldTaiga};
   private final ChunkProviderSettings field_175973_g;

   public GenLayerBiome(long var1, GenLayer var3, WorldType var4, String var5) {
      super(var1);
      this.parent = var3;
      if(var4 == WorldType.DEFAULT_1_1) {
         this.f = new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga};
         this.field_175973_g = null;
      } else if(var4 == WorldType.CUSTOMIZED) {
         this.field_175973_g = ChunkProviderSettings$Factory.jsonToFactory(var5).func_177864_b();
      } else {
         this.field_175973_g = null;
      }

   }

   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var5 = this.parent.getInts(var1, var2, var3, var4);
      int[] var6 = IntCache.getIntCache(var3 * var4);

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.initChunkSeed((long)(var8 + var1), (long)(var7 + var2));
            int var9 = var5[var8 + var7 * var3];
            int var10 = (var9 & 3840) >> 8;
            var9 = var9 & -3841;
            if(this.field_175973_g != null && this.field_175973_g.fixedBiome >= 0) {
               var6[var8 + var7 * var3] = this.field_175973_g.fixedBiome;
            } else if(isBiomeOceanic(var9)) {
               var6[var8 + var7 * var3] = var9;
            } else if(var9 == BiomeGenBase.mushroomIsland.biomeID) {
               var6[var8 + var7 * var3] = var9;
            } else if(var9 == 1) {
               if(this.nextInt(3) == 0) {
                  var6[var8 + var7 * var3] = BiomeGenBase.mesaPlateau.biomeID;
               } else {
                  var6[var8 + var7 * var3] = BiomeGenBase.mesaPlateau_F.biomeID;
               }
            } else if(var9 == 2) {
               var6[var8 + var7 * var3] = BiomeGenBase.jungle.biomeID;
            } else if(var9 == 3) {
               var6[var8 + var7 * var3] = BiomeGenBase.megaTaiga.biomeID;
            } else if(var9 == 4) {
               var6[var8 + var7 * var3] = this.field_151620_f[this.nextInt(this.field_151620_f.length)].biomeID;
            } else {
               var6[var8 + var7 * var3] = BiomeGenBase.mushroomIsland.biomeID;
            }
         }
      }

      return var6;
   }
}
