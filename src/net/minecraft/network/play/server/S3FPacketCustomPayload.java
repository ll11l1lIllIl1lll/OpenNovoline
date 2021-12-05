package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet {
   private String channel;
   private PacketBuffer data;

   public S3FPacketCustomPayload() {
   }

   public S3FPacketCustomPayload(String var1, PacketBuffer var2) {
      this.channel = var1;
      this.data = var2;
      if(var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.channel = var1.a(20);
      int var2 = var1.readableBytes();
      if(var2 <= 1048576) {
         this.data = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.channel);
      var1.writeBytes((ByteBuf)this.data);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleCustomPayload(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }

   private static IOException a(IOException var0) {
      return var0;
   }
}
