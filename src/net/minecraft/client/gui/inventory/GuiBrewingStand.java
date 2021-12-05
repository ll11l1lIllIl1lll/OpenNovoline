package net.minecraft.client.gui.inventory;

import net.aHz;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBrewingStand extends aHz {
   private static final ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private final InventoryPlayer playerInventory;
   private IInventory tileBrewingStand;

   public GuiBrewingStand(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBrewingStand(var1, var2));
      this.playerInventory = var1;
      this.tileBrewingStand = var2;
   }

   protected void drawGuiContainerForegroundLayer(int var1, int var2) {
      String var3 = this.tileBrewingStand.getDisplayName().getUnformattedText();
      this.fontRendererObj.drawString(var3, (float)(this.y / 2 - this.fontRendererObj.d(var3) / 2), 6.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, (float)(this.ab - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
      int var4 = (this.width - this.y) / 2;
      int var5 = (this.height - this.ab) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.y, this.ab);
      int var6 = this.tileBrewingStand.getField(0);
      int var7 = (int)(28.0F * (1.0F - (float)var6 / 400.0F));
      this.drawTexturedModalRect(var4 + 97, var5 + 16, 176, 0, 9, var7);
      int var8 = var6 / 2 % 7;
      switch(var8) {
      case 0:
         var7 = 29;
         break;
      case 1:
         var7 = 24;
         break;
      case 2:
         var7 = 20;
         break;
      case 3:
         var7 = 16;
         break;
      case 4:
         var7 = 11;
         break;
      case 5:
         var7 = 6;
         break;
      case 6:
         var7 = 0;
      }

      this.drawTexturedModalRect(var4 + 65, var5 + 14 + 29 - var7, 185, 29 - var7, 12, var7);
   }
}
