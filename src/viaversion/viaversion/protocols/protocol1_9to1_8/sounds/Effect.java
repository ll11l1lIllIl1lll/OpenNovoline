package viaversion.viaversion.protocols.protocol1_9to1_8.sounds;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class Effect {
   private static final Int2IntMap EFFECTS = new Int2IntOpenHashMap(19, 1.0F);

   public static int getNewId(int var0) {
      return EFFECTS.getOrDefault(var0, var0);
   }

   public static boolean contains(int var0) {
      return EFFECTS.containsKey(var0);
   }

   private static void addRewrite(int var0, int var1) {
      EFFECTS.put(var0, var1);
   }

   static {
      addRewrite(1005, 1010);
      addRewrite(1003, 1005);
      addRewrite(1006, 1011);
      addRewrite(1004, 1009);
      addRewrite(1007, 1015);
      addRewrite(1008, 1016);
      addRewrite(1009, 1016);
      addRewrite(1010, 1019);
      addRewrite(1011, 1020);
      addRewrite(1012, 1021);
      addRewrite(1014, 1024);
      addRewrite(1015, 1025);
      addRewrite(1016, 1026);
      addRewrite(1017, 1027);
      addRewrite(1020, 1029);
      addRewrite(1021, 1030);
      addRewrite(1022, 1031);
      addRewrite(1013, 1023);
      addRewrite(1018, 1028);
   }
}
