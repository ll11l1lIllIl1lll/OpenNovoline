package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GenLayer field_151628_d;

   public GenLayerHills(long var1, GenLayer var3, GenLayer var4) {
      super(var1);
      this.parent = var3;
      this.field_151628_d = var4;
   }

   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var5 = this.parent.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var6 = this.field_151628_d.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var7 = IntCache.getIntCache(var3 * var4);

      for(int var8 = 0; var8 < var4; ++var8) {
         for(int var9 = 0; var9 < var3; ++var9) {
            this.initChunkSeed((long)(var9 + var1), (long)(var8 + var2));
            int var10 = var5[var9 + 1 + (var8 + 1) * (var3 + 2)];
            int var11 = var6[var9 + 1 + (var8 + 1) * (var3 + 2)];
            boolean var12 = (var11 - 2) % 29 == 0;
            if(var10 > 255) {
               LOGGER.debug("old! " + var10);
            }

            if(var11 >= 2 && (var11 - 2) % 29 == 1 && var10 < 128) {
               if(BiomeGenBase.getBiome(var10 + 128) != null) {
                  var7[var9 + var8 * var3] = var10 + 128;
               } else {
                  var7[var9 + var8 * var3] = var10;
               }
            } else if(this.nextInt(3) != 0) {
               var7[var9 + var8 * var3] = var10;
            } else {
               int var13 = var10;
               if(var10 == BiomeGenBase.desert.biomeID) {
                  var13 = BiomeGenBase.desertHills.biomeID;
               } else if(var10 == BiomeGenBase.forest.biomeID) {
                  var13 = BiomeGenBase.forestHills.biomeID;
               } else if(var10 == BiomeGenBase.birchForest.biomeID) {
                  var13 = BiomeGenBase.birchForestHills.biomeID;
               } else if(var10 == BiomeGenBase.roofedForest.biomeID) {
                  var13 = BiomeGenBase.plains.biomeID;
               } else if(var10 == BiomeGenBase.taiga.biomeID) {
                  var13 = BiomeGenBase.taigaHills.biomeID;
               } else if(var10 == BiomeGenBase.megaTaiga.biomeID) {
                  var13 = BiomeGenBase.megaTaigaHills.biomeID;
               } else if(var10 == BiomeGenBase.coldTaiga.biomeID) {
                  var13 = BiomeGenBase.coldTaigaHills.biomeID;
               } else if(var10 == BiomeGenBase.plains.biomeID) {
                  if(this.nextInt(3) == 0) {
                     var13 = BiomeGenBase.forestHills.biomeID;
                  } else {
                     var13 = BiomeGenBase.forest.biomeID;
                  }
               } else if(var10 == BiomeGenBase.icePlains.biomeID) {
                  var13 = BiomeGenBase.iceMountains.biomeID;
               } else if(var10 == BiomeGenBase.jungle.biomeID) {
                  var13 = BiomeGenBase.jungleHills.biomeID;
               } else if(var10 == BiomeGenBase.ocean.biomeID) {
                  var13 = BiomeGenBase.deepOcean.biomeID;
               } else if(var10 == BiomeGenBase.extremeHills.biomeID) {
                  var13 = BiomeGenBase.extremeHillsPlus.biomeID;
               } else if(var10 == BiomeGenBase.savanna.biomeID) {
                  var13 = BiomeGenBase.savannaPlateau.biomeID;
               } else if(biomesEqualOrMesaPlateau(var10, BiomeGenBase.mesaPlateau_F.biomeID)) {
                  var13 = BiomeGenBase.mesa.biomeID;
               } else if(var10 == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                  int var14 = this.nextInt(2);
                  var13 = BiomeGenBase.plains.biomeID;
               }

               if(var13 != var10) {
                  if(BiomeGenBase.getBiome(var13 + 128) != null) {
                     var13 += 128;
                  } else {
                     var13 = var10;
                  }
               }

               if(var13 == var10) {
                  var7[var9 + var8 * var3] = var10;
               } else {
                  int var19 = var5[var9 + 1 + (var8 + 1 - 1) * (var3 + 2)];
                  int var15 = var5[var9 + 1 + 1 + (var8 + 1) * (var3 + 2)];
                  int var16 = var5[var9 + 1 - 1 + (var8 + 1) * (var3 + 2)];
                  int var17 = var5[var9 + 1 + (var8 + 1 + 1) * (var3 + 2)];
                  int var18 = 0;
                  if(biomesEqualOrMesaPlateau(var19, var10)) {
                     ++var18;
                  }

                  if(biomesEqualOrMesaPlateau(var15, var10)) {
                     ++var18;
                  }

                  if(biomesEqualOrMesaPlateau(var16, var10)) {
                     ++var18;
                  }

                  if(biomesEqualOrMesaPlateau(var17, var10)) {
                     ++var18;
                  }

                  if(var18 >= 3) {
                     var7[var9 + var8 * var3] = var13;
                  } else {
                     var7[var9 + var8 * var3] = var10;
                  }
               }
            }
         }
      }

      return var7;
   }
}
