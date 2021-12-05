package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate {
   private final BlockState blockstate;
   private final Map propertyPredicates = Maps.newHashMap();

   private BlockStateHelper(BlockState var1) {
      this.blockstate = var1;
   }

   public static BlockStateHelper forBlock(Block var0) {
      return new BlockStateHelper(var0.getBlockState());
   }

   public boolean apply(IBlockState var1) {
      if(var1.getBlock().equals(this.blockstate.getBlock())) {
         for(Entry var3 : this.propertyPredicates.entrySet()) {
            Comparable var4 = var1.getValue((IProperty)var3.getKey());
            if(!((Predicate)var3.getValue()).apply(var4)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public BlockStateHelper where(IProperty var1, Predicate var2) {
      if(!this.blockstate.getProperties().contains(var1)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + var1);
      } else {
         this.propertyPredicates.put(var1, var2);
         return this;
      }
   }

   private static IllegalArgumentException a(IllegalArgumentException var0) {
      return var0;
   }
}
