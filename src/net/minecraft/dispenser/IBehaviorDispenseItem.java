package net.minecraft.dispenser;

import net.minecraft.dispenser.IBehaviorDispenseItem$1;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {
   IBehaviorDispenseItem a = new IBehaviorDispenseItem$1();

   ItemStack dispense(IBlockSource var1, ItemStack var2);
}
