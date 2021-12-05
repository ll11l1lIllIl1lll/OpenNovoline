package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving {
   private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish(RenderManager var1) {
      super(var1, new ModelSilverfish(), 0.3F);
   }

   protected float getDeathMaxRotation(EntitySilverfish var1) {
      return 180.0F;
   }

   protected ResourceLocation getEntityTexture(EntitySilverfish var1) {
      return silverfishTextures;
   }
}
