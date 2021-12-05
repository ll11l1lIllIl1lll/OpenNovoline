package net.minecraft.entity.ai.attributes;

import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.MathHelper;

public class RangedAttribute extends BaseAttribute {
   private final double minimumValue;
   private final double maximumValue;
   private String description;

   public RangedAttribute(IAttribute var1, String var2, double var3, double var5, double var7) {
      super(var1, var2, var3);
      this.minimumValue = var5;
      this.maximumValue = var7;
      if(var5 > var7) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if(var3 < var5) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if(var3 > var7) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute setDescription(String var1) {
      this.description = var1;
      return this;
   }

   public String getDescription() {
      return this.description;
   }

   public double clampValue(double var1) {
      var1 = MathHelper.clamp_double(var1, this.minimumValue, this.maximumValue);
      return var1;
   }

   private static IllegalArgumentException b(IllegalArgumentException var0) {
      return var0;
   }
}
