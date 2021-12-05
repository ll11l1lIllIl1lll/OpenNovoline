package com.viaversion.viabackwards.protocol.protocol1_9_4to1_10;

import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;

final class Protocol1_9_4To1_10$1 extends ValueTransformer {
   Protocol1_9_4To1_10$1(Type var1) {
      super(var1);
   }

   public Short a(PacketWrapperImpl var1, Float var2) throws Exception {
      return Short.valueOf((short)Math.round(var2.floatValue() * 63.5F));
   }
}
