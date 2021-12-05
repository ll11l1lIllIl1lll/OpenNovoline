package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast extends RenderLiving {
   private static final ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public RenderGhast(RenderManager var1) {
      super(var1, new ModelGhast(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityGhast var1) {
      return var1.isAttacking()?ghastShootingTextures:ghastTextures;
   }

   protected void preRenderCallback(EntityGhast var1, float var2) {
      float var3 = 1.0F;
      float var4 = (8.0F + var3) / 2.0F;
      float var5 = (8.0F + 1.0F / var3) / 2.0F;
      GlStateManager.scale(var5, var4, var5);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
