package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAILookIdle extends EntityAIBase {
   private EntityLiving idleEntity;
   private double lookX;
   private double lookZ;
   private int idleTime;

   public EntityAILookIdle(EntityLiving var1) {
      this.idleEntity = var1;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      return this.idleEntity.getRNG().nextFloat() < 0.02F;
   }

   public boolean continueExecuting() {
      return this.idleTime >= 0;
   }

   public void startExecuting() {
      double var1 = 6.283185307179586D * this.idleEntity.getRNG().nextDouble();
      this.lookX = (double)MathHelper.cos(var1);
      this.lookZ = (double)MathHelper.sin(var1);
      this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
   }

   public void updateTask() {
      --this.idleTime;
      this.idleEntity.getLookHelper().setLookPosition(this.idleEntity.posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight(), this.idleEntity.posZ + this.lookZ, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
   }
}
