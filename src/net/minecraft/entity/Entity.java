package net.minecraft.entity;

import cc.novoline.Novoline;
import cc.novoline.events.EventManager;
import cc.novoline.events.events.StepConfirmEvent;
import cc.novoline.modules.combat.HitBox;
import cc.novoline.modules.move.Scaffold;
import cc.novoline.modules.player.Freecam;
import cc.novoline.modules.visual.Camera;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.aSv;
import net.rF;
import net.minecraft.block.Block;
import net.minecraft.block.Block$SoundType;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern$PatternHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandResultStats$Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent$Action;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos$MutableBlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing$Axis;
import net.minecraft.util.EnumFacing$AxisDirection;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity implements ICommandSender {
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static int nextEntityID;
   private final CommandResultStats cmdResultStats;
   public double renderDistanceWeight;
   public boolean preventEntitySpawning;
   public Entity riddenByEntity;
   public Entity ridingEntity;
   public boolean forceSpawn;
   public World worldObj;
   @aSv
   public double prevPosX;
   @aSv
   public double prevPosY;
   @aSv
   public double prevPosZ;
   @aSv
   public double posX;
   @aSv
   public double posY;
   @aSv
   public double posZ;
   @aSv
   public double motionX;
   @aSv
   public double motionY;
   @aSv
   public double motionZ;
   @aSv
   public float rotationYaw;
   @aSv
   public float rotationPitch;
   @aSv
   public float prevRotationYaw;
   @aSv
   public float prevRotationPitch;
   @aSv
   public boolean onGround;
   public float cameraRotationPitch;
   public float cameraRotationYaw;
   public float prevCameraRotationPitch;
   public float prevCameraRotationYaw;
   public boolean isCollidedHorizontally;
   public boolean isCollidedVertically;
   public boolean isCollided;
   public boolean velocityChanged;
   @aSv
   public boolean isDead;
   public float width;
   public float height;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   @aSv
   public float fallDistance;
   @aSv
   public double lastTickPosX;
   @aSv
   public double lastTickPosY;
   @aSv
   public double lastTickPosZ;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   @aSv
   public int ticksExisted;
   public int fireResistance;
   @aSv
   public int hurtResistantTime;
   public boolean addedToChunk;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   public int serverPosX;
   public int serverPosY;
   public int serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   public int dimension;
   protected boolean isInWeb;
   protected Random rand;
   protected boolean inWater;
   protected boolean firstUpdate;
   protected boolean isImmuneToFire;
   protected rF I;
   protected boolean inPortal;
   protected int portalCounter;
   protected BlockPos field_181016_an;
   protected Vec3 field_181017_ao;
   protected EnumFacing field_181018_ap;
   private UUID entityUniqueID;
   private int entityId;
   private AxisAlignedBB boundingBox;
   private boolean isOutsideBorder;
   private int nextStepDistance;
   private int fire;
   private double entityRiderPitchDelta;
   private double entityRiderYawDelta;
   private boolean invulnerable;

   public Entity(World var1) {
      this.entityId = nextEntityID++;
      this.renderDistanceWeight = 1.0D;
      this.boundingBox = ZERO_AABB;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1;
      this.rand = new Random();
      this.fireResistance = 1;
      this.firstUpdate = true;
      this.setEntityUniqueID(MathHelper.getRandomUuid(this.rand));
      this.cmdResultStats = new CommandResultStats();
      this.worldObj = var1;
      this.setPosition(0.0D, 0.0D, 0.0D);
      this.dimension = var1.provider.getDimensionId();
      this.I = new rF(this);
      this.I.b(0, Byte.valueOf((byte)0));
      this.I.b(1, Short.valueOf((short)300));
      this.I.b(3, Byte.valueOf((byte)0));
      this.I.b(2, "");
      this.I.b(4, Byte.valueOf((byte)0));
      this.entityInit();
   }

   public int getEntityID() {
      return this.entityId;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void onKillCommand() {
      this.setDead();
   }

   protected abstract void entityInit();

   public rF k() {
      return this.I;
   }

   public boolean equals(Object var1) {
      return var1 instanceof Entity && ((Entity)var1).entityId == this.entityId;
   }

   public int hashCode() {
      return this.entityId;
   }

   protected void preparePlayerToSpawn() {
      if(this.worldObj != null) {
         while(true) {
            if(this.posY > 0.0D && this.posY < 256.0D) {
               this.setPosition(this.posX, this.posY, this.posZ);
               if(!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                  ++this.posY;
                  continue;
               }
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
            break;
         }
      }

   }

   public void setDead() {
      this.isDead = true;
   }

   protected void setSize(float var1, float var2) {
      if(var1 != this.width || var2 != this.height) {
         float var3 = this.width;
         this.width = var1;
         this.height = var2;
         this.setEntityBoundingBox(new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double)this.width, this.getEntityBoundingBox().minY + (double)this.height, this.getEntityBoundingBox().minZ + (double)this.width));
         if(this.width > var3 && !this.firstUpdate && !this.worldObj.isRemote) {
            this.moveEntity((double)(var3 - this.width), 0.0D, (double)(var3 - this.width));
         }
      }

   }

   protected void setRotation(float var1, float var2) {
      this.rotationYaw = var1 % 360.0F;
      this.rotationPitch = var2 % 360.0F;
   }

   public void setPosition(double var1, double var3, double var5) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void setAngles(float var1, float var2) {
      float var3 = this.rotationPitch;
      float var4 = this.rotationYaw;
      Camera var5 = (Camera)Novoline.getInstance().getModuleManager().getModule(Camera.class);
      if(!var5.isEnabled() || !var5.f()) {
         this.rotationYaw = (float)((double)this.rotationYaw + (double)var1 * 0.15D);
         this.rotationPitch = (float)((double)this.rotationPitch - (double)var2 * 0.15D);
         this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
         this.prevRotationPitch += this.rotationPitch - var3;
         this.prevRotationYaw += this.rotationYaw - var4;
         this.cameraRotationYaw = this.rotationYaw;
         this.cameraRotationPitch = this.rotationPitch;
      }

      this.cameraRotationPitch = (float)((double)this.cameraRotationPitch - (double)var2 * 0.15D);
      this.cameraRotationPitch = MathHelper.clamp_float(this.cameraRotationPitch, -90.0F, 90.0F);
      this.cameraRotationYaw = (float)((double)this.cameraRotationYaw + (double)var1 * 0.15D);
   }

   public void onUpdate() {
      this.onEntityUpdate();
   }

   public void onEntityUpdate() {
      this.worldObj.theProfiler.startSection("entityBaseTick");
      if(this.ridingEntity != null && this.ridingEntity.isDead) {
         this.ridingEntity = null;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      if(!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer var1 = ((WorldServer)this.worldObj).getMinecraftServer();
         int var2 = this.getMaxInPortalTime();
         if(this.inPortal) {
            if(var1.getAllowNether()) {
               if(this.ridingEntity == null && this.portalCounter++ >= var2) {
                  this.portalCounter = var2;
                  this.timeUntilPortal = this.getPortalCoolDown();
                  byte var3;
                  if(this.worldObj.provider.getDimensionId() == -1) {
                     var3 = 0;
                  } else {
                     var3 = -1;
                  }

                  this.travelToDimension(var3);
               }

               this.inPortal = false;
            }
         } else {
            if(this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if(this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if(this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         this.worldObj.theProfiler.endSection();
      }

      this.spawnRunningParticles();
      this.handleWaterMovement();
      if(this.worldObj.isRemote) {
         this.fire = 0;
      } else if(this.fire > 0) {
         if(this.isImmuneToFire) {
            this.fire -= 4;
            if(this.fire < 0) {
               this.fire = 0;
            }
         } else {
            if(this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.onFire, 1.0F);
            }

            --this.fire;
         }
      }

      if(this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if(this.posY < -64.0D) {
         this.kill();
      }

      if(!this.worldObj.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.worldObj.theProfiler.endSection();
   }

   public int getMaxInPortalTime() {
      return 0;
   }

   protected void setOnFireFromLava() {
      if(!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.lava, 4.0F);
         this.setFire(15);
      }

   }

   public void setFire(int var1) {
      int var2 = var1 * 20;
      var2 = EnchantmentProtection.getFireTimeForEntity(this, var2);
      if(this.fire < var2) {
         this.fire = var2;
      }

   }

   public void extinguish() {
      this.fire = 0;
   }

   protected void kill() {
      this.setDead();
   }

   public boolean isOffsetPositionInLiquid(double var1, double var3, double var5) {
      AxisAlignedBB var7 = this.getEntityBoundingBox().offset(var1, var3, var5);
      return this.isLiquidPresentInAABB(var7);
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB var1) {
      return this.worldObj.getCollidingBoundingBoxes(this, var1).isEmpty() && !this.worldObj.isAnyLiquid(var1);
   }

   public void moveEntity(double var1, double var3, double var5) {
      if(this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(var1, var3, var5));
         this.resetPositionToBB();
      } else {
         this.worldObj.theProfiler.startSection("move");
         double var7 = this.posX;
         double var9 = this.posY;
         double var11 = this.posZ;
         if(this.isInWeb) {
            this.isInWeb = false;
            var1 *= 0.25D;
            var3 *= 0.05000000074505806D;
            var5 *= 0.25D;
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
         }

         double var13 = var1;
         double var15 = var3;
         double var17 = var5;
         Scaffold var19 = (Scaffold)Novoline.getInstance().getModuleManager().getModule(Scaffold.class);
         if((!this.onGround || !this.isSneaking() || !(this instanceof EntityPlayer)) && (!(this instanceof EntityPlayerSP) || !var19.isEnabled() || !var19.a())) {
            boolean var84 = false;
         } else {
            boolean var10000 = true;
         }

         double var21;
         for(var21 = 0.05D; var1 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(var1, -1.0D, 0.0D)).isEmpty(); var13 = var1) {
            if(var1 < var21 && var1 >= -var21) {
               var1 = 0.0D;
            } else if(var1 > 0.0D) {
               var1 -= var21;
            } else {
               var1 += var21;
            }
         }

         for(; var5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0D, -1.0D, var5)).isEmpty(); var17 = var5) {
            if(var5 < var21 && var5 >= -var21) {
               var5 = 0.0D;
            } else if(var5 > 0.0D) {
               var5 -= var21;
            } else {
               var5 += var21;
            }
         }

         for(; var1 != 0.0D && var5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(var1, -1.0D, var5)).isEmpty(); var17 = var5) {
            if(var1 < var21 && var1 >= -var21) {
               var1 = 0.0D;
            } else if(var1 > 0.0D) {
               var1 -= var21;
            } else {
               var1 += var21;
            }

            var13 = var1;
            if(var5 < var21 && var5 >= -var21) {
               var5 = 0.0D;
            } else if(var5 > 0.0D) {
               var5 -= var21;
            } else {
               var5 += var21;
            }
         }

         List var55 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(var1, var3, var5));
         AxisAlignedBB var22 = this.getEntityBoundingBox();

         for(AxisAlignedBB var24 : var55) {
            var3 = var24.calculateYOffset(this.getEntityBoundingBox(), var3);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, var3, 0.0D));
         if(!this.onGround && (var15 == var3 || var15 >= 0.0D)) {
            boolean var86 = false;
         } else {
            boolean var85 = true;
         }

         for(AxisAlignedBB var25 : var55) {
            var1 = var25.calculateXOffset(this.getEntityBoundingBox(), var1);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(var1, 0.0D, 0.0D));

         for(AxisAlignedBB var60 : var55) {
            var5 = var60.calculateZOffset(this.getEntityBoundingBox(), var5);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, var5));
         if(this.stepHeight > 0.0F && (var13 != var1 || var17 != var5)) {
            double var58 = var1;
            double var26 = var3;
            double var28 = var5;
            AxisAlignedBB var30 = this.getEntityBoundingBox();
            this.setEntityBoundingBox(var22);
            var3 = (double)this.stepHeight;
            List var31 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(var13, var3, var17));
            AxisAlignedBB var32 = this.getEntityBoundingBox();
            AxisAlignedBB var33 = var32.addCoord(var13, 0.0D, var17);
            double var34 = var3;

            for(AxisAlignedBB var37 : var31) {
               var34 = var37.calculateYOffset(var33, var34);
            }

            var32 = var32.offset(0.0D, var34, 0.0D);
            double var73 = var13;

            for(AxisAlignedBB var39 : var31) {
               var73 = var39.calculateXOffset(var32, var73);
            }

            var32 = var32.offset(var73, 0.0D, 0.0D);
            double var74 = var17;

            for(AxisAlignedBB var41 : var31) {
               var74 = var41.calculateZOffset(var32, var74);
            }

            var32 = var32.offset(0.0D, 0.0D, var74);
            AxisAlignedBB var75 = this.getEntityBoundingBox();
            double var79 = var3;

            for(AxisAlignedBB var44 : var31) {
               var79 = var44.calculateYOffset(var75, var79);
            }

            var75 = var75.offset(0.0D, var79, 0.0D);
            double var80 = var13;

            for(AxisAlignedBB var46 : var31) {
               var80 = var46.calculateXOffset(var75, var80);
            }

            var75 = var75.offset(var80, 0.0D, 0.0D);
            double var81 = var17;

            for(AxisAlignedBB var48 : var31) {
               var81 = var48.calculateZOffset(var75, var81);
            }

            var75 = var75.offset(0.0D, 0.0D, var81);
            double var82 = var73 * var73 + var74 * var74;
            double var49 = var80 * var80 + var81 * var81;
            if(var82 > var49) {
               var1 = var73;
               var5 = var74;
               var3 = -var34;
               this.setEntityBoundingBox(var32);
            } else {
               var1 = var80;
               var5 = var81;
               var3 = -var79;
               this.setEntityBoundingBox(var75);
            }

            for(AxisAlignedBB var52 : var31) {
               var3 = var52.calculateYOffset(this.getEntityBoundingBox(), var3);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, var3, 0.0D));
            if(var58 * var58 + var28 * var28 >= var1 * var1 + var5 * var5) {
               var1 = var58;
               var3 = var26;
               var5 = var28;
               this.setEntityBoundingBox(var30);
            } else {
               StepConfirmEvent var83 = new StepConfirmEvent();
               EventManager.call(var83);
            }
         }

         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("rest");
         this.resetPositionToBB();
         this.isCollidedHorizontally = var13 != var1 || var17 != var5;
         this.isCollidedVertically = var15 != var3;
         this.onGround = this.isCollidedVertically && var15 < 0.0D;
         this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
         int var59 = MathHelper.floor_double(this.posX);
         int var61 = MathHelper.floor_double(this.posY - 0.20000000298023224D);
         int var62 = MathHelper.floor_double(this.posZ);
         BlockPos var27 = new BlockPos(var59, var61, var62);
         Block var63 = this.worldObj.getBlockState(var27).getBlock();
         if(var63.getMaterial() == Material.air) {
            Block var29 = this.worldObj.getBlockState(var27.down()).getBlock();
            if(var29 instanceof BlockFence || var29 instanceof BlockWall || var29 instanceof BlockFenceGate) {
               var63 = var29;
               var27 = var27.down();
            }
         }

         this.updateFallState(var3, this.onGround, var63, var27);
         if(var13 != var1) {
            this.motionX = 0.0D;
         }

         if(var17 != var5) {
            this.motionZ = 0.0D;
         }

         if(var15 != var3) {
            var63.onLanded(this.worldObj, this);
         }

         if(this.canTriggerWalking() && this.ridingEntity == null || this instanceof EntityPlayerSP) {
            double var64 = this.posX - var7;
            double var67 = this.posY - var9;
            double var72 = this.posZ - var11;
            if(var63 != Blocks.ladder) {
               var67 = 0.0D;
            }

            if(this.onGround) {
               var63.onEntityCollidedWithBlock(this.worldObj, var27, this);
            }

            this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(var64 * var64 + var72 * var72) * 0.6D);
            this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(var64 * var64 + var67 * var67 + var72 * var72) * 0.6D);
            if(this.distanceWalkedOnStepModified > (float)this.nextStepDistance && var63.getMaterial() != Material.air) {
               this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
               if(this.isInWater()) {
                  float var35 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;
                  if(var35 > 1.0F) {
                     var35 = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), var35, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               }

               this.playStepSound(var27, var63);
            }
         }

         Entity var87 = this;

         try {
            var87.doBlockCollisions();
         } catch (Throwable var53) {
            CrashReport var66 = CrashReport.makeCrashReport(var53, "Checking entity block collision");
            CrashReportCategory var68 = var66.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(var68);
            throw new ReportedException(var66);
         }

         boolean var65 = this.isWet();
         if(this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001D, 0.001D, 0.001D))) {
            this.dealFireDamage(1);
            ++this.fire;
            if(this.fire == 0) {
               this.setFire(8);
            }
         } else if(this.fire <= 0) {
            this.fire = -this.fireResistance;
         }

         if(this.fire > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.fireResistance;
         }

         this.worldObj.theProfiler.endSection();
      }

   }

   private void resetPositionToBB() {
      this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
      this.posY = this.getEntityBoundingBox().minY;
      this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
   }

   protected String getSwimSound() {
      return "game.neutral.swim";
   }

   protected void doBlockCollisions() {
      BlockPos var1 = new BlockPos(this.getEntityBoundingBox().minX + 0.001D, this.getEntityBoundingBox().minY + 0.001D, this.getEntityBoundingBox().minZ + 0.001D);
      BlockPos var2 = new BlockPos(this.getEntityBoundingBox().maxX - 0.001D, this.getEntityBoundingBox().maxY - 0.001D, this.getEntityBoundingBox().maxZ - 0.001D);
      if(this.worldObj.isAreaLoaded(var1, var2)) {
         for(int var3 = var1.getX(); var3 <= var2.getX(); ++var3) {
            for(int var4 = var1.getY(); var4 <= var2.getY(); ++var4) {
               for(int var5 = var1.getZ(); var5 <= var2.getZ(); ++var5) {
                  BlockPos var6 = new BlockPos(var3, var4, var5);
                  IBlockState var7 = this.worldObj.getBlockState(var6);
                  IBlockState var10000 = var7;

                  try {
                     var10000.getBlock().onEntityCollidedWithBlock(this.worldObj, var6, var7, this);
                  } catch (Throwable var11) {
                     CrashReport var9 = CrashReport.makeCrashReport(var11, "Colliding entity with block");
                     CrashReportCategory var10 = var9.makeCategory("Block being collided with");
                     CrashReportCategory.addBlockInfo(var10, var6, var7);
                     throw new ReportedException(var9);
                  }
               }
            }
         }
      }

   }

   protected void playStepSound(BlockPos var1, Block var2) {
      Block$SoundType var3 = var2.stepSound;
      if(this.worldObj.getBlockState(var1.up()).getBlock() == Blocks.snow_layer) {
         var3 = Blocks.snow_layer.stepSound;
         this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getFrequency());
      } else if(!var2.getMaterial().isLiquid()) {
         this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getFrequency());
      }

   }

   public void playSound(String var1, float var2, float var3) {
      if(!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, var1, var2, var3);
      }

   }

   public boolean isSilent() {
      return this.I.g(4) == 1;
   }

   public void setSilent(boolean var1) {
      this.I.a(4, Byte.valueOf((byte)1));
   }

   protected boolean canTriggerWalking() {
      return true;
   }

   protected void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
      if(this.fallDistance > 0.0F) {
         var4.onFallenUpon(this.worldObj, var5, this, this.fallDistance);
         this.fallDistance = 0.0F;
      }

   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   protected void dealFireDamage(int var1) {
      if(!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.inFire, (float)var1);
      }

   }

   public final boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public void fall(float var1, float var2) {
      if(this.riddenByEntity != null) {
         this.riddenByEntity.fall(var1, var2);
      }

   }

   public boolean isWet() {
      return this.inWater || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY, this.posZ)) || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY + (double)this.height, this.posZ));
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public void handleWaterMovement() {
      if(this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this)) {
         if(!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.fire = 0;
      } else {
         this.inWater = false;
      }

   }

   protected void resetHeight() {
      float var1 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;
      if(var1 > 1.0F) {
         var1 = 1.0F;
      }

      this.playSound(this.getSplashSound(), var1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      float var2 = (float)MathHelper.floor_double(this.getEntityBoundingBox().minY);

      for(int var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3) {
         float var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double)var4, (double)(var2 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ, new int[0]);
      }

      for(int var6 = 0; (float)var6 < 1.0F + this.width * 20.0F; ++var6) {
         float var7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float var8 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double)var7, (double)(var2 + 1.0F), this.posZ + (double)var8, this.motionX, this.motionY, this.motionZ, new int[0]);
      }

   }

   public void spawnRunningParticles() {
      if(this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }

   }

   protected void createRunningParticles() {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.posY - 0.20000000298023224D);
      int var3 = MathHelper.floor_double(this.posZ);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      IBlockState var5 = this.worldObj.getBlockState(var4);
      Block var6 = var5.getBlock();
      if(var6.getRenderType() != -1) {
         this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, new int[]{Block.getStateId(var5)});
      }

   }

   protected String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   public boolean isInsideOfMaterial(Material var1) {
      double var2 = this.posY + (double)this.getEyeHeight();
      BlockPos var4 = new BlockPos(this.posX, var2, this.posZ);
      IBlockState var5 = this.worldObj.getBlockState(var4);
      Block var6 = var5.getBlock();
      if(var6.getMaterial() == var1) {
         float var7 = BlockLiquid.getLiquidHeightPercent(var5.getBlock().getMetaFromState(var5)) - 0.11111111F;
         float var8 = (float)(var4.getY() + 1) - var7;
         return var2 < (double)var8;
      } else {
         return false;
      }
   }

   public boolean isInLava() {
      return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
   }

   public void moveFlying(float var1, float var2, float var3) {
      float var4 = var1 * var1 + var2 * var2;
      if(var4 >= 1.0E-4F) {
         var4 = MathHelper.sqrt_float(var4);
         if(var4 < 1.0F) {
            var4 = 1.0F;
         }

         var4 = var3 / var4;
         var1 = var1 * var4;
         var2 = var2 * var4;
         float var5 = MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F);
         float var6 = MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F);
         this.motionX += (double)(var1 * var6 - var2 * var5);
         this.motionZ += (double)(var2 * var6 + var1 * var5);
      }

   }

   public int getBrightnessForRender(float var1) {
      BlockPos var2 = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(var2)?this.worldObj.getCombinedLight(var2, 0):0;
   }

   public float getBrightness(float var1) {
      BlockPos var2 = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(var2)?this.worldObj.getLightBrightness(var2):0.0F;
   }

   public void setWorld(World var1) {
      this.worldObj = var1;
   }

   public void setPositionAndRotation(double var1, double var3, double var5, float var7, float var8) {
      this.prevPosX = this.posX = var1;
      this.prevPosY = this.posY = var3;
      this.prevPosZ = this.posZ = var5;
      this.prevRotationYaw = this.rotationYaw = var7;
      this.prevRotationPitch = this.rotationPitch = var8;
      double var9 = (double)(this.prevRotationYaw - var7);
      if(var9 < -180.0D) {
         this.prevRotationYaw += 360.0F;
      }

      if(var9 >= 180.0D) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(var7, var8);
   }

   public void moveToBlockPosAndAngles(BlockPos var1, float var2, float var3) {
      this.setLocationAndAngles((double)var1.getX() + 0.5D, (double)var1.getY(), (double)var1.getZ() + 0.5D, var2, var3);
   }

   public void setLocationAndAngles(double var1, double var3, double var5, float var7, float var8) {
      this.lastTickPosX = this.prevPosX = this.posX = var1;
      this.lastTickPosY = this.prevPosY = this.posY = var3;
      this.lastTickPosZ = this.prevPosZ = this.posZ = var5;
      this.rotationYaw = var7;
      this.rotationPitch = var8;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getDistanceToEntity(Entity var1) {
      float var2 = (float)(this.posX - var1.posX);
      float var3 = (float)(this.posY - var1.posY);
      float var4 = (float)(this.posZ - var1.posZ);
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float a(TileEntity var1) {
      BlockPos var2 = var1.getPos();
      double var3 = this.posX - (double)var2.getX() + 0.5D;
      double var5 = this.posY - (double)var2.getY() + 0.5D;
      double var7 = this.posZ - (double)var2.getZ() + 0.5D;
      return MathHelper.sqrt_double(var3 * var3 + var5 * var5 + var7 * var7);
   }

   public float i(Entity var1) {
      float var2 = (float)(this.posX - var1.posX);
      float var3 = (float)(this.posZ - var1.posZ);
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3);
   }

   public double getDistance2D(Entity var1) {
      double var2 = Math.abs(var1.posX - this.posX);
      double var4 = Math.abs(var1.posZ - this.posZ);
      return Math.sqrt(var2 * var2 + var4 * var4);
   }

   public double getDistanceSq(double var1, double var3, double var5) {
      double var7 = this.posX - var1;
      double var9 = this.posY - var3;
      double var11 = this.posZ - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getDistanceSq(BlockPos var1) {
      return var1.distanceSq(this.posX, this.posY, this.posZ);
   }

   public double getDistanceSqToCenter(BlockPos var1) {
      return var1.distanceSqToCenter(this.posX, this.posY, this.posZ);
   }

   public double getDistance(double var1, double var3, double var5) {
      double var7 = this.posX - var1;
      double var9 = this.posY - var3;
      double var11 = this.posZ - var5;
      return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double getDistanceY(double var1) {
      double var3 = this.posY - var1;
      return (double)MathHelper.sqrt_double(var3 * var3);
   }

   public double getDistance(BlockPos var1) {
      double var2 = this.posX - (double)var1.getX();
      double var4 = this.posY - (double)var1.getY();
      double var6 = this.posZ - (double)var1.getZ();
      return (double)MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double getDistance(double var1, double var3) {
      double var5 = this.posX - var1;
      double var7 = this.posZ - var3;
      return (double)MathHelper.sqrt_double(var5 * var5 + var7 * var7);
   }

   public double getDistanceSqToEntity(Entity var1) {
      double var2 = this.posX - var1.posX;
      double var4 = this.posY - var1.posY;
      double var6 = this.posZ - var1.posZ;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void onCollideWithPlayer(EntityPlayer var1) {
   }

   public void applyEntityCollision(Entity var1) {
      if(var1.riddenByEntity != this && var1.ridingEntity != this && !var1.noClip && !this.noClip) {
         double var2 = var1.posX - this.posX;
         double var4 = var1.posZ - this.posZ;
         double var6 = MathHelper.abs_max(var2, var4);
         if(var6 >= 0.009999999776482582D) {
            var6 = (double)MathHelper.sqrt_double(var6);
            var2 = var2 / var6;
            var4 = var4 / var6;
            double var8 = 1.0D / var6;
            if(var8 > 1.0D) {
               var8 = 1.0D;
            }

            var2 = var2 * var8;
            var4 = var4 * var8;
            var2 = var2 * 0.05000000074505806D;
            var4 = var4 * 0.05000000074505806D;
            var2 = var2 * (double)(1.0F - this.entityCollisionReduction);
            var4 = var4 * (double)(1.0F - this.entityCollisionReduction);
            if(this.riddenByEntity == null) {
               this.addVelocity(-var2, 0.0D, -var4);
            }

            if(var1.riddenByEntity == null) {
               var1.addVelocity(var2, 0.0D, var4);
            }
         }
      }

   }

   public void addVelocity(double var1, double var3, double var5) {
      this.motionX += var1;
      this.motionY += var3;
      this.motionZ += var5;
      this.isAirBorne = true;
   }

   protected void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if(!this.isEntityInvulnerable(var1)) {
         this.setBeenAttacked();
      }

      return false;
   }

   public Vec3 getLook(float var1) {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }

   protected final Vec3 getVectorForRotation(float var1, float var2) {
      float var3 = MathHelper.cos(-var2 * 0.017453292F - 3.1415927F);
      float var4 = MathHelper.sin(-var2 * 0.017453292F - 3.1415927F);
      float var5 = -MathHelper.cos(-var1 * 0.017453292F);
      float var6 = MathHelper.sin(-var1 * 0.017453292F);
      return new Vec3((double)(var4 * var5), (double)var6, (double)(var3 * var5));
   }

   public Vec3 getPositionEyes(float var1) {
      if(var1 == 1.0F) {
         return new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      } else {
         double var2 = this.prevPosX + (this.posX - this.prevPosX) * (double)var1;
         double var4 = this.prevPosY + (this.posY - this.prevPosY) * (double)var1 + (double)this.getEyeHeight();
         double var6 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var1;
         return new Vec3(var2, var4, var6);
      }
   }

   public MovingObjectPosition rayTrace(double var1, float var3) {
      Vec3 var4 = this.getPositionEyes(var3);
      Vec3 var5 = this.getLook(var3);
      Vec3 var6 = var4.addVector(var5.xCoord * var1, var5.yCoord * var1, var5.zCoord * var1);
      return this.worldObj.rayTraceBlocks(var4, var6, false, false, true);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void addToPlayerScore(Entity var1, int var2) {
   }

   public boolean isInRangeToRender3d(double var1, double var3, double var5) {
      double var7 = this.posX - var1;
      double var9 = this.posY - var3;
      double var11 = this.posZ - var5;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      return this.isInRangeToRenderDist(var13);
   }

   public boolean isInRangeToRenderDist(double var1) {
      double var3 = this.getEntityBoundingBox().getAverageEdgeLength();
      if(Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 = var3 * 64.0D * this.renderDistanceWeight;
      return var1 < var3 * var3;
   }

   public boolean writeMountToNBT(NBTTagCompound var1) {
      String var2 = this.getEntityString();
      if(!this.isDead) {
         var1.setString("id", var2);
         this.writeToNBT(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeToNBTOptional(NBTTagCompound var1) {
      String var2 = this.getEntityString();
      if(!this.isDead && this.riddenByEntity == null) {
         var1.setString("id", var2);
         this.writeToNBT(var1);
         return true;
      } else {
         return false;
      }
   }

   public void writeToNBT(NBTTagCompound param1) {
      // $FF: Couldn't be decompiled
   }

   public void readFromNBT(NBTTagCompound param1) {
      // $FF: Couldn't be decompiled
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   protected final String getEntityString() {
      return EntityList.getEntityString(this);
   }

   protected abstract void readEntityFromNBT(NBTTagCompound var1);

   protected abstract void writeEntityToNBT(NBTTagCompound var1);

   public void onChunkLoad() {
   }

   protected NBTTagList newDoubleNBTList(double... var1) {
      NBTTagList var2 = new NBTTagList();

      for(double var6 : var1) {
         var2.appendTag(new NBTTagDouble(var6));
      }

      return var2;
   }

   protected NBTTagList newFloatNBTList(float... var1) {
      NBTTagList var2 = new NBTTagList();

      for(float var6 : var1) {
         var2.appendTag(new NBTTagFloat(var6));
      }

      return var2;
   }

   public EntityItem dropItem(Item var1, int var2) {
      return this.dropItemWithOffset(var1, var2, 0.0F);
   }

   public EntityItem dropItemWithOffset(Item var1, int var2, float var3) {
      return this.entityDropItem(new ItemStack(var1, var2, 0), var3);
   }

   public EntityItem entityDropItem(ItemStack var1, float var2) {
      if(var1.stackSize != 0 && var1.getItem() != null) {
         EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.posY + (double)var2, this.posZ, var1);
         var3.setDefaultPickupDelay();
         this.worldObj.spawnEntityInWorld(var3);
         return var3;
      } else {
         return null;
      }
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public boolean isEntityInsideOpaqueBlock() {
      if(!this.noClip && !((Freecam)Novoline.getInstance().getModuleManager().getModule(Freecam.class)).isEnabled()) {
         BlockPos$MutableBlockPos var1 = new BlockPos$MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

         for(int var2 = 0; var2 < 8; ++var2) {
            int var3 = MathHelper.floor_double(this.posY + (double)(((float)(var2 % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
            int var4 = MathHelper.floor_double(this.posX + (double)(((float)((var2 >> 1) % 2) - 0.5F) * this.width * 0.8F));
            int var5 = MathHelper.floor_double(this.posZ + (double)(((float)((var2 >> 2) % 2) - 0.5F) * this.width * 0.8F));
            if(var1.getX() != var4 || var1.getY() != var3 || var1.getZ() != var5) {
               var1.func_181079_c(var4, var3, var5);
               if(this.worldObj.getBlockState(var1).getBlock().isVisuallyOpaque()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean interactFirst(EntityPlayer var1) {
      return false;
   }

   public AxisAlignedBB getCollisionBox(Entity var1) {
      return null;
   }

   public Entity collidedEntity() {
      return null;
   }

   public void updateRidden() {
      if(this.ridingEntity.isDead) {
         this.ridingEntity = null;
      } else {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.onUpdate();
         if(this.ridingEntity != null) {
            this.ridingEntity.updateRiderPosition();
            this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

            for(this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D) {
               ;
            }

            while(this.entityRiderYawDelta < -180.0D) {
               this.entityRiderYawDelta += 360.0D;
            }

            while(this.entityRiderPitchDelta >= 180.0D) {
               this.entityRiderPitchDelta -= 360.0D;
            }

            while(this.entityRiderPitchDelta < -180.0D) {
               this.entityRiderPitchDelta += 360.0D;
            }

            double var1 = this.entityRiderYawDelta * 0.5D;
            double var3 = this.entityRiderPitchDelta * 0.5D;
            float var5 = 10.0F;
            if(var1 > (double)var5) {
               var1 = (double)var5;
            }

            if(var1 < (double)(-var5)) {
               var1 = (double)(-var5);
            }

            if(var3 > (double)var5) {
               var3 = (double)var5;
            }

            if(var3 < (double)(-var5)) {
               var3 = (double)(-var5);
            }

            this.entityRiderYawDelta -= var1;
            this.entityRiderPitchDelta -= var3;
         }
      }

   }

   public void updateRiderPosition() {
      if(this.riddenByEntity != null) {
         this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
      }

   }

   public double getYOffset() {
      return 0.0D;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.75D;
   }

   public void mountEntity(Entity var1) {
      this.entityRiderPitchDelta = 0.0D;
      this.entityRiderYawDelta = 0.0D;
      if(this.ridingEntity != null) {
         this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.getEntityBoundingBox().minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
         this.ridingEntity.riddenByEntity = null;
      }

      this.ridingEntity = null;
   }

   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPosition(var1, var3, var5);
      this.setRotation(var7, var8);
      List var11 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125D, 0.0D, 0.03125D));
      if(!var11.isEmpty()) {
         double var12 = 0.0D;

         for(AxisAlignedBB var15 : var11) {
            if(var15.maxY > var12) {
               var12 = var15.maxY;
            }
         }

         var3 = var3 + (var12 - this.getEntityBoundingBox().minY);
         this.setPosition(var1, var3, var5);
      }

   }

   public float getCollisionBorderSize() {
      return ((HitBox)Novoline.getInstance().getModuleManager().getModule(HitBox.class)).isEnabled()?((Float)((HitBox)Novoline.getInstance().getModuleManager().getModule(HitBox.class)).getHitBoxSize().get()).floatValue():0.1F;
   }

   public Vec3 getLookVec() {
      return null;
   }

   public void func_181015_d(BlockPos var1) {
      if(this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCoolDown();
      } else {
         if(!this.worldObj.isRemote && !var1.equals(this.field_181016_an)) {
            this.field_181016_an = var1;
            BlockPattern$PatternHelper var2 = Blocks.portal.func_181089_f(this.worldObj, var1);
            double var3 = var2.getFinger().getAxis() == EnumFacing$Axis.X?(double)var2.func_181117_a().getZ():(double)var2.func_181117_a().getX();
            double var5 = var2.getFinger().getAxis() == EnumFacing$Axis.X?this.posZ:this.posX;
            var5 = Math.abs(MathHelper.func_181160_c(var5 - (double)(var2.getFinger().rotateY().getAxisDirection() == EnumFacing$AxisDirection.NEGATIVE?1:0), var3, var3 - (double)var2.func_181118_d()));
            double var7 = MathHelper.func_181160_c(this.posY - 1.0D, (double)var2.func_181117_a().getY(), (double)(var2.func_181117_a().getY() - var2.func_181119_e()));
            this.field_181017_ao = new Vec3(var5, var7, 0.0D);
            this.field_181018_ap = var2.getFinger();
         }

         this.inPortal = true;
      }

   }

   public int getPortalCoolDown() {
      return 300;
   }

   public void setVelocity(double var1, double var3, double var5) {
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
   }

   public void handleStatusUpdate(byte var1) {
   }

   public void performHurtAnimation() {
   }

   public ItemStack[] getInventory() {
      return null;
   }

   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
   }

   public boolean isBurning() {
      boolean var1 = this.worldObj != null && this.worldObj.isRemote;
      return !this.isImmuneToFire && (this.fire > 0 || this.getFlag(0));
   }

   public boolean isRiding() {
      return this.ridingEntity != null;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void setSneaking(boolean var1) {
      this.setFlag(1, var1);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean var1) {
      this.setFlag(3, var1);
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public void setInvisible(boolean var1) {
      this.setFlag(5, var1);
   }

   public boolean isInvisibleToPlayer(EntityPlayer var1) {
      return !var1.isSpectator() && this.isInvisible();
   }

   public boolean isEating() {
      return this.getFlag(4);
   }

   public void setEating(boolean var1) {
      this.setFlag(4, var1);
   }

   protected boolean getFlag(int var1) {
      return (this.I.g(0) & 1 << var1) != 0;
   }

   protected void setFlag(int var1, boolean var2) {
      byte var3 = this.I.g(0);
      this.I.a(0, Byte.valueOf((byte)(var3 | 1 << var1)));
   }

   public int getAir() {
      return this.I.i(1);
   }

   public void setAir(int var1) {
      this.I.a(1, Short.valueOf((short)var1));
   }

   public void onStruckByLightning(EntityLightningBolt var1) {
      this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
      ++this.fire;
      if(this.fire == 0) {
         this.setFire(8);
      }

   }

   public void onKillEntity(EntityLivingBase var1) {
   }

   protected boolean pushOutOfBlocks(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      double var8 = var1 - (double)var7.getX();
      double var10 = var3 - (double)var7.getY();
      double var12 = var5 - (double)var7.getZ();
      List var14 = this.worldObj.func_147461_a(this.getEntityBoundingBox());
      if(var14.isEmpty() && !this.worldObj.isBlockFullCube(var7)) {
         return false;
      } else {
         byte var15 = 3;
         double var16 = 9999.0D;
         if(!this.worldObj.isBlockFullCube(var7.west()) && var8 < var16) {
            var16 = var8;
            var15 = 0;
         }

         if(!this.worldObj.isBlockFullCube(var7.east()) && 1.0D - var8 < var16) {
            var16 = 1.0D - var8;
            var15 = 1;
         }

         if(!this.worldObj.isBlockFullCube(var7.up()) && 1.0D - var10 < var16) {
            var16 = 1.0D - var10;
            var15 = 3;
         }

         if(!this.worldObj.isBlockFullCube(var7.north()) && var12 < var16) {
            var16 = var12;
            var15 = 4;
         }

         if(!this.worldObj.isBlockFullCube(var7.south()) && 1.0D - var12 < var16) {
            var16 = 1.0D - var12;
            var15 = 5;
         }

         float var18 = this.rand.nextFloat() * 0.2F + 0.1F;
         switch(var15) {
         case 0:
            this.motionX = (double)(-var18);
            break;
         case 1:
            this.motionX = (double)var18;
         case 2:
         default:
            break;
         case 3:
            this.motionY = (double)var18;
            break;
         case 4:
            this.motionZ = (double)(-var18);
            break;
         case 5:
            this.motionZ = (double)var18;
         }

         return true;
      }
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   @aSv
   public String getName() {
      if(this.hasCustomName()) {
         return this.getCustomNameTag();
      } else {
         String var1 = EntityList.getEntityString(this);
         var1 = "generic";
         return StatCollector.translateToLocal("entity." + var1 + ".name");
      }
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean isEntityEqual(Entity var1) {
      return this == var1;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float var1) {
   }

   public void func_181013_g(float var1) {
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public boolean hitByEntity(Entity var1) {
      return false;
   }

   public String toString() {
      return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[]{this.getClass().getCanonicalName(), this.getName(), Integer.valueOf(this.entityId), this.worldObj == null?"~NULL~":this.worldObj.getWorldInfo().getWorldName(), Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)});
   }

   public boolean isEntityInvulnerable(DamageSource var1) {
      return this.invulnerable && var1 != DamageSource.outOfWorld && !var1.isCreativePlayer();
   }

   public void copyLocationAndAnglesFrom(Entity var1) {
      this.setLocationAndAngles(var1.posX, var1.posY, var1.posZ, var1.rotationYaw, var1.rotationPitch);
   }

   public void copyDataFromOld(Entity var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var1.writeToNBT(var2);
      this.readFromNBT(var2);
      this.timeUntilPortal = var1.timeUntilPortal;
      this.field_181016_an = var1.field_181016_an;
      this.field_181017_ao = var1.field_181017_ao;
      this.field_181018_ap = var1.field_181018_ap;
   }

   public void travelToDimension(int var1) {
      if(!this.worldObj.isRemote && !this.isDead) {
         this.worldObj.theProfiler.startSection("changeDimension");
         MinecraftServer var2 = MinecraftServer.getServer();
         int var3 = this.dimension;
         WorldServer var4 = var2.worldServerForDimension(var3);
         WorldServer var5 = var2.worldServerForDimension(var1);
         this.dimension = var1;
         if(var3 == 1 && var1 == 1) {
            var5 = var2.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.removeEntity(this);
         this.isDead = false;
         this.worldObj.theProfiler.startSection("reposition");
         var2.getConfigurationManager().transferEntityToWorld(this, var3, var4, var5);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity var6 = EntityList.createEntityByName(EntityList.getEntityString(this), var5);
         var6.copyDataFromOld(this);
         if(var3 == 1 && var1 == 1) {
            BlockPos var7 = this.worldObj.getTopSolidOrLiquidBlock(var5.getSpawnPoint());
            var6.moveToBlockPosAndAngles(var7, var6.rotationYaw, var6.rotationPitch);
         }

         var5.spawnEntityInWorld(var6);
         this.isDead = true;
         this.worldObj.theProfiler.endSection();
         var4.resetUpdateEntityTick();
         var5.resetUpdateEntityTick();
         this.worldObj.theProfiler.endSection();
      }

   }

   public float getExplosionResistance(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      return var4.getBlock().getExplosionResistance(this);
   }

   public boolean verifyExplosion(Explosion var1, World var2, BlockPos var3, IBlockState var4, float var5) {
      return true;
   }

   public int getMaxFallHeight() {
      return 3;
   }

   public Vec3 func_181014_aG() {
      return this.field_181017_ao;
   }

   public EnumFacing func_181012_aH() {
      return this.field_181018_ap;
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void addEntityCrashInfo(CrashReportCategory var1) {
      var1.addCrashSectionCallable("Entity Type", this::lambda$addEntityCrashInfo$0);
      var1.addCrashSection("Entity ID", Integer.valueOf(this.entityId));
      var1.addCrashSectionCallable("Entity Name", this::getName);
      var1.addCrashSection("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[]{Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)}));
      var1.addCrashSection("Entity\'s Block location", CrashReportCategory.getCoordinateInfo((double)MathHelper.floor_double(this.posX), (double)MathHelper.floor_double(this.posY), (double)MathHelper.floor_double(this.posZ)));
      var1.addCrashSection("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[]{Double.valueOf(this.motionX), Double.valueOf(this.motionY), Double.valueOf(this.motionZ)}));
      var1.addCrashSectionCallable("Entity\'s Rider", this::lambda$addEntityCrashInfo$1);
      var1.addCrashSectionCallable("Entity\'s Vehicle", this::lambda$addEntityCrashInfo$2);
   }

   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   public UUID getUniqueID() {
      return this.getEntityUniqueID();
   }

   public boolean isPushedByWater() {
      return true;
   }

   @aSv
   public IChatComponent getDisplayName() {
      ChatComponentText var1 = new ChatComponentText(this.getName());
      var1.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      var1.getChatStyle().setInsertion(this.getUniqueID().toString());
      return var1;
   }

   public String getCustomNameTag() {
      return this.I.a(2);
   }

   public void setCustomNameTag(String var1) {
      this.I.a(2, var1);
   }

   public boolean hasCustomName() {
      return this.I.a(2).length() > 0;
   }

   public boolean getAlwaysRenderNameTag() {
      return this.I.g(3) == 1;
   }

   public void setAlwaysRenderNameTag(boolean var1) {
      this.I.a(3, Byte.valueOf((byte)1));
   }

   public void setPositionAndUpdate(double var1, double var3, double var5) {
      this.setLocationAndAngles(var1, var3, var5, this.rotationYaw, this.rotationPitch);
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public void onDataWatcherUpdate(int var1) {
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.getHorizontal(MathHelper.floor_double((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
   }

   protected HoverEvent getHoverEvent() {
      NBTTagCompound var1 = new NBTTagCompound();
      String var2 = EntityList.getEntityString(this);
      var1.setString("id", this.getUniqueID().toString());
      var1.setString("type", var2);
      var1.setString("name", this.getName());
      return new HoverEvent(HoverEvent$Action.SHOW_ENTITY, new ChatComponentText(var1.toString()));
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP var1) {
      return true;
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   public void setEntityBoundingBox(AxisAlignedBB var1) {
      this.boundingBox = var1;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public boolean isOutsideBorder() {
      return this.isOutsideBorder;
   }

   public void setOutsideBorder(boolean var1) {
      this.isOutsideBorder = var1;
   }

   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      return false;
   }

   public void addChatMessage(IChatComponent var1) {
   }

   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return true;
   }

   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5D, this.posZ);
   }

   public Vec3 getPositionVector() {
      return new Vec3(this.posX, this.posY, this.posZ);
   }

   public World getEntityWorld() {
      return this.worldObj;
   }

   public Entity getCommandSenderEntity() {
      return this;
   }

   public boolean sendCommandFeedback() {
      return false;
   }

   public void setCommandStat(CommandResultStats$Type var1, int var2) {
      this.cmdResultStats.func_179672_a(this, var1, var2);
   }

   public CommandResultStats getCommandStats() {
      return this.cmdResultStats;
   }

   public void func_174817_o(Entity var1) {
      this.cmdResultStats.func_179671_a(var1.getCommandStats());
   }

   public NBTTagCompound getNBTTagCompound() {
      return null;
   }

   public void clientUpdateEntityNBT(NBTTagCompound var1) {
   }

   public boolean interactAt(EntityPlayer var1, Vec3 var2) {
      return false;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   protected void applyEnchantments(EntityLivingBase var1, Entity var2) {
      if(var2 instanceof EntityLivingBase) {
         EnchantmentHelper.applyThornEnchantments((EntityLivingBase)var2, var1);
      }

      EnchantmentHelper.applyArthropodEnchantments(var1, var2);
   }

   public boolean isBlockUnder() {
      int var1 = (int)(this.posY - 1.0D);

      while(true) {
         BlockPos var2 = new BlockPos(this.posX, (double)var1, this.posZ);
         if(!(Minecraft.getInstance().world.getBlockState(var2).getBlock() instanceof BlockAir)) {
            return true;
         }

         --var1;
      }
   }

   public double getLastTickDistance() {
      return Math.hypot(this.posX - this.prevPosX, this.posZ - this.prevPosZ);
   }

   public void resetLastTickDistance() {
      this.prevPosX = this.posX;
      this.prevPosZ = this.posZ;
   }

   public UUID getEntityUniqueID() {
      return this.entityUniqueID;
   }

   public void setEntityUniqueID(UUID var1) {
      this.entityUniqueID = var1;
   }

   private String lambda$addEntityCrashInfo$2() throws Exception {
      return this.ridingEntity.toString();
   }

   private String lambda$addEntityCrashInfo$1() throws Exception {
      return this.riddenByEntity.toString();
   }

   private String lambda$addEntityCrashInfo$0() throws Exception {
      return EntityList.getEntityString(this) + " (" + this.getClass().getCanonicalName() + ")";
   }

   private static Throwable b(Throwable var0) {
      return var0;
   }
}
