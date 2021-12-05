package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable extends Entity implements IProjectile {
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private Block inTile;
   protected boolean inGround;
   public int throwableShake;
   private EntityLivingBase thrower;
   private String throwerName;
   private int ticksInGround;
   private int ticksInAir;

   public EntityThrowable(World var1) {
      super(var1);
      this.setSize(0.25F, 0.25F);
   }

   protected void entityInit() {
   }

   public boolean isInRangeToRenderDist(double var1) {
      double var3 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
      if(Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 = var3 * 64.0D;
      return var1 < var3 * var3;
   }

   public EntityThrowable(World var1, EntityLivingBase var2) {
      super(var1);
      this.thrower = var2;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, var2.rotationYaw, var2.rotationPitch);
      this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F);
      this.posY -= 0.10000000149011612D;
      this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F);
      this.setPosition(this.posX, this.posY, this.posZ);
      float var3 = 0.4F;
      this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var3);
      this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var3);
      this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.getInaccuracy()) / 180.0F * 3.1415927F) * var3);
      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.getVelocity(), 1.0F);
   }

   public EntityThrowable(World var1, double var2, double var4, double var6) {
      super(var1);
      this.ticksInGround = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(var2, var4, var6);
   }

   protected float getVelocity() {
      return 1.5F;
   }

   protected float getInaccuracy() {
      return 0.0F;
   }

   public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {
      float var9 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);
      var1 = var1 / (double)var9;
      var3 = var3 / (double)var9;
      var5 = var5 / (double)var9;
      var1 = var1 + this.rand.nextGaussian() * 0.007499999832361937D * (double)var8;
      var3 = var3 + this.rand.nextGaussian() * 0.007499999832361937D * (double)var8;
      var5 = var5 + this.rand.nextGaussian() * 0.007499999832361937D * (double)var8;
      var1 = var1 * (double)var7;
      var3 = var3 * (double)var7;
      var5 = var5 * (double)var7;
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
      float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.func_181159_b(var1, var5) * 180.0D / 3.141592653589793D);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.func_181159_b(var3, (double)var10) * 180.0D / 3.141592653589793D);
      this.ticksInGround = 0;
   }

   public void setVelocity(double var1, double var3, double var5) {
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
      if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float var7 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.func_181159_b(var1, var5) * 180.0D / 3.141592653589793D);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.func_181159_b(var3, (double)var7) * 180.0D / 3.141592653589793D);
      }

   }

   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.onUpdate();
      if(this.throwableShake > 0) {
         --this.throwableShake;
      }

      if(this.inGround) {
         if(this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
            ++this.ticksInGround;
            if(this.ticksInGround == 1200) {
               this.setDead();
            }

            return;
         }

         this.inGround = false;
         this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
         this.ticksInGround = 0;
         this.ticksInAir = 0;
      } else {
         ++this.ticksInAir;
      }

      Vec3 var1 = new Vec3(this.posX, this.posY, this.posZ);
      Vec3 var2 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      MovingObjectPosition var3 = this.worldObj.rayTraceBlocks(var1, var2);
      var1 = new Vec3(this.posX, this.posY, this.posZ);
      new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      var2 = new Vec3(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
      if(!this.worldObj.isRemote) {
         Entity var4 = null;
         List var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
         double var6 = 0.0D;
         EntityLivingBase var8 = this.getThrower();

         for(Entity var10 : var5) {
            if(var10.canBeCollidedWith() && (var10 != var8 || this.ticksInAir >= 5)) {
               float var12 = 0.3F;
               AxisAlignedBB var13 = var10.getEntityBoundingBox().expand((double)var12, (double)var12, (double)var12);
               MovingObjectPosition var14 = var13.calculateIntercept(var1, var2);
               double var15 = var1.squareDistanceTo(var14.hitVec);
               if(var15 < var6 || var6 == 0.0D) {
                  var4 = var10;
                  var6 = var15;
               }
            }
         }

         var3 = new MovingObjectPosition(var4);
      }

      if(var3.typeOfHit == MovingObjectPosition$MovingObjectType.BLOCK && this.worldObj.getBlockState(var3.getBlockPos()).getBlock() == Blocks.portal) {
         this.func_181015_d(var3.getBlockPos());
      } else {
         this.onImpact(var3);
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float var19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.func_181159_b(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D);

      for(this.rotationPitch = (float)(MathHelper.func_181159_b(this.motionY, (double)var19) * 180.0D / 3.141592653589793D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      float var20 = 0.99F;
      float var21 = this.getGravityVelocity();
      if(this.isInWater()) {
         for(int var7 = 0; var7 < 4; ++var7) {
            float var22 = 0.25F;
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)var22, this.posY - this.motionY * (double)var22, this.posZ - this.motionZ * (double)var22, this.motionX, this.motionY, this.motionZ, new int[0]);
         }

         var20 = 0.8F;
      }

      this.motionX *= (double)var20;
      this.motionY *= (double)var20;
      this.motionZ *= (double)var20;
      this.motionY -= (double)var21;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   protected float getGravityVelocity() {
      return 0.03F;
   }

   protected abstract void onImpact(MovingObjectPosition var1);

   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("xTile", (short)this.xTile);
      var1.setShort("yTile", (short)this.yTile);
      var1.setShort("zTile", (short)this.zTile);
      ResourceLocation var2 = (ResourceLocation)Block.blockRegistry.getNameForObject(this.inTile);
      var1.setString("inTile", "");
      var1.setByte("shake", (byte)this.throwableShake);
      var1.setByte("inGround", (byte)(this.inGround?1:0));
      if((this.throwerName == null || this.throwerName.isEmpty()) && this.thrower instanceof EntityPlayer) {
         this.throwerName = this.thrower.getName();
      }

      var1.setString("ownerName", this.throwerName == null?"":this.throwerName);
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      this.xTile = var1.getShort("xTile");
      this.yTile = var1.getShort("yTile");
      this.zTile = var1.getShort("zTile");
      if(var1.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(var1.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(var1.getByte("inTile") & 255);
      }

      this.throwableShake = var1.getByte("shake") & 255;
      this.inGround = var1.getByte("inGround") == 1;
      this.thrower = null;
      this.throwerName = var1.getString("ownerName");
      if(this.throwerName != null && this.throwerName.isEmpty()) {
         this.throwerName = null;
      }

      this.thrower = this.getThrower();
   }

   public EntityLivingBase getThrower() {
      if(this.thrower == null && this.throwerName != null && !this.throwerName.isEmpty()) {
         this.thrower = this.worldObj.getPlayerEntityByName(this.throwerName);
         if(this.thrower == null && this.worldObj instanceof WorldServer) {
            EntityThrowable var10000 = this;

            try {
               Entity var1 = ((WorldServer)var10000.worldObj).getEntityFromUuid(UUID.fromString(this.throwerName));
               if(var1 instanceof EntityLivingBase) {
                  this.thrower = (EntityLivingBase)var1;
               }
            } catch (Throwable var2) {
               this.thrower = null;
            }
         }
      }

      return this.thrower;
   }

   private static Throwable a(Throwable var0) {
      return var0;
   }
}
