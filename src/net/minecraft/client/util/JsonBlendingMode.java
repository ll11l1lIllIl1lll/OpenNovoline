package net.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {
   private static JsonBlendingMode field_148118_a = null;
   private final int field_148116_b;
   private final int field_148117_c;
   private final int field_148114_d;
   private final int field_148115_e;
   private final int field_148112_f;
   private final boolean field_148113_g;
   private final boolean field_148119_h;

   private JsonBlendingMode(boolean var1, boolean var2, int var3, int var4, int var5, int var6, int var7) {
      this.field_148113_g = var1;
      this.field_148116_b = var3;
      this.field_148114_d = var4;
      this.field_148117_c = var5;
      this.field_148115_e = var6;
      this.field_148119_h = var2;
      this.field_148112_f = var7;
   }

   public JsonBlendingMode() {
      this(false, true, 1, 0, 1, 0, '耆');
   }

   public JsonBlendingMode(int var1, int var2, int var3) {
      this(false, false, var1, var2, var1, var2, var3);
   }

   public JsonBlendingMode(int var1, int var2, int var3, int var4, int var5) {
      this(true, false, var1, var2, var3, var4, var5);
   }

   public void func_148109_a() {
      if(!this.equals(field_148118_a)) {
         if(field_148118_a == null || this.field_148119_h != field_148118_a.func_148111_b()) {
            field_148118_a = this;
            if(this.field_148119_h) {
               GlStateManager.disableBlend();
               return;
            }

            GlStateManager.enableBlend();
         }

         GL14.glBlendEquation(this.field_148112_f);
         if(this.field_148113_g) {
            GlStateManager.tryBlendFuncSeparate(this.field_148116_b, this.field_148114_d, this.field_148117_c, this.field_148115_e);
         } else {
            GlStateManager.blendFunc(this.field_148116_b, this.field_148114_d);
         }
      }

   }

   public boolean equals(Object var1) {
      if(this == var1) {
         return true;
      } else if(!(var1 instanceof JsonBlendingMode)) {
         return false;
      } else {
         JsonBlendingMode var2 = (JsonBlendingMode)var1;
         return this.field_148112_f == var2.field_148112_f && this.field_148115_e == var2.field_148115_e && this.field_148114_d == var2.field_148114_d && this.field_148119_h == var2.field_148119_h && this.field_148113_g == var2.field_148113_g && this.field_148117_c == var2.field_148117_c && this.field_148116_b == var2.field_148116_b;
      }
   }

   public int hashCode() {
      int var1 = this.field_148116_b;
      var1 = 31 * var1 + this.field_148117_c;
      var1 = 31 * var1 + this.field_148114_d;
      var1 = 31 * var1 + this.field_148115_e;
      var1 = 31 * var1 + this.field_148112_f;
      var1 = 31 * var1 + (this.field_148113_g?1:0);
      var1 = 31 * var1 + (this.field_148119_h?1:0);
      return var1;
   }

   public boolean func_148111_b() {
      return this.field_148119_h;
   }

   public static JsonBlendingMode a(JsonObject var0) {
      return new JsonBlendingMode();
   }

   private static int func_148108_a(String var0) {
      String var1 = var0.trim().toLowerCase();
      return var1.equals("add")?'耆':(var1.equals("subtract")?'耊':(var1.equals("reversesubtract")?'耋':(var1.equals("reverse_subtract")?'耋':(var1.equals("min")?'耇':(var1.equals("max")?'耈':'耆')))));
   }

   private static int func_148107_b(String var0) {
      String var1 = var0.trim().toLowerCase();
      var1 = var1.replaceAll("_", "");
      var1 = var1.replaceAll("one", "1");
      var1 = var1.replaceAll("zero", "0");
      var1 = var1.replaceAll("minus", "-");
      return var1.equals("0")?0:(var1.equals("1")?1:(var1.equals("srccolor")?768:(var1.equals("1-srccolor")?769:(var1.equals("dstcolor")?774:(var1.equals("1-dstcolor")?775:(var1.equals("srcalpha")?770:(var1.equals("1-srcalpha")?771:(var1.equals("dstalpha")?772:(var1.equals("1-dstalpha")?773:-1)))))))));
   }
}
