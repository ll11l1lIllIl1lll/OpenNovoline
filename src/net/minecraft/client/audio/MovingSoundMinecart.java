package net.minecraft.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MovingSoundMinecart extends MovingSound {
   private final EntityMinecart minecart;
   private float distance = 0.0F;

   public MovingSoundMinecart(EntityMinecart var1) {
      super(new ResourceLocation("minecraft:minecart.base"));
      this.minecart = var1;
      this.repeat = true;
      this.repeatDelay = 0;
   }

   public void update() {
      if(this.minecart.isDead) {
         this.donePlaying = true;
      } else {
         this.xPosF = (float)this.minecart.posX;
         this.yPosF = (float)this.minecart.posY;
         this.zPosF = (float)this.minecart.posZ;
         float var1 = MathHelper.sqrt_double(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);
         if((double)var1 >= 0.01D) {
            this.distance = MathHelper.clamp_float(this.distance + 0.0025F, 0.0F, 1.0F);
            this.volume = 0.0F + MathHelper.clamp_float(var1, 0.0F, 0.5F) * 0.7F;
         } else {
            this.distance = 0.0F;
            this.volume = 0.0F;
         }
      }

   }
}
