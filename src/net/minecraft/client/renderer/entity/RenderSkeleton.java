package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton$1;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderSkeleton extends RenderBiped {
   private static final ResourceLocation skeletonTextures = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation witherSkeletonTextures = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderSkeleton(RenderManager var1) {
      super(var1, new ModelSkeleton(), 0.5F);
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new RenderSkeleton$1(this, this));
   }

   protected void preRenderCallback(EntitySkeleton var1, float var2) {
      if(var1.getSkeletonType() == 1) {
         GlStateManager.scale(1.2F, 1.2F, 1.2F);
      }

   }

   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
   }

   protected ResourceLocation getEntityTexture(EntitySkeleton var1) {
      return var1.getSkeletonType() == 1?witherSkeletonTextures:skeletonTextures;
   }
}
