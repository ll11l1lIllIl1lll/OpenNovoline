package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {
   public boolean matches(InventoryCrafting var1, World var2) {
      int var3 = 0;
      Object var4 = null;

      for(int var5 = 0; var5 < var1.getSizeInventory(); ++var5) {
         ItemStack var6 = var1.getStackInSlot(var5);
         if(var6.getItem() == Items.filled_map) {
            return false;
         }

         if(var6.getItem() != Items.map) {
            return false;
         }

         ++var3;
      }

      return true;
   }

   public ItemStack getCraftingResult(InventoryCrafting var1) {
      int var2 = 0;
      Object var3 = null;

      for(int var4 = 0; var4 < var1.getSizeInventory(); ++var4) {
         ItemStack var5 = var1.getStackInSlot(var4);
         if(var5.getItem() == Items.filled_map) {
            return null;
         }

         if(var5.getItem() != Items.map) {
            return null;
         }

         ++var2;
      }

      if(var2 >= 1) {
         ItemStack var6 = new ItemStack(Items.filled_map, var2 + 1, ((ItemStack)var3).getMetadata());
         if(((ItemStack)var3).hasDisplayName()) {
            var6.setStackDisplayName(((ItemStack)var3).getDisplayName());
         }

         return var6;
      } else {
         return null;
      }
   }

   public int getRecipeSize() {
      return 9;
   }

   public ItemStack getRecipeOutput() {
      return null;
   }

   public ItemStack[] getRemainingItems(InventoryCrafting var1) {
      ItemStack[] var2 = new ItemStack[var1.getSizeInventory()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = var1.getStackInSlot(var3);
         if(var4.getItem().hasContainerItem()) {
            var2[var3] = new ItemStack(var4.getItem().getContainerItem());
         }
      }

      return var2;
   }
}
