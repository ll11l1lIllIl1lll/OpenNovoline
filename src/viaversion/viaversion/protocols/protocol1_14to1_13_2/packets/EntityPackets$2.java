package viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import net.aSG;
import net.aTM;
import net.acE;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.version.Types1_13_2;

final class EntityPackets$2 extends acE {
   final aTM c;

   EntityPackets$2(aTM var1) {
      this.c = var1;
   }

   public void registerMap() {
      this.a(Type.VAR_INT);
      this.a(Type.UUID);
      this.a(Type.VAR_INT);
      this.a(Type.DOUBLE);
      this.a(Type.DOUBLE);
      this.a(Type.DOUBLE);
      this.a(Type.BYTE);
      this.a(Type.BYTE);
      this.a(Type.BYTE);
      this.a(Type.SHORT);
      this.a(Type.SHORT);
      this.a(Type.SHORT);
      this.a(Types1_13_2.METADATA_LIST, aSG.c);
      this.a(this.c.getTrackerAndRewriter(aSG.c));
   }
}
