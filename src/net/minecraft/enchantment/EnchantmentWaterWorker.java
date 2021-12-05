package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("waterWorker");
   }

   public int getMinEnchantability(int var1) {
      return 1;
   }

   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + 40;
   }

   public int getMaxLevel() {
      return 1;
   }
}
