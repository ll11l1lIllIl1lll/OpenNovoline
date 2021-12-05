package net.minecraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStandArmor extends ModelBiped {
   public ModelArmorStandArmor() {
      this(0.0F);
   }

   public ModelArmorStandArmor(float var1) {
      this(var1, 64, 32);
   }

   protected ModelArmorStandArmor(float var1, int var2, int var3) {
      super(var1, 0.0F, var2, var3);
   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      if(var7 instanceof EntityArmorStand) {
         EntityArmorStand var8 = (EntityArmorStand)var7;
         this.bipedHead.rotateAngleX = 0.017453292F * var8.getHeadRotation().getX();
         this.bipedHead.rotateAngleY = 0.017453292F * var8.getHeadRotation().getY();
         this.bipedHead.rotateAngleZ = 0.017453292F * var8.getHeadRotation().getZ();
         this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
         this.bipedBody.rotateAngleX = 0.017453292F * var8.getBodyRotation().getX();
         this.bipedBody.rotateAngleY = 0.017453292F * var8.getBodyRotation().getY();
         this.bipedBody.rotateAngleZ = 0.017453292F * var8.getBodyRotation().getZ();
         this.bipedLeftArm.rotateAngleX = 0.017453292F * var8.getLeftArmRotation().getX();
         this.bipedLeftArm.rotateAngleY = 0.017453292F * var8.getLeftArmRotation().getY();
         this.bipedLeftArm.rotateAngleZ = 0.017453292F * var8.getLeftArmRotation().getZ();
         this.bipedRightArm.rotateAngleX = 0.017453292F * var8.getRightArmRotation().getX();
         this.bipedRightArm.rotateAngleY = 0.017453292F * var8.getRightArmRotation().getY();
         this.bipedRightArm.rotateAngleZ = 0.017453292F * var8.getRightArmRotation().getZ();
         this.bipedLeftLeg.rotateAngleX = 0.017453292F * var8.getLeftLegRotation().getX();
         this.bipedLeftLeg.rotateAngleY = 0.017453292F * var8.getLeftLegRotation().getY();
         this.bipedLeftLeg.rotateAngleZ = 0.017453292F * var8.getLeftLegRotation().getZ();
         this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
         this.bipedRightLeg.rotateAngleX = 0.017453292F * var8.getRightLegRotation().getX();
         this.bipedRightLeg.rotateAngleY = 0.017453292F * var8.getRightLegRotation().getY();
         this.bipedRightLeg.rotateAngleZ = 0.017453292F * var8.getRightLegRotation().getZ();
         this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
         copyModelAngles(this.bipedHead, this.bipedHeadwear);
      }

   }
}
