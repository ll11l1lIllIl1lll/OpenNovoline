package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage implements Packet {
   private String message;

   public C01PacketChatMessage() {
   }

   public C01PacketChatMessage(String var1) {
      if(var1.length() > 100) {
         var1 = var1.substring(0, 100);
      }

      this.message = var1;
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.message = var1.a(100);
   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.message);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processChatMessage(this);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }
}
