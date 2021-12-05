package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityHeadLook implements Packet {
   private int entityId;
   private byte yaw;

   public S19PacketEntityHeadLook() {
   }

   public S19PacketEntityHeadLook(Entity var1, byte var2) {
      this.entityId = var1.getEntityID();
      this.yaw = var2;
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityId = var1.readVarIntFromBuffer();
      this.yaw = var1.readByte();
   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityId);
      var1.writeByte(this.yaw);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleEntityHeadLook(this);
   }

   public Entity getEntity(World var1) {
      return var1.getEntityByID(this.entityId);
   }

   public byte getYaw() {
      return this.yaw;
   }
}
