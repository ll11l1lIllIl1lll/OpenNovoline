package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import viaversion.viaversion.api.minecraft.BlockFace;

// $FF: synthetic class
class StairConnectionHandler$1 {
   static final int[] $SwitchMap$viaversion$viaversion$api$minecraft$BlockFace = new int[BlockFace.values().length];

   static {
      try {
         $SwitchMap$viaversion$viaversion$api$minecraft$BlockFace[BlockFace.NORTH.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
         ;
      }

      try {
         $SwitchMap$viaversion$viaversion$api$minecraft$BlockFace[BlockFace.SOUTH.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
         ;
      }

      try {
         $SwitchMap$viaversion$viaversion$api$minecraft$BlockFace[BlockFace.EAST.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
         ;
      }

      try {
         $SwitchMap$viaversion$viaversion$api$minecraft$BlockFace[BlockFace.WEST.ordinal()] = 4;
      } catch (NoSuchFieldError var1) {
         ;
      }

   }
}
