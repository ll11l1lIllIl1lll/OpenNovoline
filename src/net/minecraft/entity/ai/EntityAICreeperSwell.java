package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;

public class EntityAICreeperSwell extends EntityAIBase {
   EntityCreeper swellingCreeper;
   EntityLivingBase creeperAttackTarget;

   public EntityAICreeperSwell(EntityCreeper var1) {
      this.swellingCreeper = var1;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      EntityLivingBase var1 = this.swellingCreeper.getAttackTarget();
      return this.swellingCreeper.getCreeperState() > 0 || this.swellingCreeper.getDistanceSqToEntity(var1) < 9.0D;
   }

   public void startExecuting() {
      this.swellingCreeper.getNavigator().clearPathEntity();
      this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
   }

   public void resetTask() {
      this.creeperAttackTarget = null;
   }

   public void updateTask() {
      if(this.creeperAttackTarget == null) {
         this.swellingCreeper.setCreeperState(-1);
      } else if(this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > 49.0D) {
         this.swellingCreeper.setCreeperState(-1);
      } else if(!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget)) {
         this.swellingCreeper.setCreeperState(-1);
      } else {
         this.swellingCreeper.setCreeperState(1);
      }

   }
}
