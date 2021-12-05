package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
   private static final Joiner COMMA_JOIN = Joiner.on(",");
   private final List depth = Lists.newArrayList();
   private final Map symbolMap = Maps.newHashMap();
   private int aisleHeight;
   private int rowWidth;

   private FactoryBlockPattern() {
      this.symbolMap.put(Character.valueOf(' '), Predicates.alwaysTrue());
   }

   public FactoryBlockPattern aisle(String... var1) {
      if(!ArrayUtils.isEmpty(var1) && !StringUtils.isEmpty(var1[0])) {
         if(this.depth.isEmpty()) {
            this.aisleHeight = var1.length;
            this.rowWidth = var1[0].length();
         }

         if(var1.length != this.aisleHeight) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + var1.length + ")");
         } else {
            for(String var5 : var1) {
               if(var5.length() != this.rowWidth) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + var5.length() + ")");
               }

               for(char var9 : var5.toCharArray()) {
                  if(!this.symbolMap.containsKey(Character.valueOf(var9))) {
                     this.symbolMap.put(Character.valueOf(var9), (Object)null);
                  }
               }
            }

            this.depth.add(var1);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static FactoryBlockPattern start() {
      return new FactoryBlockPattern();
   }

   public FactoryBlockPattern where(char var1, Predicate var2) {
      this.symbolMap.put(Character.valueOf(var1), var2);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   private Predicate[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate[][][] var1 = (Predicate[][][])((Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.depth.size(), this.aisleHeight, this.rowWidth}));

      for(int var2 = 0; var2 < this.depth.size(); ++var2) {
         for(int var3 = 0; var3 < this.aisleHeight; ++var3) {
            for(int var4 = 0; var4 < this.rowWidth; ++var4) {
               var1[var2][var3][var4] = (Predicate)this.symbolMap.get(Character.valueOf(((String[])this.depth.get(var2))[var3].charAt(var4)));
            }
         }
      }

      return var1;
   }

   private void checkMissingPredicates() {
      ArrayList var1 = Lists.newArrayList();

      for(Entry var3 : this.symbolMap.entrySet()) {
         if(var3.getValue() == null) {
            var1.add(var3.getKey());
         }
      }

      if(!var1.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(var1) + " are missing");
      }
   }

   private static IllegalArgumentException a(IllegalArgumentException var0) {
      return var0;
   }
}
