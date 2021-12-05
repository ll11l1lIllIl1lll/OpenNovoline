package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemSnowball extends Item {
   public ItemSnowball() {
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if(!var3.abilities.isCreative()) {
         --var1.stackSize;
      }

      var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if(!var2.isRemote) {
         var2.spawnEntityInWorld(new EntitySnowball(var2, var3));
      }

      var3.triggerAchievement(StatList.objectUseStats[Item.b(this)]);
      return var1;
   }
}
