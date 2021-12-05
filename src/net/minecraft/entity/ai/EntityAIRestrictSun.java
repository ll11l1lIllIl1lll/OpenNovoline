package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAIRestrictSun extends EntityAIBase {
   private EntityCreature theEntity;

   public EntityAIRestrictSun(EntityCreature var1) {
      this.theEntity = var1;
   }

   public boolean shouldExecute() {
      return this.theEntity.worldObj.isDaytime();
   }

   public void startExecuting() {
      ((PathNavigateGround)this.theEntity.getNavigator()).setAvoidSun(true);
   }

   public void resetTask() {
      ((PathNavigateGround)this.theEntity.getNavigator()).setAvoidSun(false);
   }
}
