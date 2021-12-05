package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.realms.RealmsButton;
import net.minecraft.util.MathHelper;

public class RealmsSliderButton extends RealmsButton {
   public float value;
   public boolean sliding;
   private final float minValue;
   private final float maxValue;
   private int steps;

   public RealmsSliderButton(int var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var6, 0, 1.0F, (float)var5);
   }

   public RealmsSliderButton(int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8) {
      super(var1, var2, var3, var4, 20, "");
      this.value = 1.0F;
      this.minValue = var7;
      this.maxValue = var8;
      this.value = this.toPct((float)var6);
      this.getProxy().displayString = this.getMessage();
   }

   public String getMessage() {
      return "";
   }

   public float toPct(float var1) {
      return MathHelper.clamp_float((this.clamp(var1) - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
   }

   public float toValue(float var1) {
      return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.clamp_float(var1, 0.0F, 1.0F));
   }

   public float clamp(float var1) {
      var1 = this.clampSteps(var1);
      return MathHelper.clamp_float(var1, this.minValue, this.maxValue);
   }

   protected float clampSteps(float var1) {
      if(this.steps > 0) {
         var1 = (float)(this.steps * Math.round(var1 / (float)this.steps));
      }

      return var1;
   }

   public int getYImage(boolean var1) {
      return 0;
   }

   public void renderBg(int var1, int var2) {
      if(this.getProxy().visible) {
         if(this.sliding) {
            this.value = (float)((double)var1 - (this.getProxy().xPosition + 4.0D)) / (float)(this.getProxy().getButtonWidth() - 8);
            this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
            float var3 = this.toValue(this.value);
            this.clicked(var3);
            this.value = this.toPct(var3);
            this.getProxy().displayString = this.getMessage();
         }

         Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit((int)this.getProxy().xPosition + (int)(this.value * (float)(this.getProxy().getButtonWidth() - 8)), (int)this.getProxy().yPosition, 0, 66, 4, 20);
         this.blit((int)this.getProxy().xPosition + (int)(this.value * (float)(this.getProxy().getButtonWidth() - 8)) + 4, (int)this.getProxy().yPosition, 196, 66, 4, 20);
      }

   }

   public void clicked(int var1, int var2) {
      this.value = (float)((double)var1 - (this.getProxy().xPosition + 4.0D)) / (float)(this.getProxy().getButtonWidth() - 8);
      this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
      this.clicked(this.toValue(this.value));
      this.getProxy().displayString = this.getMessage();
      this.sliding = true;
   }

   public void clicked(float var1) {
   }

   public void released(int var1, int var2) {
      this.sliding = false;
   }
}
