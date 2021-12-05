package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.sI;
import net.sN;
import net.sb;
import net.sq;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes$3;
import net.minecraft.client.renderer.BlockModelShapes$4;
import net.minecraft.client.renderer.BlockModelShapes$5;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap$Builder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;

public class BlockModelShapes {
   private final Map bakedModelStore = Maps.newIdentityHashMap();
   private final BlockStateMapper blockStateMapper = new BlockStateMapper();
   private final ModelManager modelManager;

   public BlockModelShapes(ModelManager var1) {
      this.modelManager = var1;
      this.registerAllBlocks();
   }

   public BlockStateMapper getBlockStateMapper() {
      return this.blockStateMapper;
   }

   public TextureAtlasSprite getTexture(IBlockState var1) {
      Block var2 = var1.getBlock();
      IBakedModel var3 = this.getModelForState(var1);
      if(var3 == this.modelManager.getMissingModel()) {
         if(var2 == Blocks.wall_sign || var2 == Blocks.standing_sign || var2 == Blocks.chest || var2 == Blocks.trapped_chest || var2 == Blocks.standing_banner || var2 == Blocks.wall_banner) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/planks_oak");
         }

         if(var2 == Blocks.ender_chest) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/obsidian");
         }

         if(var2 == Blocks.flowing_lava || var2 == Blocks.lava) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/lava_still");
         }

         if(var2 == Blocks.flowing_water || var2 == Blocks.water) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/water_still");
         }

         if(var2 == Blocks.skull) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/soul_sand");
         }

         if(var2 == Blocks.barrier) {
            return this.modelManager.getTextureMap().getAtlasSprite("minecraft:items/barrier");
         }
      }

      var3 = this.modelManager.getMissingModel();
      return var3.getParticleTexture();
   }

   public IBakedModel getModelForState(IBlockState var1) {
      IBakedModel var2 = (IBakedModel)this.bakedModelStore.get(var1);
      var2 = this.modelManager.getMissingModel();
      return var2;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void reloadModels() {
      this.bakedModelStore.clear();

      for(Entry var2 : this.blockStateMapper.putAllStateModelLocations().entrySet()) {
         this.bakedModelStore.put(var2.getKey(), this.modelManager.a((ModelResourceLocation)var2.getValue()));
      }

   }

   public void registerBlockWithStateMapper(Block var1, IStateMapper var2) {
      this.blockStateMapper.registerBlockStateMapper(var1, var2);
   }

   public void registerBuiltInBlocks(Block... var1) {
      this.blockStateMapper.registerBuiltInBlocks(var1);
   }

   private void registerAllBlocks() {
      this.registerBuiltInBlocks(new Block[]{Blocks.air, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.piston_extension, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.standing_sign, Blocks.skull, Blocks.end_portal, Blocks.barrier, Blocks.wall_sign, Blocks.wall_banner, Blocks.standing_banner});
      this.registerBlockWithStateMapper(Blocks.stone, (new StateMap$Builder()).withName(BlockStone.VARIANT).build());
      this.registerBlockWithStateMapper(Blocks.prismarine, (new StateMap$Builder()).withName(BlockPrismarine.VARIANT).build());
      this.registerBlockWithStateMapper(Blocks.leaves, (new StateMap$Builder()).withName(BlockOldLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[]{BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
      this.registerBlockWithStateMapper(Blocks.leaves2, (new StateMap$Builder()).withName(BlockNewLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[]{BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
      this.registerBlockWithStateMapper(Blocks.cactus, (new StateMap$Builder()).ignore(new IProperty[]{BlockCactus.P}).build());
      this.registerBlockWithStateMapper(Blocks.reeds, (new StateMap$Builder()).ignore(new IProperty[]{BlockReed.P}).build());
      this.registerBlockWithStateMapper(Blocks.jukebox, (new StateMap$Builder()).ignore(new IProperty[]{BlockJukebox.HAS_RECORD}).build());
      this.registerBlockWithStateMapper(Blocks.command_block, (new StateMap$Builder()).ignore(new IProperty[]{BlockCommandBlock.TRIGGERED}).build());
      this.registerBlockWithStateMapper(Blocks.cobblestone_wall, (new StateMap$Builder()).withName(BlockWall.VARIANT).withSuffix("_wall").build());
      this.registerBlockWithStateMapper(Blocks.double_plant, (new StateMap$Builder()).withName(BlockDoublePlant.VARIANT).ignore(new IProperty[]{BlockDoublePlant.field_181084_N}).build());
      this.registerBlockWithStateMapper(Blocks.oak_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.spruce_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.birch_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.jungle_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.dark_oak_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.acacia_fence_gate, (new StateMap$Builder()).ignore(new IProperty[]{BlockFenceGate.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.tripwire, (new StateMap$Builder()).ignore(new IProperty[]{BlockTripWire.DISARMED, BlockTripWire.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.double_wooden_slab, (new StateMap$Builder()).withName(BlockPlanks.VARIANT).withSuffix("_double_slab").build());
      this.registerBlockWithStateMapper(Blocks.wooden_slab, (new StateMap$Builder()).withName(BlockPlanks.VARIANT).withSuffix("_slab").build());
      this.registerBlockWithStateMapper(Blocks.tnt, (new StateMap$Builder()).ignore(new IProperty[]{BlockTNT.EXPLODE}).build());
      this.registerBlockWithStateMapper(Blocks.fire, (new StateMap$Builder()).ignore(new IProperty[]{BlockFire.X}).build());
      this.registerBlockWithStateMapper(Blocks.redstone_wire, (new StateMap$Builder()).ignore(new IProperty[]{BlockRedstoneWire.P}).build());
      this.registerBlockWithStateMapper(Blocks.oak_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.spruce_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.birch_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.jungle_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.acacia_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.dark_oak_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.iron_door, (new StateMap$Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      this.registerBlockWithStateMapper(Blocks.wool, (new StateMap$Builder()).withName(BlockColored.COLOR).withSuffix("_wool").build());
      this.registerBlockWithStateMapper(Blocks.carpet, (new StateMap$Builder()).withName(BlockColored.COLOR).withSuffix("_carpet").build());
      this.registerBlockWithStateMapper(Blocks.stained_hardened_clay, (new StateMap$Builder()).withName(BlockColored.COLOR).withSuffix("_stained_hardened_clay").build());
      this.registerBlockWithStateMapper(Blocks.stained_glass_pane, (new StateMap$Builder()).withName(BlockColored.COLOR).withSuffix("_stained_glass_pane").build());
      this.registerBlockWithStateMapper(Blocks.stained_glass, (new StateMap$Builder()).withName(BlockColored.COLOR).withSuffix("_stained_glass").build());
      this.registerBlockWithStateMapper(Blocks.sandstone, (new StateMap$Builder()).withName(BlockSandStone.TYPE).build());
      this.registerBlockWithStateMapper(Blocks.red_sandstone, (new StateMap$Builder()).withName(BlockRedSandstone.TYPE).build());
      this.registerBlockWithStateMapper(Blocks.tallgrass, (new StateMap$Builder()).withName(BlockTallGrass.TYPE).build());
      this.registerBlockWithStateMapper(Blocks.bed, (new StateMap$Builder()).ignore(new IProperty[]{BlockBed.OCCUPIED}).build());
      this.registerBlockWithStateMapper(Blocks.yellow_flower, (new StateMap$Builder()).withName(Blocks.yellow_flower.getTypeProperty()).build());
      this.registerBlockWithStateMapper(Blocks.red_flower, (new StateMap$Builder()).withName(Blocks.red_flower.getTypeProperty()).build());
      this.registerBlockWithStateMapper(Blocks.stone_slab, (new StateMap$Builder()).withName(BlockStoneSlab.VARIANT).withSuffix("_slab").build());
      this.registerBlockWithStateMapper(Blocks.stone_slab2, (new StateMap$Builder()).withName(BlockStoneSlabNew.VARIANT).withSuffix("_slab").build());
      this.registerBlockWithStateMapper(Blocks.monster_egg, (new StateMap$Builder()).withName(BlockSilverfish.VARIANT).withSuffix("_monster_egg").build());
      this.registerBlockWithStateMapper(Blocks.stonebrick, (new StateMap$Builder()).withName(BlockStoneBrick.VARIANT).build());
      this.registerBlockWithStateMapper(Blocks.dispenser, (new StateMap$Builder()).ignore(new IProperty[]{BlockDispenser.TRIGGERED}).build());
      this.registerBlockWithStateMapper(Blocks.dropper, (new StateMap$Builder()).ignore(new IProperty[]{BlockDropper.TRIGGERED}).build());
      this.registerBlockWithStateMapper(Blocks.log, (new StateMap$Builder()).withName(BlockOldLog.VARIANT).withSuffix("_log").build());
      this.registerBlockWithStateMapper(Blocks.log2, (new StateMap$Builder()).withName(BlockNewLog.VARIANT).withSuffix("_log").build());
      this.registerBlockWithStateMapper(Blocks.planks, (new StateMap$Builder()).withName(BlockPlanks.VARIANT).withSuffix("_planks").build());
      this.registerBlockWithStateMapper(Blocks.sapling, (new StateMap$Builder()).withName(BlockSapling.TYPE).withSuffix("_sapling").build());
      this.registerBlockWithStateMapper(Blocks.sand, (new StateMap$Builder()).withName(BlockSand.VARIANT).build());
      this.registerBlockWithStateMapper(Blocks.hopper, (new StateMap$Builder()).ignore(new IProperty[]{BlockHopper.ENABLED}).build());
      this.registerBlockWithStateMapper(Blocks.flower_pot, (new StateMap$Builder()).ignore(new IProperty[]{BlockFlowerPot.Q}).build());
      this.registerBlockWithStateMapper(Blocks.quartz_block, new sb(this));
      this.registerBlockWithStateMapper(Blocks.deadbush, new sN(this));
      this.registerBlockWithStateMapper(Blocks.pumpkin_stem, new BlockModelShapes$3(this));
      this.registerBlockWithStateMapper(Blocks.melon_stem, new BlockModelShapes$4(this));
      this.registerBlockWithStateMapper(Blocks.dirt, new BlockModelShapes$5(this));
      this.registerBlockWithStateMapper(Blocks.double_stone_slab, new sq(this));
      this.registerBlockWithStateMapper(Blocks.double_stone_slab2, new sI(this));
   }
}
