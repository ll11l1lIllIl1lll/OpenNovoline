package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGhast extends ModelBase {
   ModelRenderer body;
   ModelRenderer[] tentacles = new ModelRenderer[9];

   public ModelGhast() {
      boolean var1 = true;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      this.body.rotationPointY += 8.0F;
      Random var2 = new Random(1660L);

      for(int var3 = 0; var3 < this.tentacles.length; ++var3) {
         this.tentacles[var3] = new ModelRenderer(this, 0, 0);
         float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var6 = var2.nextInt(7) + 8;
         this.tentacles[var3].addBox(-1.0F, 0.0F, -1.0F, 2, var6, 2);
         this.tentacles[var3].rotationPointX = var4;
         this.tentacles[var3].rotationPointZ = var5;
         this.tentacles[var3].rotationPointY = 15.0F;
      }

   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      for(int var8 = 0; var8 < this.tentacles.length; ++var8) {
         this.tentacles[var8].rotateAngleX = 0.2F * MathHelper.sin(var3 * 0.3F + (float)var8) + 0.4F;
      }

   }

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.6F, 0.0F);
      this.body.render(var7);

      for(ModelRenderer var11 : this.tentacles) {
         var11.render(var7);
      }

      GlStateManager.popMatrix();
   }
}
