package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving {
   private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube(RenderManager var1) {
      super(var1, new ModelMagmaCube(), 0.25F);
   }

   protected ResourceLocation getEntityTexture(EntityMagmaCube var1) {
      return magmaCubeTextures;
   }

   protected void preRenderCallback(EntityMagmaCube var1, float var2) {
      int var3 = var1.getSlimeSize();
      float var4 = (var1.prevSquishFactor + (var1.squishFactor - var1.prevSquishFactor) * var2) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      float var6 = (float)var3;
      GlStateManager.scale(var5 * var6, 1.0F / var5 * var6, var5 * var6);
   }
}
