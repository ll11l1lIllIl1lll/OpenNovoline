package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience implements Packet {
   private float field_149401_a;
   private int totalExperience;
   private int level;

   public S1FPacketSetExperience() {
   }

   public S1FPacketSetExperience(float var1, int var2, int var3) {
      this.field_149401_a = var1;
      this.totalExperience = var2;
      this.level = var3;
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.field_149401_a = var1.readFloat();
      this.level = var1.readVarIntFromBuffer();
      this.totalExperience = var1.readVarIntFromBuffer();
   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeFloat(this.field_149401_a);
      var1.writeVarIntToBuffer(this.level);
      var1.writeVarIntToBuffer(this.totalExperience);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleSetExperience(this);
   }

   public float func_149397_c() {
      return this.field_149401_a;
   }

   public int getTotalExperience() {
      return this.totalExperience;
   }

   public int getLevel() {
      return this.level;
   }
}
