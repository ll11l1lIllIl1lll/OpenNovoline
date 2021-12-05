package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPlanks$EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing$Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

   public BlockFenceGate(BlockPlanks$EnumType var1) {
      super(Material.wood, var1.func_181070_c());
      this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, Boolean.FALSE).withProperty(POWERED, Boolean.FALSE).withProperty(IN_WALL, Boolean.FALSE));
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      EnumFacing$Axis var4 = ((EnumFacing)var1.getValue(FACING)).getAxis();
      if(var4 == EnumFacing$Axis.Z && (var2.getBlockState(var3.west()).getBlock() == Blocks.cobblestone_wall || var2.getBlockState(var3.east()).getBlock() == Blocks.cobblestone_wall) || var4 == EnumFacing$Axis.X && (var2.getBlockState(var3.north()).getBlock() == Blocks.cobblestone_wall || var2.getBlockState(var3.south()).getBlock() == Blocks.cobblestone_wall)) {
         var1 = var1.withProperty(IN_WALL, Boolean.TRUE);
      }

      return var1;
   }

   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return var1.getBlockState(var2.down()).getBlock().getMaterial().isSolid() && super.canPlaceBlockAt(var1, var2);
   }

   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      if(((Boolean)var3.getValue(OPEN)).booleanValue()) {
         return null;
      } else {
         EnumFacing$Axis var4 = ((EnumFacing)var3.getValue(FACING)).getAxis();
         return var4 == EnumFacing$Axis.Z?new AxisAlignedBB((double)var2.getX(), (double)var2.getY(), (double)((float)var2.getZ() + 0.375F), (double)(var2.getX() + 1), (double)((float)var2.getY() + 1.5F), (double)((float)var2.getZ() + 0.625F)):new AxisAlignedBB((double)((float)var2.getX() + 0.375F), (double)var2.getY(), (double)var2.getZ(), (double)((float)var2.getX() + 0.625F), (double)((float)var2.getY() + 1.5F), (double)(var2.getZ() + 1));
      }
   }

   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      EnumFacing$Axis var3 = ((EnumFacing)var1.getBlockState(var2).getValue(FACING)).getAxis();
      if(var3 == EnumFacing$Axis.Z) {
         this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
      }

   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return ((Boolean)var1.getBlockState(var2).getValue(OPEN)).booleanValue();
   }

   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing()).withProperty(OPEN, Boolean.FALSE).withProperty(POWERED, Boolean.FALSE).withProperty(IN_WALL, Boolean.FALSE);
   }

   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if(((Boolean)var3.getValue(OPEN)).booleanValue()) {
         var3 = var3.withProperty(OPEN, Boolean.FALSE);
      } else {
         EnumFacing var9 = EnumFacing.fromAngle((double)var4.rotationYaw);
         if(var3.getValue(FACING) == var9.getOpposite()) {
            var3 = var3.withProperty(FACING, var9);
         }

         var3 = var3.withProperty(OPEN, Boolean.TRUE);
      }

      var1.setBlockState(var2, var3, 2);
      var1.playAuxSFXAtEntity(var4, ((Boolean)var3.getValue(OPEN)).booleanValue()?1003:1006, var2, 0);
      return true;
   }

   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if(!var1.isRemote) {
         boolean var5 = var1.isBlockPowered(var2);
         if(var4.canProvidePower()) {
            if(!((Boolean)var3.getValue(OPEN)).booleanValue() && !((Boolean)var3.getValue(POWERED)).booleanValue()) {
               var1.setBlockState(var2, var3.withProperty(OPEN, Boolean.TRUE).withProperty(POWERED, Boolean.TRUE), 2);
               var1.playAuxSFXAtEntity((EntityPlayer)null, 1003, var2, 0);
            } else if(((Boolean)var3.getValue(OPEN)).booleanValue() && ((Boolean)var3.getValue(POWERED)).booleanValue()) {
               var1.setBlockState(var2, var3.withProperty(OPEN, Boolean.FALSE).withProperty(POWERED, Boolean.FALSE), 2);
               var1.playAuxSFXAtEntity((EntityPlayer)null, 1006, var2, 0);
            } else if(var5 != ((Boolean)var3.getValue(POWERED)).booleanValue()) {
               var1.setBlockState(var2, var3.withProperty(POWERED, Boolean.valueOf(var5)), 2);
            }
         }
      }

   }

   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(var1)).withProperty(OPEN, Boolean.valueOf((var1 & 4) != 0)).withProperty(POWERED, Boolean.valueOf((var1 & 8) != 0));
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((EnumFacing)var1.getValue(FACING)).getHorizontalIndex();
      if(((Boolean)var1.getValue(POWERED)).booleanValue()) {
         var2 |= 8;
      }

      if(((Boolean)var1.getValue(OPEN)).booleanValue()) {
         var2 |= 4;
      }

      return var2;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING, OPEN, POWERED, IN_WALL});
   }
}
