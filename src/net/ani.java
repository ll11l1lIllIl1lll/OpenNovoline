package net;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import net.acE;
import net.aqX;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.type.Type;

class ani extends acE {
   final aqX c;

   ani(aqX var1) {
      this.c = var1;
   }

   public void registerMap() {
      this.a(this::lambda$registerMap$0);
   }

   private void lambda$registerMap$0(PacketWrapper var1) throws Exception {
      Position var2 = (Position)var1.passthrough(Type.POSITION1_14);
      short var3 = ((Short)var1.passthrough(Type.UNSIGNED_BYTE)).shortValue();
      CompoundTag var4 = (CompoundTag)var1.passthrough(Type.NBT);
      aqX.a(this.c, var4);
   }
}
