package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {
   public final int xCoord;
   public final int yCoord;
   public final int zCoord;
   private final int hash;
   int index = -1;
   float totalPathDistance;
   float distanceToNext;
   float distanceToTarget;
   PathPoint previous;
   public boolean visited;

   public PathPoint(int var1, int var2, int var3) {
      this.xCoord = var1;
      this.yCoord = var2;
      this.zCoord = var3;
      this.hash = makeHash(var1, var2, var3);
   }

   public static int makeHash(int var0, int var1, int var2) {
      return var1 & 255 | (var0 & 32767) << 8 | (var2 & 32767) << 24 | Integer.MIN_VALUE | '耀';
   }

   public float distanceTo(PathPoint var1) {
      float var2 = (float)(var1.xCoord - this.xCoord);
      float var3 = (float)(var1.yCoord - this.yCoord);
      float var4 = (float)(var1.zCoord - this.zCoord);
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSquared(PathPoint var1) {
      float var2 = (float)(var1.xCoord - this.xCoord);
      float var3 = (float)(var1.yCoord - this.yCoord);
      float var4 = (float)(var1.zCoord - this.zCoord);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public boolean equals(Object var1) {
      if(!(var1 instanceof PathPoint)) {
         return false;
      } else {
         PathPoint var2 = (PathPoint)var1;
         return this.hash == var2.hash && this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }

   public String toString() {
      return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
   }
}
