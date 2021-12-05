package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended$IGuiListEntry;
import net.minecraft.client.gui.GuiSlot;

public abstract class GuiListExtended extends GuiSlot {
   public GuiListExtended(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected void elementClicked(int var1, boolean var2, int var3, int var4) {
   }

   protected boolean isSelected(int var1) {
      return false;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.getListEntry(var1).drawEntry(var1, var2, var3, this.getListWidth(), var4, var5, var6, this.getSlotIndexFromScreenCoords(var5, var6) == var1);
   }

   protected void func_178040_a(int var1, int var2, int var3) {
      this.getListEntry(var1).setSelected(var1, var2, var3);
   }

   public boolean mouseClicked(int var1, int var2, int var3) {
      if(this.isMouseYWithinSlotBounds(var2)) {
         int var4 = this.getSlotIndexFromScreenCoords(var1, var2);
         int var5 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int var6 = this.top + 4 - this.getAmountScrolled() + var4 * this.slotHeight + this.headerPadding;
         int var7 = var1 - var5;
         int var8 = var2 - var6;
         if(this.getListEntry(var4).mousePressed(var4, var1, var2, var3, var7, var8)) {
            this.setEnabled(false);
            return true;
         }
      }

      return false;
   }

   public boolean mouseReleased(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.getSize(); ++var4) {
         int var5 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int var6 = this.top + 4 - this.getAmountScrolled() + var4 * this.slotHeight + this.headerPadding;
         int var7 = var1 - var5;
         int var8 = var2 - var6;
         this.getListEntry(var4).mouseReleased(var4, var1, var2, var3, var7, var8);
      }

      this.setEnabled(true);
      return false;
   }

   public abstract GuiListExtended$IGuiListEntry getListEntry(int var1);
}
