package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   private GuiScreen parentScreen;
   private GuiTextField field_146583_f;
   private final String saveName;

   public GuiRenameWorld(GuiScreen var1, String var2) {
      this.parentScreen = var1;
      this.saveName = var2;
   }

   public void updateScreen() {
      this.field_146583_f.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton", new Object[0])));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
      ISaveFormat var1 = this.mc.getSaveLoader();
      WorldInfo var2 = var1.getWorldInfo(this.saveName);
      String var3 = var2.getWorldName();
      this.field_146583_f = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.field_146583_f.setFocused(true);
      this.field_146583_f.setText(var3);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) throws IOException {
      if(var1.enabled) {
         if(var1.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if(var1.id == 0) {
            ISaveFormat var2 = this.mc.getSaveLoader();
            var2.renameWorld(this.saveName, this.field_146583_f.getText().trim());
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }

   }

   protected void keyTyped(char var1, int var2) throws IOException {
      this.field_146583_f.textboxKeyTyped(var1, var2);
      ((GuiButton)this.buttonList.get(0)).enabled = !this.field_146583_f.getText().trim().isEmpty();
      if(var2 == 28 || var2 == 156) {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

   }

   protected void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.field_146583_f.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle", new Object[0]), this.width / 2, 20, 16777215);
      this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 47, 10526880);
      this.field_146583_f.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }

   private static IOException a(IOException var0) {
      return var0;
   }
}
