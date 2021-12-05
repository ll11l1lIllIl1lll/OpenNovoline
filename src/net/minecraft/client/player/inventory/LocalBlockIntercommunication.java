package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject {
   private String guiID;
   private IChatComponent displayName;

   public LocalBlockIntercommunication(String var1, IChatComponent var2) {
      this.guiID = var1;
      this.displayName = var2;
   }

   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      throw new UnsupportedOperationException();
   }

   public String getName() {
      return this.displayName.getUnformattedText();
   }

   public boolean hasCustomName() {
      return true;
   }

   public String getGuiID() {
      return this.guiID;
   }

   public IChatComponent getDisplayName() {
      return this.displayName;
   }
}
