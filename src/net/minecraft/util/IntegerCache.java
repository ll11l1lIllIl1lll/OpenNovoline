package net.minecraft.util;

public class IntegerCache {
   private static final Integer[] field_181757_a = new Integer['\uffff'];

   public static Integer func_181756_a(int var0) {
      return var0 < field_181757_a.length?field_181757_a[var0]:Integer.valueOf(var0);
   }

   static {
      int var0 = 0;

      for(int var1 = field_181757_a.length; var0 < var1; ++var0) {
         field_181757_a[var0] = Integer.valueOf(var0);
      }

   }
}
