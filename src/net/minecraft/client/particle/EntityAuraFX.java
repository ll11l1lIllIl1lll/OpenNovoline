package net.minecraft.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   protected EntityAuraFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      float var14 = this.rand.nextFloat() * 0.1F + 0.2F;
      this.particleRed = var14;
      this.particleGreen = var14;
      this.particleBlue = var14;
      this.setParticleTextureIndex(0);
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
      this.motionX *= 0.019999999552965164D;
      this.motionY *= 0.019999999552965164D;
      this.motionZ *= 0.019999999552965164D;
      this.particleMaxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
      this.noClip = true;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.99D;
      this.motionY *= 0.99D;
      this.motionZ *= 0.99D;
      if(this.particleMaxAge-- <= 0) {
         this.setDead();
      }

   }
}
