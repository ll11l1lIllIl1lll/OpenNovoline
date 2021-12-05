package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks$EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks$EnumType.class, BlockOldLeaf::lambda$static$0);

   public BlockOldLeaf() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks$EnumType.OAK).withProperty(CHECK_DECAY, Boolean.TRUE).withProperty(DECAYABLE, Boolean.TRUE));
   }

   public int getRenderColor(IBlockState var1) {
      if(var1.getBlock() != this) {
         return super.getRenderColor(var1);
      } else {
         BlockPlanks$EnumType var2 = (BlockPlanks$EnumType)var1.getValue(VARIANT);
         return var2 == BlockPlanks$EnumType.SPRUCE?ColorizerFoliage.getFoliageColorPine():(var2 == BlockPlanks$EnumType.BIRCH?ColorizerFoliage.getFoliageColorBirch():super.getRenderColor(var1));
      }
   }

   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      IBlockState var4 = var1.getBlockState(var2);
      if(var4.getBlock() == this) {
         BlockPlanks$EnumType var5 = (BlockPlanks$EnumType)var4.getValue(VARIANT);
         if(var5 == BlockPlanks$EnumType.SPRUCE) {
            return ColorizerFoliage.getFoliageColorPine();
         }

         if(var5 == BlockPlanks$EnumType.BIRCH) {
            return ColorizerFoliage.getFoliageColorBirch();
         }
      }

      return super.colorMultiplier(var1, var2, var3);
   }

   protected void dropApple(World var1, BlockPos var2, IBlockState var3, int var4) {
      if(var3.getValue(VARIANT) == BlockPlanks$EnumType.OAK && var1.rand.nextInt(var4) == 0) {
         spawnAsEntity(var1, var2, new ItemStack(Items.apple, 1, 0));
      }

   }

   protected int getSaplingDropChance(IBlockState var1) {
      return var1.getValue(VARIANT) == BlockPlanks$EnumType.JUNGLE?40:super.getSaplingDropChance(var1);
   }

   public void getSubBlocks(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.OAK.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.SPRUCE.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.BIRCH.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks$EnumType.JUNGLE.getMetadata()));
   }

   protected ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata());
   }

   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, this.getWoodType(var1)).withProperty(DECAYABLE, Boolean.valueOf((var1 & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((var1 & 8) > 0));
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata();
      if(!((Boolean)var1.getValue(DECAYABLE)).booleanValue()) {
         var2 |= 4;
      }

      if(((Boolean)var1.getValue(CHECK_DECAY)).booleanValue()) {
         var2 |= 8;
      }

      return var2;
   }

   public BlockPlanks$EnumType getWoodType(int var1) {
      return BlockPlanks$EnumType.byMetadata((var1 & 3) % 4);
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{VARIANT, CHECK_DECAY, DECAYABLE});
   }

   public int damageDropped(IBlockState var1) {
      return ((BlockPlanks$EnumType)var1.getValue(VARIANT)).getMetadata();
   }

   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if(!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().getItem() == Items.shears) {
         var2.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(var1, var3, new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanks$EnumType)var4.getValue(VARIANT)).getMetadata()));
      } else {
         super.harvestBlock(var1, var2, var3, var4, var5);
      }

   }

   private static boolean lambda$static$0(BlockPlanks$EnumType var0) {
      return var0.getMetadata() < 4;
   }
}
