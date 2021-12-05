package optifine;

import optifine.MatchBlock;

public class CacheLocalByte {
   private int maxX = 18;
   private int maxY = 128;
   private int maxZ = 18;
   private int offsetX = 0;
   private int offsetY = 0;
   private int offsetZ = 0;
   private byte[][][] cache = (byte[][][])((byte[][][])null);
   private byte[] lastZs = null;
   private int lastDz = 0;

   public CacheLocalByte(int var1, int var2, int var3) {
      this.maxX = var1;
      this.maxY = var2;
      this.maxZ = var3;
      this.cache = new byte[var1][var2][var3];
      this.resetCache();
   }

   public void resetCache() {
      MatchBlock.b();
      int var2 = 0;
      if(var2 < this.maxX) {
         byte[][] var3 = this.cache[var2];
         int var4 = 0;
         if(var4 < this.maxY) {
            byte[] var5 = var3[var4];
            int var6 = 0;
            if(var6 < this.maxZ) {
               var5[var6] = -1;
               ++var6;
            }

            ++var4;
         }

         ++var2;
      }

   }

   public void setOffset(int var1, int var2, int var3) {
      this.offsetX = var1;
      this.offsetY = var2;
      this.offsetZ = var3;
      this.resetCache();
   }

   public byte get(int var1, int var2, int var3) {
      try {
         this.lastZs = this.cache[var1 - this.offsetX][var2 - this.offsetY];
         this.lastDz = var3 - this.offsetZ;
         return this.lastZs[this.lastDz];
      } catch (ArrayIndexOutOfBoundsException var5) {
         var5.printStackTrace();
         return (byte)-1;
      }
   }

   public void setLast(byte var1) {
      try {
         this.lastZs[this.lastDz] = var1;
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private static ArrayIndexOutOfBoundsException a(ArrayIndexOutOfBoundsException var0) {
      return var0;
   }
}
