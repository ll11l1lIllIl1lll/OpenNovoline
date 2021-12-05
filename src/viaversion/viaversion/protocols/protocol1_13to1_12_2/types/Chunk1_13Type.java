package viaversion.viaversion.protocols.protocol1_13to1_12_2.types;

import cc.novoline.modules.PlayerManager;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import net.aEY;
import net.acE;
import net.cT;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.minecraft.Environment;
import viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import viaversion.viaversion.api.minecraft.chunks.Chunk;
import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.type.PartialType;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.minecraft.BaseChunkType;

public class Chunk1_13Type extends PartialType {
   public Chunk1_13Type(cT var1) {
      super(var1, "Chunk", Chunk.class);
   }

   public Chunk a(ByteBuf var1, cT var2) throws Exception {
      int var4 = var1.readInt();
      int var5 = var1.readInt();
      PlayerManager.b();
      boolean var6 = var1.readBoolean();
      int var7 = Type.VAR_INT.readPrimitive(var1);
      ByteBuf var8 = var1.readSlice(Type.VAR_INT.readPrimitive(var1));
      ChunkSection[] var9 = new ChunkSection[16];
      int var10 = 0;
      if(var10 < 16) {
         if((var7 & 1 << var10) != 0) {
            ChunkSection var11 = (ChunkSection)aEY.c.read(var8);
            var9[var10] = var11;
            var11.readBlockLight(var8);
            if(var2.a() == Environment.NORMAL) {
               var11.readSkyLight(var8);
            }
         }

         ++var10;
      }

      int[] var14 = var6?new int[256]:null;
      if(var6) {
         if(var8.readableBytes() >= 1024) {
            int var15 = 0;
            if(var15 < 256) {
               var14[var15] = var8.readInt();
               ++var15;
            }
         }

         Via.getPlatform().getLogger().log(Level.WARNING, "Chunk x=" + var4 + " z=" + var5 + " doesn\'t have biome data!");
      }

      ArrayList var17 = new ArrayList(Arrays.asList((Object[])Type.NBT_ARRAY.read(var1)));
      if(var1.readableBytes() > 0) {
         byte[] var12 = (byte[])Type.REMAINING_BYTES.read(var1);
         if(Via.getManager().isDebug()) {
            Via.getPlatform().getLogger().warning("Found " + var12.length + " more bytes than expected while reading the chunk: " + var4 + "/" + var5);
         }
      }

      BaseChunk var10000 = new BaseChunk(var4, var5, var6, false, var7, var9, var14, var17);
      if(acE.b() == null) {
         PlayerManager.b(new acE[5]);
      }

      return var10000;
   }

   public void a(ByteBuf param1, cT param2, Chunk param3) throws Exception {
      // $FF: Couldn't be decompiled
   }

   public Class getBaseClass() {
      return BaseChunkType.class;
   }

   private static Exception a(Exception var0) {
      return var0;
   }
}
