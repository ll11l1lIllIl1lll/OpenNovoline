package viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import net.acE;
import viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.PlayerPackets1_14;
import viaversion.viaversion.api.type.Type;

class PlayerPackets1_14$2 extends acE {
   final PlayerPackets1_14 this$0;

   PlayerPackets1_14$2(PlayerPackets1_14 var1) {
      this.this$0 = var1;
   }

   public void registerMap() {
      this.a(Type.POSITION1_14, Type.POSITION);
   }
}
