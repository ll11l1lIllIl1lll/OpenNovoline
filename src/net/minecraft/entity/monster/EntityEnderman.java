package net.minecraft.entity.monster;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import net.aR;
import net.ay;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman$1;
import net.minecraft.entity.monster.EntityEnderman$AIFindPlayer;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 0.15000000596046448D, 0)).setSaved(false);
   private static final Set carriableBlocks = Sets.newIdentityHashSet();
   private boolean isAggressive;

   public EntityEnderman(World var1) {
      super(var1);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(10, new ay(this));
      this.tasks.addTask(11, new aR(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(2, new EntityEnderman$AIFindPlayer(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEndermite.class, 10, true, false, new EntityEnderman$1(this)));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0D);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.I.b(16, Short.valueOf((short)0));
      this.I.b(17, Byte.valueOf((byte)0));
      this.I.b(18, Byte.valueOf((byte)0));
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      IBlockState var2 = this.getHeldBlockState();
      var1.setShort("carried", (short)Block.getIdFromBlock(var2.getBlock()));
      var1.setShort("carriedData", (short)var2.getBlock().getMetaFromState(var2));
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      IBlockState var2;
      if(var1.hasKey("carried", 8)) {
         var2 = Block.getBlockFromName(var1.getString("carried")).getStateFromMeta(var1.getShort("carriedData") & '\uffff');
      } else {
         var2 = Block.getBlockById(var1.getShort("carried")).getStateFromMeta(var1.getShort("carriedData") & '\uffff');
      }

      this.setHeldBlockState(var2);
   }

   private boolean shouldAttackPlayer(EntityPlayer var1) {
      ItemStack var2 = var1.inventory.armorInventory[3];
      if(var2.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         return false;
      } else {
         Vec3 var3 = var1.getLook(1.0F).normalize();
         Vec3 var4 = new Vec3(this.posX - var1.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - (var1.posY + (double)var1.getEyeHeight()), this.posZ - var1.posZ);
         double var5 = var4.lengthVector();
         var4 = var4.normalize();
         double var7 = var3.dotProduct(var4);
         return var7 > 1.0D - 0.025D / var5 && var1.canEntityBeSeen(this);
      }
   }

   public float getEyeHeight() {
      return 2.55F;
   }

   public void onLivingUpdate() {
      if(this.worldObj.isRemote) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
         }
      }

      this.isJumping = false;
      super.onLivingUpdate();
   }

   protected void updateAITasks() {
      if(this.isWet()) {
         this.attackEntityFrom(DamageSource.drown, 1.0F);
      }

      if(this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
         this.setScreaming(false);
      }

      if(this.worldObj.isDaytime()) {
         float var1 = this.getBrightness(1.0F);
         if(var1 > 0.5F && this.worldObj.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.setAttackTarget((EntityLivingBase)null);
            this.setScreaming(false);
            this.isAggressive = false;
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   protected boolean teleportRandomly() {
      double var1 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
      double var3 = this.posY + (double)(this.rand.nextInt(64) - 32);
      double var5 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
      return this.a(var1, var3, var5);
   }

   protected boolean teleportToEntity(Entity var1) {
      Vec3 var2 = new Vec3(this.posX - var1.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - var1.posY + (double)var1.getEyeHeight(), this.posZ - var1.posZ);
      var2 = var2.normalize();
      double var3 = 16.0D;
      double var5 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.xCoord * var3;
      double var7 = this.posY + (double)(this.rand.nextInt(16) - 8) - var2.yCoord * var3;
      double var9 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.zCoord * var3;
      return this.a(var5, var7, var9);
   }

   protected boolean a(double var1, double var3, double var5) {
      double var7 = this.posX;
      double var9 = this.posY;
      double var11 = this.posZ;
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      boolean var13 = false;
      BlockPos var14 = new BlockPos(this.posX, this.posY, this.posZ);
      if(this.worldObj.isBlockLoaded(var14)) {
         boolean var15 = false;

         while(var14.getY() > 0) {
            BlockPos var16 = var14.down();
            Block var17 = this.worldObj.getBlockState(var16).getBlock();
            if(var17.getMaterial().blocksMovement()) {
               var15 = true;
            } else {
               --this.posY;
               var14 = var16;
            }
         }

         super.setPositionAndUpdate(this.posX, this.posY, this.posZ);
         if(this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox())) {
            var13 = true;
         }
      }

      this.setPosition(var7, var9, var11);
      return false;
   }

   protected String getLivingSound() {
      return this.isScreaming()?"mob.endermen.scream":"mob.endermen.idle";
   }

   protected String getHurtSound() {
      return "mob.endermen.hit";
   }

   protected String getDeathSound() {
      return "mob.endermen.death";
   }

   protected Item getDropItem() {
      return Items.ender_pearl;
   }

   protected void dropFewItems(boolean var1, int var2) {
      Item var3 = this.getDropItem();
      int var4 = this.rand.nextInt(2 + var2);

      for(int var5 = 0; var5 < var4; ++var5) {
         this.dropItem(var3, 1);
      }

   }

   public void setHeldBlockState(IBlockState var1) {
      this.I.a(16, Short.valueOf((short)(Block.getStateId(var1) & '\uffff')));
   }

   public IBlockState getHeldBlockState() {
      return Block.getStateById(this.I.i(16) & '\uffff');
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if(this.isEntityInvulnerable(var1)) {
         return false;
      } else {
         if(var1.getEntity() == null || !(var1.getEntity() instanceof EntityEndermite)) {
            if(!this.worldObj.isRemote) {
               this.setScreaming(true);
            }

            if(var1 instanceof EntityDamageSource && var1.getEntity() instanceof EntityPlayer) {
               if(var1.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP)var1.getEntity()).theItemInWorldManager.isCreative()) {
                  this.setScreaming(false);
               } else {
                  this.isAggressive = true;
               }
            }

            if(var1 instanceof EntityDamageSourceIndirect) {
               this.isAggressive = false;

               for(int var4 = 0; var4 < 64; ++var4) {
                  if(this.teleportRandomly()) {
                     return true;
                  }
               }

               return false;
            }
         }

         boolean var3 = super.attackEntityFrom(var1, var2);
         if(var1.isUnblockable() && this.rand.nextInt(10) != 0) {
            this.teleportRandomly();
         }

         return var3;
      }
   }

   public boolean isScreaming() {
      return this.I.g(18) > 0;
   }

   public void setScreaming(boolean var1) {
      this.I.a(18, Byte.valueOf((byte)1));
   }

   static AttributeModifier access$000() {
      return attackingSpeedBoostModifier;
   }

   static boolean access$100(EntityEnderman var0, EntityPlayer var1) {
      return var0.shouldAttackPlayer(var1);
   }

   static boolean access$202(EntityEnderman var0, boolean var1) {
      return var0.isAggressive = var1;
   }

   static Set access$300() {
      return carriableBlocks;
   }

   static {
      carriableBlocks.add(Blocks.grass);
      carriableBlocks.add(Blocks.dirt);
      carriableBlocks.add(Blocks.sand);
      carriableBlocks.add(Blocks.gravel);
      carriableBlocks.add(Blocks.yellow_flower);
      carriableBlocks.add(Blocks.red_flower);
      carriableBlocks.add(Blocks.brown_mushroom);
      carriableBlocks.add(Blocks.red_mushroom);
      carriableBlocks.add(Blocks.tnt);
      carriableBlocks.add(Blocks.cactus);
      carriableBlocks.add(Blocks.clay);
      carriableBlocks.add(Blocks.pumpkin);
      carriableBlocks.add(Blocks.melon_block);
      carriableBlocks.add(Blocks.mycelium);
   }
}
