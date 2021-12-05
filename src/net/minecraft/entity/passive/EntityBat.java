package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
   private BlockPos spawnPosition;

   public EntityBat(World var1) {
      super(var1);
      this.setSize(0.5F, 0.9F);
      this.setIsBatHanging(true);
   }

   protected void entityInit() {
      super.entityInit();
      this.I.b(16, Byte.valueOf((byte)0));
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   protected String getLivingSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0?null:"mob.bat.idle";
   }

   protected String getHurtSound() {
      return "mob.bat.hurt";
   }

   protected String getDeathSound() {
      return "mob.bat.death";
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity var1) {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
   }

   public boolean getIsBatHanging() {
      return (this.I.g(16) & 1) != 0;
   }

   public void setIsBatHanging(boolean var1) {
      byte var2 = this.I.g(16);
      this.I.a(16, Byte.valueOf((byte)(var2 | 1)));
   }

   public void onUpdate() {
      super.onUpdate();
      if(this.getIsBatHanging()) {
         this.motionX = this.motionY = this.motionZ = 0.0D;
         this.posY = (double)MathHelper.floor_double(this.posY) + 1.0D - (double)this.height;
      } else {
         this.motionY *= 0.6000000238418579D;
      }

   }

   protected void updateAITasks() {
      super.updateAITasks();
      BlockPos var1 = new BlockPos(this);
      BlockPos var2 = var1.up();
      if(this.getIsBatHanging()) {
         if(!this.worldObj.getBlockState(var2).getBlock().isNormalCube()) {
            this.setIsBatHanging(false);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, var1, 0);
         } else {
            if(this.rand.nextInt(200) == 0) {
               this.rotationYawHead = (float)this.rand.nextInt(360);
            }

            if(this.worldObj.getClosestPlayerToEntity(this, 4.0D) != null) {
               this.setIsBatHanging(false);
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, var1, 0);
            }
         }
      } else {
         if(this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if(this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.distanceSq((double)((int)this.posX), (double)((int)this.posY), (double)((int)this.posZ)) < 4.0D) {
            this.spawnPosition = new BlockPos((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
         }

         double var3 = (double)this.spawnPosition.getX() + 0.5D - this.posX;
         double var5 = (double)this.spawnPosition.getY() + 0.1D - this.posY;
         double var7 = (double)this.spawnPosition.getZ() + 0.5D - this.posZ;
         this.motionX += (Math.signum(var3) * 0.5D - this.motionX) * 0.10000000149011612D;
         this.motionY += (Math.signum(var5) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
         this.motionZ += (Math.signum(var7) * 0.5D - this.motionZ) * 0.10000000149011612D;
         float var9 = (float)(MathHelper.func_181159_b(this.motionZ, this.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
         float var10 = MathHelper.wrapAngleTo180_float(var9 - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += var10;
         if(this.rand.nextInt(100) == 0 && this.worldObj.getBlockState(var2).getBlock().isNormalCube()) {
            this.setIsBatHanging(true);
         }
      }

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public void fall(float var1, float var2) {
   }

   protected void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if(this.isEntityInvulnerable(var1)) {
         return false;
      } else {
         if(!this.worldObj.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
         }

         return super.attackEntityFrom(var1, var2);
      }
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.I.a(16, Byte.valueOf(var1.getByte("BatFlags")));
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setByte("BatFlags", this.I.g(16));
   }

   public boolean getCanSpawnHere() {
      BlockPos var1 = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if(var1.getY() >= this.worldObj.func_181545_F()) {
         return false;
      } else {
         int var2 = this.worldObj.getLightFromNeighbors(var1);
         byte var3 = 4;
         if(this.isDateAroundHalloween(this.worldObj.getCurrentDate())) {
            var3 = 7;
         } else if(this.rand.nextBoolean()) {
            return false;
         }

         return var2 <= this.rand.nextInt(var3) && super.getCanSpawnHere();
      }
   }

   private boolean isDateAroundHalloween(Calendar var1) {
      return var1.get(2) + 1 == 10 && var1.get(5) >= 20 || var1.get(2) + 1 == 11 && var1.get(5) <= 3;
   }

   public float getEyeHeight() {
      return this.height / 2.0F;
   }
}
