package net.minecraft.entity.monster;

import net.aP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower$EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
   private int homeCheckTimer;
   Village villageObj;
   private int attackTimer;
   private int holdRoseTick;

   public EntityIronGolem(World var1) {
      super(var1);
      this.setSize(1.4F, 2.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
      this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
      this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(5, new EntityAILookAtVillager(this));
      this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(3, new aP(this, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
   }

   protected void entityInit() {
      super.entityInit();
      this.I.b(16, Byte.valueOf((byte)0));
   }

   protected void updateAITasks() {
      if(--this.homeCheckTimer <= 0) {
         this.homeCheckTimer = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
         if(this.villageObj == null) {
            this.detachHome();
         } else {
            BlockPos var1 = this.villageObj.getCenter();
            this.setHomePosAndDistance(var1, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
         }
      }

      super.updateAITasks();
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
   }

   protected int decreaseAirSupply(int var1) {
      return var1;
   }

   protected void collideWithEntity(Entity var1) {
      if(var1 instanceof IMob && !(var1 instanceof EntityCreeper) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((EntityLivingBase)var1);
      }

      super.collideWithEntity(var1);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if(this.attackTimer > 0) {
         --this.attackTimer;
      }

      if(this.holdRoseTick > 0) {
         --this.holdRoseTick;
      }

      if(this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D && this.rand.nextInt(5) == 0) {
         int var1 = MathHelper.floor_double(this.posX);
         int var2 = MathHelper.floor_double(this.posY - 0.20000000298023224D);
         int var3 = MathHelper.floor_double(this.posZ);
         IBlockState var4 = this.worldObj.getBlockState(new BlockPos(var1, var2, var3));
         Block var5 = var4.getBlock();
         if(var5.getMaterial() != Material.air) {
            this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D, new int[]{Block.getStateId(var4)});
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setPlayerCreated(var1.getBoolean("PlayerCreated"));
   }

   public boolean attackEntityAsMob(Entity var1) {
      this.attackTimer = 10;
      this.worldObj.setEntityState(this, (byte)4);
      boolean var2 = var1.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(7 + this.rand.nextInt(15)));
      var1.motionY += 0.4000000059604645D;
      this.applyEnchantments(this, var1);
      this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      return var2;
   }

   public void handleStatusUpdate(byte var1) {
      if(var1 == 4) {
         this.attackTimer = 10;
         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      } else if(var1 == 11) {
         this.holdRoseTick = 400;
      } else {
         super.handleStatusUpdate(var1);
      }

   }

   public Village getVillage() {
      return this.villageObj;
   }

   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean var1) {
      this.holdRoseTick = 400;
      this.worldObj.setEntityState(this, (byte)11);
   }

   protected String getHurtSound() {
      return "mob.irongolem.hit";
   }

   protected String getDeathSound() {
      return "mob.irongolem.death";
   }

   protected void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   protected void dropFewItems(boolean var1, int var2) {
      int var3 = this.rand.nextInt(3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.red_flower), 1, (float)BlockFlower$EnumFlowerType.POPPY.getMeta());
      }

      int var6 = 3 + this.rand.nextInt(3);

      for(int var5 = 0; var5 < var6; ++var5) {
         this.dropItem(Items.iron_ingot, 1);
      }

   }

   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.I.g(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean var1) {
      byte var2 = this.I.g(16);
      this.I.a(16, Byte.valueOf((byte)(var2 | 1)));
   }

   public void onDeath(DamageSource var1) {
      if(!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
         this.villageObj.setReputationForPlayer(this.attackingPlayer.getName(), -5);
      }

      super.onDeath(var1);
   }
}
