package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   public EntityFlying(World var1) {
      super(var1);
   }

   public void fall(float var1, float var2) {
   }

   protected void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
   }

   public void moveEntityWithHeading(float var1, float var2) {
      if(this.isInWater()) {
         this.moveFlying(var1, var2, 0.02F);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.800000011920929D;
         this.motionY *= 0.800000011920929D;
         this.motionZ *= 0.800000011920929D;
      } else if(this.isInLava()) {
         this.moveFlying(var1, var2, 0.02F);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.5D;
         this.motionY *= 0.5D;
         this.motionZ *= 0.5D;
      } else {
         float var3 = 0.91F;
         if(this.onGround) {
            var3 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
         }

         float var4 = 0.16277136F / (var3 * var3 * var3);
         this.moveFlying(var1, var2, this.onGround?0.1F * var4:0.02F);
         var3 = 0.91F;
         if(this.onGround) {
            var3 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)var3;
         this.motionY *= (double)var3;
         this.motionZ *= (double)var3;
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var9 = this.posX - this.prevPosX;
      double var5 = this.posZ - this.prevPosZ;
      float var7 = MathHelper.sqrt_double(var9 * var9 + var5 * var5) * 4.0F;
      if(var7 > 1.0F) {
         var7 = 1.0F;
      }

      this.limbSwingAmount += (var7 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public boolean isOnLadder() {
      return false;
   }
}
