package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer {
   private final RenderSlime slimeRenderer;
   private final ModelBase slimeModel = new ModelSlime(0);

   public LayerSlimeGel(RenderSlime var1) {
      this.slimeRenderer = var1;
   }

   public void doRenderLayer(EntitySlime var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!var1.isInvisible()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.o();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
         this.slimeModel.render(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.disableBlend();
         GlStateManager.v();
      }

   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
