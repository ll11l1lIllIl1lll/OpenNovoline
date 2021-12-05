package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class RecipesWeapons {
   private String[][] recipePatterns = new String[][]{{"X", "X", "#"}};
   private Object[][] recipeItems = new Object[][]{{Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot}, {Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword}};

   public void addRecipes(CraftingManager var1) {
      for(int var2 = 0; var2 < this.recipeItems[0].length; ++var2) {
         Object var3 = this.recipeItems[0][var2];

         for(int var4 = 0; var4 < this.recipeItems.length - 1; ++var4) {
            Item var5 = (Item)this.recipeItems[var4 + 1][var2];
            var1.addRecipe(new ItemStack(var5), new Object[]{this.recipePatterns[var4], Character.valueOf('#'), Items.stick, Character.valueOf('X'), var3});
         }
      }

      var1.addRecipe(new ItemStack(Items.bow, 1), new Object[]{" #X", "# X", " #X", Character.valueOf('X'), Items.string, Character.valueOf('#'), Items.stick});
      var1.addRecipe(new ItemStack(Items.arrow, 4), new Object[]{"X", "#", "Y", Character.valueOf('Y'), Items.feather, Character.valueOf('X'), Items.flint, Character.valueOf('#'), Items.stick});
   }
}
