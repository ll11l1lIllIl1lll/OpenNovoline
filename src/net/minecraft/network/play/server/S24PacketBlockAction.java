package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S24PacketBlockAction implements Packet {
   private BlockPos blockPosition;
   private int instrument;
   private int pitch;
   private Block block;

   public S24PacketBlockAction() {
   }

   public S24PacketBlockAction(BlockPos var1, Block var2, int var3, int var4) {
      this.blockPosition = var1;
      this.instrument = var3;
      this.pitch = var4;
      this.block = var2;
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.blockPosition = var1.readBlockPos();
      this.instrument = var1.readUnsignedByte();
      this.pitch = var1.readUnsignedByte();
      this.block = Block.getBlockById(var1.readVarIntFromBuffer() & 4095);
   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeBlockPos(this.blockPosition);
      var1.writeByte(this.instrument);
      var1.writeByte(this.pitch);
      var1.writeVarIntToBuffer(Block.getIdFromBlock(this.block) & 4095);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleBlockAction(this);
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public int getData1() {
      return this.instrument;
   }

   public int getData2() {
      return this.pitch;
   }

   public Block getBlockType() {
      return this.block;
   }
}
