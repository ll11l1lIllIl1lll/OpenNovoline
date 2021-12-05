package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed$EnumPartType;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer$EnumStatus;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
   public static final PropertyEnum PART = PropertyEnum.create("part", BlockBed$EnumPartType.class);
   public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");

   public BlockBed() {
      super(Material.cloth);
      this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBed$EnumPartType.FOOT).withProperty(OCCUPIED, Boolean.FALSE));
      this.setBedBounds();
   }

   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if(!var1.isRemote) {
         if(var3.getValue(PART) != BlockBed$EnumPartType.HEAD) {
            var2 = var2.offset((EnumFacing)var3.getValue(FACING));
            var3 = var1.getBlockState(var2);
            if(var3.getBlock() != this) {
               return true;
            }
         }

         if(var1.provider.canRespawnHere() && var1.getBiomeGenForCoords(var2) != BiomeGenBase.hell) {
            if(((Boolean)var3.getValue(OCCUPIED)).booleanValue()) {
               EntityPlayer var12 = this.getPlayerInBed(var1, var2);
               var4.addChatMessage(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
               return true;
            }

            EntityPlayer$EnumStatus var11 = var4.trySleep(var2);
            if(var11 == EntityPlayer$EnumStatus.OK) {
               var3 = var3.withProperty(OCCUPIED, Boolean.TRUE);
               var1.setBlockState(var2, var3, 4);
            } else if(var11 == EntityPlayer$EnumStatus.NOT_POSSIBLE_NOW) {
               var4.addChatMessage(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
            } else if(var11 == EntityPlayer$EnumStatus.NOT_SAFE) {
               var4.addChatMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
            }
         } else {
            var1.setBlockToAir(var2);
            BlockPos var9 = var2.offset(((EnumFacing)var3.getValue(FACING)).getOpposite());
            if(var1.getBlockState(var9).getBlock() == this) {
               var1.setBlockToAir(var9);
            }

            var1.newExplosion((Entity)null, (double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, 5.0F, true, true);
         }
      }

      return true;
   }

   private EntityPlayer getPlayerInBed(World var1, BlockPos var2) {
      for(EntityPlayer var4 : var1.getPlayerEntities()) {
         if(var4.isPlayerSleeping() && var4.playerLocation.equals(var2)) {
            return var4;
         }
      }

      return null;
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBedBounds();
   }

   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var5 = (EnumFacing)var3.getValue(FACING);
      if(var3.getValue(PART) == BlockBed$EnumPartType.HEAD) {
         if(var1.getBlockState(var2.offset(var5.getOpposite())).getBlock() != this) {
            var1.setBlockToAir(var2);
         }
      } else if(var1.getBlockState(var2.offset(var5)).getBlock() != this) {
         var1.setBlockToAir(var2);
         if(!var1.isRemote) {
            this.dropBlockAsItem(var1, var2, var3, 0);
         }
      }

   }

   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var1.getValue(PART) == BlockBed$EnumPartType.HEAD?null:Items.bed;
   }

   private void setBedBounds() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos getSafeExitLocation(World var0, BlockPos var1, int var2) {
      EnumFacing var3 = (EnumFacing)var0.getBlockState(var1).getValue(FACING);
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();

      for(int var7 = 0; var7 <= 1; ++var7) {
         int var8 = var4 - var3.getFrontOffsetX() * var7 - 1;
         int var9 = var6 - var3.getFrontOffsetZ() * var7 - 1;
         int var10 = var8 + 2;
         int var11 = var9 + 2;

         for(int var12 = var8; var12 <= var10; ++var12) {
            for(int var13 = var9; var13 <= var11; ++var13) {
               BlockPos var14 = new BlockPos(var12, var5, var13);
               if(hasRoomForPlayer(var0, var14)) {
                  return var14;
               }
            }
         }
      }

      return null;
   }

   protected static boolean hasRoomForPlayer(World var0, BlockPos var1) {
      return World.doesBlockHaveSolidTopSurface(var0, var1.down()) && !var0.getBlockState(var1).getBlock().getMaterial().isSolid() && !var0.getBlockState(var1.up()).getBlock().getMaterial().isSolid();
   }

   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if(var3.getValue(PART) == BlockBed$EnumPartType.FOOT) {
         super.dropBlockAsItemWithChance(var1, var2, var3, var4, 0);
      }

   }

   public int getMobilityFlag() {
      return 1;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public Item getItem(World var1, BlockPos var2) {
      return Items.bed;
   }

   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if(var4.abilities.isCreative() && var3.getValue(PART) == BlockBed$EnumPartType.HEAD) {
         BlockPos var5 = var2.offset(((EnumFacing)var3.getValue(FACING)).getOpposite());
         if(var1.getBlockState(var5).getBlock() == this) {
            var1.setBlockToAir(var5);
         }
      }

   }

   public IBlockState getStateFromMeta(int var1) {
      EnumFacing var2 = EnumFacing.getHorizontal(var1);
      return (var1 & 8) > 0?this.getDefaultState().withProperty(PART, BlockBed$EnumPartType.HEAD).withProperty(FACING, var2).withProperty(OCCUPIED, Boolean.valueOf((var1 & 4) > 0)):this.getDefaultState().withProperty(PART, BlockBed$EnumPartType.FOOT).withProperty(FACING, var2);
   }

   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if(var1.getValue(PART) == BlockBed$EnumPartType.FOOT) {
         IBlockState var4 = var2.getBlockState(var3.offset((EnumFacing)var1.getValue(FACING)));
         if(var4.getBlock() == this) {
            var1 = var1.withProperty(OCCUPIED, var4.getValue(OCCUPIED));
         }
      }

      return var1;
   }

   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 = var2 | ((EnumFacing)var1.getValue(FACING)).getHorizontalIndex();
      if(var1.getValue(PART) == BlockBed$EnumPartType.HEAD) {
         var2 |= 8;
         if(((Boolean)var1.getValue(OCCUPIED)).booleanValue()) {
            var2 |= 4;
         }
      }

      return var2;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING, PART, OCCUPIED});
   }
}
