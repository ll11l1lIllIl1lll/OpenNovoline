package net.minecraft.block;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");

   public BlockFence(Material var1) {
      this(var1, var1.getMaterialMapColor());
   }

   public BlockFence(Material var1, MapColor var2) {
      super(var1, var2);
      this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List var5, Entity var6) {
      boolean var7 = this.canConnectTo(var1, var2.north());
      boolean var8 = this.canConnectTo(var1, var2.south());
      boolean var9 = this.canConnectTo(var1, var2.west());
      boolean var10 = this.canConnectTo(var1, var2.east());
      float var11 = 0.375F;
      float var12 = 0.625F;
      float var13 = 0.375F;
      float var14 = 0.625F;
      var13 = 0.0F;
      var14 = 1.0F;
      this.setBlockBounds(var11, 0.0F, var13, var12, 1.5F, var14);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      var13 = 0.375F;
      var14 = 0.625F;
      var11 = 0.0F;
      var12 = 1.0F;
      this.setBlockBounds(var11, 0.0F, var13, var12, 1.5F, var14);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      var13 = 0.0F;
      var14 = 1.0F;
      this.setBlockBounds(var11, 0.0F, var13, var12, 1.0F, var14);
   }

   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      boolean var3 = this.canConnectTo(var1, var2.north());
      boolean var4 = this.canConnectTo(var1, var2.south());
      boolean var5 = this.canConnectTo(var1, var2.west());
      boolean var6 = this.canConnectTo(var1, var2.east());
      float var7 = 0.375F;
      float var8 = 0.625F;
      float var9 = 0.375F;
      float var10 = 0.625F;
      var9 = 0.0F;
      var10 = 1.0F;
      var7 = 0.0F;
      var8 = 1.0F;
      this.setBlockBounds(var7, 0.0F, var9, var8, 1.0F, var10);
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return false;
   }

   public boolean canConnectTo(IBlockAccess var1, BlockPos var2) {
      Block var3 = var1.getBlockState(var2).getBlock();
      return var3 != Blocks.barrier && (var3 instanceof BlockFence && var3.blockMaterial == this.blockMaterial || var3 instanceof BlockFenceGate || var3.blockMaterial.isOpaque() && var3.isFullCube() && var3.blockMaterial != Material.gourd);
   }

   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return var1.isRemote || ItemLead.attachToFence(var4, var1, var2);
   }

   public int getMetaFromState(IBlockState var1) {
      return 0;
   }

   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(var2, var3.north()))).withProperty(EAST, Boolean.valueOf(this.canConnectTo(var2, var3.east()))).withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(var2, var3.south()))).withProperty(WEST, Boolean.valueOf(this.canConnectTo(var2, var3.west())));
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{NORTH, EAST, WEST, SOUTH});
   }
}
