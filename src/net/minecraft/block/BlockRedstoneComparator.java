package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneComparator$1;
import net.minecraft.block.BlockRedstoneComparator$Mode;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyEnum MODE = PropertyEnum.create("mode", BlockRedstoneComparator$Mode.class);

   public BlockRedstoneComparator(boolean var1) {
      super(var1);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.FALSE).withProperty(MODE, BlockRedstoneComparator$Mode.COMPARE));
      this.isBlockContainer = true;
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.comparator.name");
   }

   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.comparator;
   }

   public Item getItem(World var1, BlockPos var2) {
      return Items.comparator;
   }

   protected int getDelay(IBlockState var1) {
      return 2;
   }

   protected IBlockState getPoweredState(IBlockState var1) {
      Boolean var2 = (Boolean)var1.getValue(POWERED);
      BlockRedstoneComparator$Mode var3 = (BlockRedstoneComparator$Mode)var1.getValue(MODE);
      EnumFacing var4 = (EnumFacing)var1.getValue(FACING);
      return Blocks.powered_comparator.getDefaultState().withProperty(FACING, var4).withProperty(POWERED, var2).withProperty(MODE, var3);
   }

   protected IBlockState getUnpoweredState(IBlockState var1) {
      Boolean var2 = (Boolean)var1.getValue(POWERED);
      BlockRedstoneComparator$Mode var3 = (BlockRedstoneComparator$Mode)var1.getValue(MODE);
      EnumFacing var4 = (EnumFacing)var1.getValue(FACING);
      return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, var4).withProperty(POWERED, var2).withProperty(MODE, var3);
   }

   protected boolean isPowered(IBlockState var1) {
      return this.isRepeaterPowered || ((Boolean)var1.getValue(POWERED)).booleanValue();
   }

   protected int getActiveSignal(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.getTileEntity(var2);
      return var4 instanceof TileEntityComparator?((TileEntityComparator)var4).getOutputSignal():0;
   }

   private int calculateOutput(World var1, BlockPos var2, IBlockState var3) {
      return var3.getValue(MODE) == BlockRedstoneComparator$Mode.SUBTRACT?Math.max(this.calculateInputStrength(var1, var2, var3) - this.getPowerOnSides(var1, var2, var3), 0):this.calculateInputStrength(var1, var2, var3);
   }

   protected boolean shouldBePowered(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.calculateInputStrength(var1, var2, var3);
      return var4 >= 15;
   }

   protected int calculateInputStrength(World var1, BlockPos var2, IBlockState var3) {
      int var4 = super.calculateInputStrength(var1, var2, var3);
      EnumFacing var5 = (EnumFacing)var3.getValue(FACING);
      BlockPos var6 = var2.offset(var5);
      Block var7 = var1.getBlockState(var6).getBlock();
      if(var7.hasComparatorInputOverride()) {
         var4 = var7.getComparatorInputOverride(var1, var6);
      } else if(var4 < 15 && var7.isNormalCube()) {
         var6 = var6.offset(var5);
         var7 = var1.getBlockState(var6).getBlock();
         if(var7.hasComparatorInputOverride()) {
            var4 = var7.getComparatorInputOverride(var1, var6);
         } else if(var7.getMaterial() == Material.air) {
            EntityItemFrame var8 = this.findItemFrame(var1, var5, var6);
            var4 = var8.func_174866_q();
         }
      }

      return var4;
   }

   private EntityItemFrame findItemFrame(World var1, EnumFacing var2, BlockPos var3) {
      List var4 = var1.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), (double)(var3.getX() + 1), (double)(var3.getY() + 1), (double)(var3.getZ() + 1)), new BlockRedstoneComparator$1(this, var2));
      return var4.size() == 1?(EntityItemFrame)var4.get(0):null;
   }

   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if(!var4.abilities.isAllowEdit()) {
         return false;
      } else {
         var3 = var3.cycleProperty(MODE);
         var1.playSoundEffect((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, "random.click", 0.3F, var3.getValue(MODE) == BlockRedstoneComparator$Mode.SUBTRACT?0.55F:0.5F);
         var1.setBlockState(var2, var3, 2);
         this.onStateChange(var1, var2, var3);
         return true;
      }
   }

   protected void updateState(World var1, BlockPos var2, IBlockState var3) {
      if(!var1.isBlockTickPending(var2, this)) {
         int var4 = this.calculateOutput(var1, var2, var3);
         TileEntity var5 = var1.getTileEntity(var2);
         int var6 = var5 instanceof TileEntityComparator?((TileEntityComparator)var5).getOutputSignal():0;
         if(var4 != var6 || this.isPowered(var3) != this.shouldBePowered(var1, var2, var3)) {
            if(this.isFacingTowardsRepeater(var1, var2, var3)) {
               var1.updateBlockTick(var2, this, 2, -1);
            } else {
               var1.updateBlockTick(var2, this, 2, 0);
            }
         }
      }

   }

   private void onStateChange(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.calculateOutput(var1, var2, var3);
      TileEntity var5 = var1.getTileEntity(var2);
      int var6 = 0;
      if(var5 instanceof TileEntityComparator) {
         TileEntityComparator var7 = (TileEntityComparator)var5;
         var6 = var7.getOutputSignal();
         var7.setOutputSignal(var4);
      }

      if(var6 != var4 || var3.getValue(MODE) == BlockRedstoneComparator$Mode.COMPARE) {
         boolean var9 = this.shouldBePowered(var1, var2, var3);
         boolean var8 = this.isPowered(var3);
         var1.setBlockState(var2, var3.withProperty(POWERED, Boolean.FALSE), 2);
         this.notifyNeighbors(var1, var2, var3);
      }

   }

   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if(this.isRepeaterPowered) {
         var1.setBlockState(var2, this.getUnpoweredState(var3).withProperty(POWERED, Boolean.TRUE), 4);
      }

      this.onStateChange(var1, var2, var3);
   }

   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      super.onBlockAdded(var1, var2, var3);
      var1.setTileEntity(var2, this.createNewTileEntity(var1, 0));
   }

   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      var1.removeTileEntity(var2);
      this.notifyNeighbors(var1, var2, var3);
   }

   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      super.onBlockEventReceived(var1, var2, var3, var4, var5);
      TileEntity var6 = var1.getTileEntity(var2);
      return var6.receiveClientEvent(var4, var5);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityComparator();
   }

   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(var1)).withProperty(POWERED, Boolean.valueOf((var1 & 8) > 0)).withProperty(MODE, (var1 & 4) > 0?BlockRedstoneComparator$Mode.SUBTRACT:BlockRedstoneComparator$Mode.COMPARE);
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((EnumFacing)var1.getValue(FACING)).getHorizontalIndex();
      if(((Boolean)var1.getValue(POWERED)).booleanValue()) {
         var2 |= 8;
      }

      if(var1.getValue(MODE) == BlockRedstoneComparator$Mode.SUBTRACT) {
         var2 |= 4;
      }

      return var2;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING, MODE, POWERED});
   }

   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite()).withProperty(POWERED, Boolean.FALSE).withProperty(MODE, BlockRedstoneComparator$Mode.COMPARE);
   }
}
