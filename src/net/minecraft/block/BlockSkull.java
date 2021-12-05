package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSkull$1;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern$PatternHelper;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing$Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyBool NODROP = PropertyBool.create("nodrop");
   private static final Predicate IS_WITHER_SKELETON = BlockSkull::lambda$static$0;
   private BlockPattern witherBasePattern;
   private BlockPattern witherPattern;

   protected BlockSkull() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, Boolean.FALSE));
      this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal("tile.skull.skeleton.name");
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      switch(BlockSkull$1.$SwitchMap$net$minecraft$util$EnumFacing[((EnumFacing)var1.getBlockState(var2).getValue(FACING)).ordinal()]) {
      case 1:
      default:
         this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
         break;
      case 2:
         this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
         break;
      case 3:
         this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
         break;
      case 4:
         this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
         break;
      case 5:
         this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }

   }

   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing()).withProperty(NODROP, Boolean.FALSE);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntitySkull();
   }

   public Item getItem(World var1, BlockPos var2) {
      return Items.skull;
   }

   public int getDamageValue(World var1, BlockPos var2) {
      TileEntity var3 = var1.getTileEntity(var2);
      return var3 instanceof TileEntitySkull?((TileEntitySkull)var3).getSkullType():super.getDamageValue(var1, var2);
   }

   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
   }

   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if(var4.abilities.isCreative()) {
         var3 = var3.withProperty(NODROP, Boolean.TRUE);
         var1.setBlockState(var2, var3, 4);
      }

      super.onBlockHarvested(var1, var2, var3, var4);
   }

   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if(!var1.isRemote) {
         if(!((Boolean)var3.getValue(NODROP)).booleanValue()) {
            TileEntity var4 = var1.getTileEntity(var2);
            if(var4 instanceof TileEntitySkull) {
               TileEntitySkull var5 = (TileEntitySkull)var4;
               ItemStack var6 = new ItemStack(Items.skull, 1, this.getDamageValue(var1, var2));
               if(var5.getSkullType() == 3 && var5.getPlayerProfile() != null) {
                  var6.setTagCompound(new NBTTagCompound());
                  NBTTagCompound var7 = new NBTTagCompound();
                  NBTUtil.writeGameProfile(var7, var5.getPlayerProfile());
                  var6.getTagCompound().setTag("SkullOwner", var7);
               }

               spawnAsEntity(var1, var2, var6);
            }
         }

         super.breakBlock(var1, var2, var3);
      }

   }

   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.skull;
   }

   public boolean canDispenserPlace(World var1, BlockPos var2, ItemStack var3) {
      return var3.getMetadata() == 1 && var2.getY() >= 2 && var1.getDifficulty() != EnumDifficulty.PEACEFUL && !var1.isRemote && this.getWitherBasePattern().match(var1, var2) != null;
   }

   public void checkWitherSpawn(World var1, BlockPos var2, TileEntitySkull var3) {
      if(var3.getSkullType() == 1 && var2.getY() >= 2 && var1.getDifficulty() != EnumDifficulty.PEACEFUL && !var1.isRemote) {
         BlockPattern var4 = this.getWitherPattern();
         BlockPattern$PatternHelper var5 = var4.match(var1, var2);

         for(int var6 = 0; var6 < 3; ++var6) {
            BlockWorldState var7 = var5.translateOffset(var6, 0, 0);
            var1.setBlockState(var7.getPos(), var7.getBlockState().withProperty(NODROP, Boolean.TRUE), 2);
         }

         for(int var11 = 0; var11 < var4.getPalmLength(); ++var11) {
            for(int var13 = 0; var13 < var4.getThumbLength(); ++var13) {
               var1.setBlockState(var5.translateOffset(var11, var13, 0).getPos(), Blocks.air.getDefaultState(), 2);
            }
         }

         BlockPos var12 = var5.translateOffset(1, 0, 0).getPos();
         EntityWither var14 = new EntityWither(var1);
         BlockPos var8 = var5.translateOffset(1, 2, 0).getPos();
         var14.setLocationAndAngles((double)var8.getX() + 0.5D, (double)var8.getY() + 0.55D, (double)var8.getZ() + 0.5D, var5.getFinger().getAxis() == EnumFacing$Axis.X?0.0F:90.0F, 0.0F);
         var14.renderYawOffset = var5.getFinger().getAxis() == EnumFacing$Axis.X?0.0F:90.0F;
         var14.func_82206_m();

         for(EntityPlayer var10 : var1.getEntitiesWithinAABB(EntityPlayer.class, var14.getEntityBoundingBox().expand(50.0D, 50.0D, 50.0D))) {
            var10.triggerAchievement(AchievementList.spawnWither);
         }

         var1.spawnEntityInWorld(var14);

         for(int var15 = 0; var15 < 120; ++var15) {
            var1.spawnParticle(EnumParticleTypes.SNOWBALL, (double)var12.getX() + var1.rand.nextDouble(), (double)(var12.getY() - 2) + var1.rand.nextDouble() * 3.9D, (double)var12.getZ() + var1.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
         }

         for(int var16 = 0; var16 < var4.getPalmLength(); ++var16) {
            for(int var17 = 0; var17 < var4.getThumbLength(); ++var17) {
               var1.notifyNeighborsRespectDebug(var5.translateOffset(var16, var17, 0).getPos(), Blocks.air);
            }
         }
      }

   }

   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(var1 & 7)).withProperty(NODROP, Boolean.valueOf((var1 & 8) > 0));
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((EnumFacing)var1.getValue(FACING)).getIndex();
      if(((Boolean)var1.getValue(NODROP)).booleanValue()) {
         var2 |= 8;
      }

      return var2;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING, NODROP});
   }

   protected BlockPattern getWitherBasePattern() {
      if(this.witherBasePattern == null) {
         this.witherBasePattern = FactoryBlockPattern.start().aisle(new String[]{"   ", "###", "~#~"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
      }

      return this.witherBasePattern;
   }

   protected BlockPattern getWitherPattern() {
      if(this.witherPattern == null) {
         this.witherPattern = FactoryBlockPattern.start().aisle(new String[]{"^^^", "###", "~#~"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('^', IS_WITHER_SKELETON).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
      }

      return this.witherPattern;
   }

   private static boolean lambda$static$0(BlockWorldState var0) {
      return var0.getBlockState() != null && var0.getBlockState().getBlock() == Blocks.skull && var0.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull)var0.getTileEntity()).getSkullType() == 1;
   }
}
