package net.optifine;

import java.io.FileNotFoundException;
import net.acE;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;
import net.optifine.Blender;
import net.optifine.Config;
import net.optifine.CustomSkyLayer;
import net.optifine.MatchBlock;

public class CustomSky {
   private static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][])((CustomSkyLayer[][])null);

   public static void reset() {
      worldSkyLayers = (CustomSkyLayer[][])((CustomSkyLayer[][])null);
   }

   public static void update() {
      reset();
      if(Config.isCustomSky()) {
         worldSkyLayers = readCustomSkies();
      }

   }

   private static CustomSkyLayer[][] readCustomSkies() {
      // $FF: Couldn't be decompiled
   }

   public static void renderSky(World var0, TextureManager var1, float var2, float var3) {
      acE[] var4 = MatchBlock.b();
      if(worldSkyLayers != null && Config.getGameSettings().renderDistanceChunks >= 8) {
         int var5 = var0.provider.getDimensionId();
         if(var5 >= 0 && var5 < worldSkyLayers.length) {
            CustomSkyLayer[] var6 = worldSkyLayers[var5];
            long var7 = var0.getWorldTime();
            int var9 = (int)(var7 % 24000L);
            int var10 = 0;
            if(var10 < var6.length) {
               CustomSkyLayer var11 = var6[var10];
               if(var11.isActive(var0, var9)) {
                  var11.render(var9, var2, var3);
               }

               ++var10;
            }

            Blender.clearBlend(var3);
         }
      }

   }

   public static boolean hasSkyLayers(World var0) {
      acE[] var1 = MatchBlock.b();
      if(worldSkyLayers == null) {
         return false;
      } else if(Config.getGameSettings().renderDistanceChunks < 8) {
         return false;
      } else {
         int var2 = var0.provider.getDimensionId();
         if(var2 >= 0 && var2 < worldSkyLayers.length) {
            CustomSkyLayer[] var3 = worldSkyLayers[var2];
            return var3 == null?false:var3.length > 0;
         } else {
            return false;
         }
      }
   }

   private static FileNotFoundException a(FileNotFoundException var0) {
      return var0;
   }
}
