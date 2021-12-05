package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob {
   private int lastActiveTime;
   private int timeSinceIgnited;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int field_175494_bm = 0;

   public EntityCreeper(World var1) {
      super(var1);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAICreeperSwell(this));
      this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, false));
      this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
   }

   public int getMaxFallHeight() {
      return this.getAttackTarget() == null?3:3 + (int)(this.getHealth() - 1.0F);
   }

   public void fall(float var1, float var2) {
      super.fall(var1, var2);
      this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + var1 * 1.5F);
      if(this.timeSinceIgnited > this.fuseTime - 5) {
         this.timeSinceIgnited = this.fuseTime - 5;
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.I.b(16, Byte.valueOf((byte)-1));
      this.I.b(17, Byte.valueOf((byte)0));
      this.I.b(18, Byte.valueOf((byte)0));
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      if(this.I.g(17) == 1) {
         var1.setBoolean("powered", true);
      }

      var1.setShort("Fuse", (short)this.fuseTime);
      var1.setByte("ExplosionRadius", (byte)this.explosionRadius);
      var1.setBoolean("ignited", this.hasIgnited());
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.I.a(17, Byte.valueOf((byte)(var1.getBoolean("powered")?1:0)));
      if(var1.hasKey("Fuse", 99)) {
         this.fuseTime = var1.getShort("Fuse");
      }

      if(var1.hasKey("ExplosionRadius", 99)) {
         this.explosionRadius = var1.getByte("ExplosionRadius");
      }

      if(var1.getBoolean("ignited")) {
         this.ignite();
      }

   }

   public void onUpdate() {
      if(this.isEntityAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         if(this.hasIgnited()) {
            this.setCreeperState(1);
         }

         int var1 = this.getCreeperState();
         if(this.timeSinceIgnited == 0) {
            this.playSound("creeper.primed", 1.0F, 0.5F);
         }

         this.timeSinceIgnited += var1;
         if(this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
         }

         if(this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.explode();
         }
      }

      super.onUpdate();
   }

   protected String getHurtSound() {
      return "mob.creeper.say";
   }

   protected String getDeathSound() {
      return "mob.creeper.death";
   }

   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
      if(var1.getEntity() instanceof EntitySkeleton) {
         int var2 = Item.b(Items.record_13);
         int var3 = Item.b(Items.record_wait);
         int var4 = var2 + this.rand.nextInt(var3 - var2 + 1);
         this.dropItem(Item.getItemById(var4), 1);
      } else if(var1.getEntity() instanceof EntityCreeper && var1.getEntity() != this && ((EntityCreeper)var1.getEntity()).getPowered() && ((EntityCreeper)var1.getEntity()).isAIEnabled()) {
         ((EntityCreeper)var1.getEntity()).func_175493_co();
         this.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
      }

   }

   public boolean attackEntityAsMob(Entity var1) {
      return true;
   }

   public boolean getPowered() {
      return this.I.g(17) == 1;
   }

   public float getCreeperFlashIntensity(float var1) {
      return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * var1) / (float)(this.fuseTime - 2);
   }

   protected Item getDropItem() {
      return Items.gunpowder;
   }

   public int getCreeperState() {
      return this.I.g(16);
   }

   public void setCreeperState(int var1) {
      this.I.a(16, Byte.valueOf((byte)var1));
   }

   public void onStruckByLightning(EntityLightningBolt var1) {
      super.onStruckByLightning(var1);
      this.I.a(17, Byte.valueOf((byte)1));
   }

   protected boolean interact(EntityPlayer var1) {
      ItemStack var2 = var1.inventory.getCurrentItem();
      if(var2.getItem() == Items.flint_and_steel) {
         this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.ignite", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
         var1.swingItem();
         if(!this.worldObj.isRemote) {
            this.ignite();
            var2.damageItem(1, var1);
            return true;
         }
      }

      return super.interact(var1);
   }

   private void explode() {
      if(!this.worldObj.isRemote) {
         boolean var1 = this.worldObj.getGameRules().getBoolean("mobGriefing");
         float var2 = this.getPowered()?2.0F:1.0F;
         this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius * var2, var1);
         this.setDead();
      }

   }

   public boolean hasIgnited() {
      return this.I.g(18) != 0;
   }

   public void ignite() {
      this.I.a(18, Byte.valueOf((byte)1));
   }

   public boolean isAIEnabled() {
      return this.field_175494_bm < 1 && this.worldObj.getGameRules().getBoolean("doMobLoot");
   }

   public void func_175493_co() {
      ++this.field_175494_bm;
   }
}
