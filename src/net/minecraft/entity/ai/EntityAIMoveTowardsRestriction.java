package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
   private EntityCreature theEntity;
   private double b;
   private double f;
   private double d;
   private double movementSpeed;

   public EntityAIMoveTowardsRestriction(EntityCreature var1, double var2) {
      this.theEntity = var1;
      this.movementSpeed = var2;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if(this.theEntity.isWithinHomeDistanceCurrentPosition()) {
         return false;
      } else {
         BlockPos var1 = this.theEntity.getHomePosition();
         Vec3 var2 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
         return false;
      }
   }

   public boolean continueExecuting() {
      return !this.theEntity.getNavigator().noPath();
   }

   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.b, this.f, this.d, this.movementSpeed);
   }
}
