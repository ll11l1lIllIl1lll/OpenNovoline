package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
   private String name;
   private ILockableContainer upperChest;
   private ILockableContainer lowerChest;

   public InventoryLargeChest(String var1, ILockableContainer var2, ILockableContainer var3) {
      this.name = var1;
      this.upperChest = var3;
      this.lowerChest = var3;
      if(var3.isLocked()) {
         var3.setLockCode(var3.getLockCode());
      } else if(var3.isLocked()) {
         var3.setLockCode(var3.getLockCode());
      }

   }

   public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
   }

   public boolean isPartOfLargeChest(IInventory var1) {
      return this.upperChest == var1 || this.lowerChest == var1;
   }

   public String getName() {
      return this.upperChest.hasCustomName()?this.upperChest.getName():(this.lowerChest.hasCustomName()?this.lowerChest.getName():this.name);
   }

   public boolean hasCustomName() {
      return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
   }

   public IChatComponent getDisplayName() {
      return (IChatComponent)(this.hasCustomName()?new ChatComponentText(this.getName()):new ChatComponentTranslation(this.getName(), new Object[0]));
   }

   public ItemStack getStackInSlot(int var1) {
      return var1 >= this.upperChest.getSizeInventory()?this.lowerChest.getStackInSlot(var1 - this.upperChest.getSizeInventory()):this.upperChest.getStackInSlot(var1);
   }

   public ItemStack decrStackSize(int var1, int var2) {
      return var1 >= this.upperChest.getSizeInventory()?this.lowerChest.decrStackSize(var1 - this.upperChest.getSizeInventory(), var2):this.upperChest.decrStackSize(var1, var2);
   }

   public ItemStack removeStackFromSlot(int var1) {
      return var1 >= this.upperChest.getSizeInventory()?this.lowerChest.removeStackFromSlot(var1 - this.upperChest.getSizeInventory()):this.upperChest.removeStackFromSlot(var1);
   }

   public void setInventorySlotContents(int var1, ItemStack var2) {
      if(var1 >= this.upperChest.getSizeInventory()) {
         this.lowerChest.setInventorySlotContents(var1 - this.upperChest.getSizeInventory(), var2);
      } else {
         this.upperChest.setInventorySlotContents(var1, var2);
      }

   }

   public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
   }

   public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
   }

   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.upperChest.isUseableByPlayer(var1) && this.lowerChest.isUseableByPlayer(var1);
   }

   public void openInventory(EntityPlayer var1) {
      this.upperChest.openInventory(var1);
      this.lowerChest.openInventory(var1);
   }

   public void closeInventory(EntityPlayer var1) {
      this.upperChest.closeInventory(var1);
      this.lowerChest.closeInventory(var1);
   }

   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }

   public int getField(int var1) {
      return 0;
   }

   public void setField(int var1, int var2) {
   }

   public int getFieldCount() {
      return 0;
   }

   public boolean isLocked() {
      return this.upperChest.isLocked() || this.lowerChest.isLocked();
   }

   public void setLockCode(LockCode var1) {
      this.upperChest.setLockCode(var1);
      this.lowerChest.setLockCode(var1);
   }

   public LockCode getLockCode() {
      return this.upperChest.getLockCode();
   }

   public String getGuiID() {
      return this.upperChest.getGuiID();
   }

   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }

   public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
   }
}
