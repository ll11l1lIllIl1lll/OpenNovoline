package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 staticVector = new Vec3(0.0D, 0.0D, 0.0D);

   public static Vec3 findRandomTarget(EntityCreature var0, int var1, int var2) {
      return findRandomTargetBlock(var0, var1, var2, (Vec3)null);
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature var0, int var1, int var2, Vec3 var3) {
      staticVector = var3.subtract(var0.posX, var0.posY, var0.posZ);
      return findRandomTargetBlock(var0, var1, var2, staticVector);
   }

   public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature var0, int var1, int var2, Vec3 var3) {
      staticVector = (new Vec3(var0.posX, var0.posY, var0.posZ)).subtract(var3);
      return findRandomTargetBlock(var0, var1, var2, staticVector);
   }

   private static Vec3 findRandomTargetBlock(EntityCreature var0, int var1, int var2, Vec3 var3) {
      Random var4 = var0.getRNG();
      boolean var5 = false;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      float var9 = -99999.0F;
      if(var0.hasHome()) {
         double var11 = var0.getHomePosition().distanceSq((double)MathHelper.floor_double(var0.posX), (double)MathHelper.floor_double(var0.posY), (double)MathHelper.floor_double(var0.posZ)) + 4.0D;
         double var13 = (double)(var0.getMaximumHomeDistance() + (float)var1);
         boolean var10 = var11 < var13 * var13;
      } else {
         boolean var18 = false;
      }

      for(int var19 = 0; var19 < 10; ++var19) {
         int var12 = var4.nextInt(2 * var1 + 1) - var1;
         int var21 = var4.nextInt(2 * var2 + 1) - var2;
         int var14 = var4.nextInt(2 * var1 + 1) - var1;
         if((double)var12 * var3.xCoord + (double)var14 * var3.zCoord >= 0.0D) {
            if(var0.hasHome() && var1 > 1) {
               BlockPos var15 = var0.getHomePosition();
               if(var0.posX > (double)var15.getX()) {
                  var12 -= var4.nextInt(var1 / 2);
               } else {
                  var12 += var4.nextInt(var1 / 2);
               }

               if(var0.posZ > (double)var15.getZ()) {
                  var14 -= var4.nextInt(var1 / 2);
               } else {
                  var14 += var4.nextInt(var1 / 2);
               }
            }

            var12 = var12 + MathHelper.floor_double(var0.posX);
            var21 = var21 + MathHelper.floor_double(var0.posY);
            var14 = var14 + MathHelper.floor_double(var0.posZ);
            BlockPos var24 = new BlockPos(var12, var21, var14);
            if(var0.isWithinHomeDistanceFromPosition(var24)) {
               float var16 = var0.getBlockPathWeight(var24);
               if(var16 > var9) {
                  var9 = var16;
                  var6 = var12;
                  var7 = var21;
                  var8 = var14;
                  var5 = true;
               }
            }
         }
      }

      return new Vec3((double)var6, (double)var7, (double)var8);
   }
}
