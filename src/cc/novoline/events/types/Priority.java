package cc.novoline.events.types;

public final class Priority {
   public static final byte HIGHEST = 0;
   public static final byte HIGH = 1;
   public static final byte MEDIUM = 2;
   public static final byte LOW = 3;
   public static final byte LOWEST = 4;
   public static final byte[] VALUE_ARRAY = new byte[]{(byte)0, (byte)1, (byte)2, (byte)3, (byte)4};
   private static int[] e;

   static {
      int[] var10000 = new int[4];
      b(var10000);
   }

   public static void b(int[] var0) {
      e = var0;
   }

   public static int[] b() {
      return e;
   }
}
