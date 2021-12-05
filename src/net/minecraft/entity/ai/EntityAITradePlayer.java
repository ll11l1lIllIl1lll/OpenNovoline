package net.minecraft.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase {
   private EntityVillager villager;

   public EntityAITradePlayer(EntityVillager var1) {
      this.villager = var1;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      if(!this.villager.isEntityAlive()) {
         return false;
      } else if(this.villager.isInWater()) {
         return false;
      } else if(!this.villager.onGround) {
         return false;
      } else if(this.villager.velocityChanged) {
         return false;
      } else {
         EntityPlayer var1 = this.villager.getCustomer();
         return this.villager.getDistanceSqToEntity(var1) <= 16.0D && var1.openContainer instanceof Container;
      }
   }

   public void startExecuting() {
      this.villager.getNavigator().clearPathEntity();
   }

   public void resetTask() {
      this.villager.setCustomer((EntityPlayer)null);
   }
}
