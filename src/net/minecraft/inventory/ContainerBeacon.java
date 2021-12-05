package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon$BeaconSlot;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   private IInventory tileBeacon;
   private final ContainerBeacon$BeaconSlot beaconSlot;

   public ContainerBeacon(IInventory var1, IInventory var2) {
      this.tileBeacon = var2;
      this.addSlotToContainer(this.beaconSlot = new ContainerBeacon$BeaconSlot(this, var2, 0, 136, 110));
      byte var3 = 36;
      short var4 = 137;

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(var1, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlotToContainer(new Slot(var1, var7, var3 + var7 * 18, 58 + var4));
      }

   }

   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
      var1.func_175173_a(this, this.tileBeacon);
   }

   public void updateProgressBar(int var1, int var2) {
      this.tileBeacon.setField(var1, var2);
   }

   public IInventory func_180611_e() {
      return this.tileBeacon;
   }

   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      if(!var1.worldObj.isRemote) {
         ItemStack var2 = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         var1.dropPlayerItemWithRandomChoice(var2, false);
      }

   }

   public boolean canInteractWith(EntityPlayer var1) {
      return this.tileBeacon.isUseableByPlayer(var1);
   }

   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(var2);
      if(var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if(!this.mergeItemStack(var5, 1, 37, true)) {
            return null;
         }

         var4.onSlotChange(var5, var3);
         if(var5.stackSize == 0) {
            var4.putStack((ItemStack)null);
         } else {
            var4.onSlotChanged();
         }

         if(var5.stackSize == var3.stackSize) {
            return null;
         }

         var4.onPickupFromSlot(var1, var5);
      }

      return var3;
   }
}
