package net;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSelectWorld;

public class apG {
   public static void a(GuiSelectWorld var0, int var1) {
      var0.func_146615_e(var1);
   }

   public static void a(GuiSelectWorld var0) {
      var0.drawDefaultBackground();
   }

   public static void a(GuiSelectWorld var0, FontRenderer var1, String var2, int var3, int var4, int var5) {
      var0.drawString(var1, var2, var3, var4, var5);
   }
}
