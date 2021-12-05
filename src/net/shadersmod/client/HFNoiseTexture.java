package net.shadersmod.client;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.shadersmod.client.ShaderOption;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture {
   public int texID = GL11.glGenTextures();
   public int textureUnit = 15;

   public HFNoiseTexture(int var1, int var2) {
      byte[] var3 = this.genHFNoiseImage(var1, var2);
      ByteBuffer var4 = BufferUtils.createByteBuffer(var3.length);
      var4.put(var3);
      var4.flip();
      GlStateManager.bindTexture(this.texID);
      GL11.glTexImage2D(3553, 0, 6407, var1, var2, 0, 6407, 5121, var4);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GlStateManager.bindTexture(0);
   }

   public int getID() {
      return this.texID;
   }

   public void destroy() {
      GlStateManager.deleteTexture(this.texID);
      this.texID = 0;
   }

   private int random(int var1) {
      var1 = var1 ^ var1 << 13;
      var1 = var1 ^ var1 >> 17;
      var1 = var1 ^ var1 << 5;
      return var1;
   }

   private byte random(int var1, int var2, int var3) {
      int var4 = (this.random(var1) + this.random(var2 * 19)) * this.random(var3 * 23) - var3;
      return (byte)(this.random(var4) % 128);
   }

   private byte[] genHFNoiseImage(int var1, int var2) {
      byte[] var4 = new byte[var1 * var2 * 3];
      ShaderOption.p();
      int var5 = 0;
      int var6 = 0;
      if(var6 < var2) {
         int var7 = 0;
         if(var7 < var1) {
            int var8 = 1;
            if(var8 < 4) {
               var4[var5++] = this.random(var7, var6, var8);
               ++var8;
            }

            ++var7;
         }

         ++var6;
      }

      return var4;
   }
}
