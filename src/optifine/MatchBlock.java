package optifine;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import net.minecraft.block.state.BlockStateBase;
import optifine.Config;
import optifine.Matches;

public class MatchBlock {
   private int blockId;
   private int[] metadatas;
   private static PacketRemapper[] b;

   public MatchBlock(int var1) {
      this.blockId = -1;
      this.metadatas = null;
      this.blockId = var1;
   }

   public MatchBlock(int var1, int var2) {
      b();
      super();
      this.blockId = -1;
      this.metadatas = null;
      this.blockId = var1;
      if(var2 >= 0 && var2 <= 15) {
         this.metadatas = new int[]{var2};
      }

   }

   public MatchBlock(int var1, int[] var2) {
      this.blockId = -1;
      this.metadatas = null;
      this.blockId = var1;
      this.metadatas = var2;
   }

   public int getBlockId() {
      return this.blockId;
   }

   public int[] getMetadatas() {
      return this.metadatas;
   }

   public boolean a(BlockStateBase var1) {
      PacketRemapper[] var2 = b();
      return var1.getBlockId() != this.blockId?false:Matches.a(var1.getMetadata(), this.metadatas);
   }

   public boolean matches(int var1, int var2) {
      PacketRemapper[] var3 = b();
      return var1 != this.blockId?false:Matches.a(var2, this.metadatas);
   }

   public void addMetadata(int var1) {
      PacketRemapper[] var2 = b();
      if(this.metadatas != null && var1 >= 0 && var1 <= 15) {
         int var3 = 0;
         if(var3 < this.metadatas.length) {
            if(this.metadatas[var3] == var1) {
               return;
            }

            ++var3;
         }

         this.metadatas = Config.addIntToArray(this.metadatas, var1);
      }

   }

   public String toString() {
      return "" + this.blockId + ":" + Config.a(this.metadatas);
   }

   public static void b(PacketRemapper[] var0) {
      b = var0;
   }

   public static PacketRemapper[] b() {
      return b;
   }

   static {
      if(b() != null) {
         b(new PacketRemapper[1]);
      }

   }
}
