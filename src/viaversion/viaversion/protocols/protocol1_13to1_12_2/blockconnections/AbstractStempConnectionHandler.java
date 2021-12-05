package viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.abi;
import net.acE;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.BlockFace;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData$ConnectorInitAction;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.WrappedBlockData;

public abstract class AbstractStempConnectionHandler extends abi {
   private static final BlockFace[] BLOCK_FACES = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST};
   private final int baseStateId;
   private final Set blockId = new HashSet();
   private final Map stemps = new HashMap();

   protected AbstractStempConnectionHandler(String var1) {
      this.baseStateId = ConnectionData.getId(var1);
   }

   public ConnectionData$ConnectorInitAction getInitAction(String var1, String var2) {
      return this::lambda$getInitAction$0;
   }

   public int connect(UserConnection var1, Position var2, int var3) {
      acE[] var4 = abi.b();
      if(var3 != this.baseStateId) {
         return var3;
      } else {
         BlockFace[] var5 = BLOCK_FACES;
         int var6 = var5.length;
         int var7 = 0;
         if(var7 < var6) {
            BlockFace var8 = var5[var7];
            if(this.blockId.contains(Integer.valueOf(this.getBlockData(var1, var2.getRelative(var8))))) {
               return ((Integer)this.stemps.get(var8)).intValue();
            }

            ++var7;
         }

         return this.baseStateId;
      }
   }

   private void lambda$getInitAction$0(String var1, AbstractStempConnectionHandler var2, String var3, WrappedBlockData var4) {
      acE[] var5 = abi.b();
      if(var4.getSavedBlockStateId() == this.baseStateId || var1.equals(var4.getMinecraftKey())) {
         if(var4.getSavedBlockStateId() != this.baseStateId) {
            var2.blockId.add(Integer.valueOf(var4.getSavedBlockStateId()));
         }

         ConnectionData.connectionHandlerMap.put(var4.getSavedBlockStateId(), var2);
      }

      if(var4.getMinecraftKey().equals(var3)) {
         String var6 = var4.getValue("facing").toUpperCase(Locale.ROOT);
         this.stemps.put(BlockFace.valueOf(var6), Integer.valueOf(var4.getSavedBlockStateId()));
      }

   }
}
