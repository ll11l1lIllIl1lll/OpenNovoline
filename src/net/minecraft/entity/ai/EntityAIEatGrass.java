package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTallGrass$EnumType;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase {
   private static final Predicate field_179505_b = BlockStateHelper.forBlock(Blocks.tallgrass).where(BlockTallGrass.TYPE, Predicates.equalTo(BlockTallGrass$EnumType.GRASS));
   private EntityLiving grassEaterEntity;
   private World entityWorld;
   int eatingGrassTimer;

   public EntityAIEatGrass(EntityLiving var1) {
      this.grassEaterEntity = var1;
      this.entityWorld = var1.worldObj;
      this.setMutexBits(7);
   }

   public boolean shouldExecute() {
      if(this.grassEaterEntity.getRNG().nextInt(this.grassEaterEntity.isChild()?50:1000) != 0) {
         return false;
      } else {
         BlockPos var1 = new BlockPos(this.grassEaterEntity.posX, this.grassEaterEntity.posY, this.grassEaterEntity.posZ);
         return field_179505_b.apply(this.entityWorld.getBlockState(var1)) || this.entityWorld.getBlockState(var1.down()).getBlock() == Blocks.grass;
      }
   }

   public void startExecuting() {
      this.eatingGrassTimer = 40;
      this.entityWorld.setEntityState(this.grassEaterEntity, (byte)10);
      this.grassEaterEntity.getNavigator().clearPathEntity();
   }

   public void resetTask() {
      this.eatingGrassTimer = 0;
   }

   public boolean continueExecuting() {
      return this.eatingGrassTimer > 0;
   }

   public int getEatingGrassTimer() {
      return this.eatingGrassTimer;
   }

   public void updateTask() {
      this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);
      if(this.eatingGrassTimer == 4) {
         BlockPos var1 = new BlockPos(this.grassEaterEntity.posX, this.grassEaterEntity.posY, this.grassEaterEntity.posZ);
         if(field_179505_b.apply(this.entityWorld.getBlockState(var1))) {
            if(this.entityWorld.getGameRules().getBoolean("mobGriefing")) {
               this.entityWorld.destroyBlock(var1, false);
            }

            this.grassEaterEntity.eatGrassBonus();
         } else {
            BlockPos var2 = var1.down();
            if(this.entityWorld.getBlockState(var2).getBlock() == Blocks.grass) {
               if(this.entityWorld.getGameRules().getBoolean("mobGriefing")) {
                  this.entityWorld.playAuxSFX(2001, var2, Block.getIdFromBlock(Blocks.grass));
                  this.entityWorld.setBlockState(var2, Blocks.dirt.getDefaultState(), 2);
               }

               this.grassEaterEntity.eatGrassBonus();
            }
         }
      }

   }
}
