package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog$EnumAxis;
import net.minecraft.block.BlockNewLog$1;
import net.minecraft.block.BlockPlanks$EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog extends BlockLog {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks$EnumType.class, BlockNewLog::lambda$static$0);

   public BlockNewLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks$EnumType.ACACIA).withProperty(LOG_AXIS, BlockLog$EnumAxis.Y));
   }

   public MapColor getMapColor(IBlockState var1) {
      BlockPlanks$EnumType var2 = (BlockPlanks$EnumType)var1.getValue(VARIANT);
      switch(BlockNewLog$1.$SwitchMap$net$minecraft$block$BlockLog$EnumAxis[((BlockLog$EnumAxis)var1.getValue(LOG_AXIS)).ordinal()]) {
      case 1:
      case 2:
      case 3:
      default:
         switch(BlockNewLog$1.$SwitchMap$net$minecraft$block$BlockPlanks$EnumType[var2.ordinal()]) {
         case 1:
         default:
            return MapColor.stoneColor;
         case 2:
            return BlockPlanks$EnumType.DARK_OAK.func_181070_c();
         }
      case 4:
         return var2.func_181070_c();
      }
   }

   public void getSubBlocks(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.ACACIA.getMetadata() - 4));
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.DARK_OAK.getMetadata() - 4));
   }

   public IBlockState getStateFromMeta(int var1) {
      IBlockState var2 = this.getDefaultState().withProperty(VARIANT, BlockPlanks$EnumType.byMetadata((var1 & 3) + 4));
      switch(var1 & 12) {
      case 0:
         var2 = var2.withProperty(LOG_AXIS, BlockLog$EnumAxis.Y);
         break;
      case 4:
         var2 = var2.withProperty(LOG_AXIS, BlockLog$EnumAxis.X);
         break;
      case 8:
         var2 = var2.withProperty(LOG_AXIS, BlockLog$EnumAxis.Z);
         break;
      default:
         var2 = var2.withProperty(LOG_AXIS, BlockLog$EnumAxis.NONE);
      }

      return var2;
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata() - 4;
      switch(BlockNewLog$1.$SwitchMap$net$minecraft$block$BlockLog$EnumAxis[((BlockLog$EnumAxis)var1.getValue(LOG_AXIS)).ordinal()]) {
      case 1:
         var2 |= 4;
         break;
      case 2:
         var2 |= 8;
         break;
      case 3:
         var2 |= 12;
      }

      return var2;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{VARIANT, LOG_AXIS});
   }

   protected ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata() - 4);
   }

   public int damageDropped(IBlockState var1) {
      return ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata() - 4;
   }

   private static boolean lambda$static$0(BlockPlanks$EnumType var0) {
      return var0.getMetadata() >= 4;
   }
}
