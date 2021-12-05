package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDropParticleFX extends EntityFX {
   private final Material materialType;
   private int bobTimer;

   protected EntityDropParticleFX(World var1, double var2, double var4, double var6, Material var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.motionX = this.motionY = this.motionZ = 0.0D;
      if(var8 == Material.water) {
         this.particleRed = 0.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 0.0F;
      }

      this.setParticleTextureIndex(113);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.materialType = var8;
      this.bobTimer = 40;
      this.particleMaxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      this.motionX = this.motionY = this.motionZ = 0.0D;
   }

   public int getBrightnessForRender(float var1) {
      return this.materialType == Material.water?super.getBrightnessForRender(var1):257;
   }

   public float getBrightness(float var1) {
      return this.materialType == Material.water?super.getBrightness(var1):1.0F;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if(this.materialType == Material.water) {
         this.particleRed = 0.2F;
         this.particleGreen = 0.3F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.bobTimer + 16);
         this.particleBlue = 4.0F / (float)(40 - this.bobTimer + 8);
      }

      this.motionY -= (double)this.particleGravity;
      if(this.bobTimer-- > 0) {
         this.motionX *= 0.02D;
         this.motionY *= 0.02D;
         this.motionZ *= 0.02D;
         this.setParticleTextureIndex(113);
      } else {
         this.setParticleTextureIndex(112);
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if(this.particleMaxAge-- <= 0) {
         this.setDead();
      }

      if(this.onGround) {
         if(this.materialType == Material.water) {
            this.setDead();
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         } else {
            this.setParticleTextureIndex(114);
         }

         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

      BlockPos var1 = new BlockPos(this);
      IBlockState var2 = this.worldObj.getBlockState(var1);
      Material var3 = var2.getBlock().getMaterial();
      if(var3.isLiquid() || var3.isSolid()) {
         double var4 = 0.0D;
         if(var2.getBlock() instanceof BlockLiquid) {
            var4 = (double)BlockLiquid.getLiquidHeightPercent(((Integer)var2.getValue(BlockLiquid.P)).intValue());
         }

         double var6 = (double)(MathHelper.floor_double(this.posY) + 1) - var4;
         if(this.posY < var6) {
            this.setDead();
         }
      }

   }
}
