package net;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import net.EN;
import net.aLW;
import net.aqS;
import net.aqu;

class acu extends PacketRemapper {
   final aqS c;

   acu(aqS var1) {
      this.c = var1;
   }

   public void registerMap() {
      this.a(Type.VAR_INT_ARRAY_PRIMITIVE);
      this.a(this::lambda$registerMap$0);
   }

   private void lambda$registerMap$0(PacketWrapperImpl var1) throws Exception {
      aqu.d();
      aLW var3 = this.c.a(var1.e());
      int[] var4 = (int[])var1.b(Type.VAR_INT_ARRAY_PRIMITIVE, 0);
      int var5 = var4.length;
      int var6 = 0;
      if(var6 < var5) {
         int var7 = var4[var6];
         var3.b(var7);
         ++var6;
      }

   }
}
