package net.minecraft.item;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.a5z;
import net.aXp;
import net.ajY;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt$DirtType;
import net.minecraft.block.BlockDoublePlant$EnumPlantType;
import net.minecraft.block.BlockFlower$EnumFlowerColor;
import net.minecraft.block.BlockFlower$EnumFlowerType;
import net.minecraft.block.BlockPlanks$EnumType;
import net.minecraft.block.BlockPrismarine$EnumType;
import net.minecraft.block.BlockRedSandstone$EnumType;
import net.minecraft.block.BlockSand$EnumType;
import net.minecraft.block.BlockSandStone$EnumType;
import net.minecraft.block.BlockStone$EnumType;
import net.minecraft.block.BlockStoneBrick$EnumType;
import net.minecraft.block.BlockWall$EnumType;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart$EnumMinecartType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item$ToolMaterial;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemDoublePlant;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
   public static final RegistryNamespaced itemRegistry = new RegistryNamespaced();
   private static final Map BLOCK_TO_ITEM = Maps.newHashMap();
   protected static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   private CreativeTabs tabToDisplayOn;
   protected static Random itemRand = new Random();
   protected int maxStackSize = 64;
   private int maxDamage;
   protected boolean bFull3D;
   protected boolean hasSubtypes;
   private Item containerItem;
   private String potionEffect;
   private String unlocalizedName;

   public static int b(Item var0) {
      return 0;
   }

   public static Item getItemById(int var0) {
      return (Item)itemRegistry.getObjectById(var0);
   }

   public static Item getItemFromBlock(Block var0) {
      return (Item)BLOCK_TO_ITEM.get(var0);
   }

   public ModelBiped a(EntityLivingBase var1, ItemStack var2, int var3) {
      return null;
   }

   public static Item getByNameOrId(String var0) {
      Item var1 = (Item)itemRegistry.getObject(new ResourceLocation(var0));
      String var10000 = var0;

      try {
         return getItemById(Integer.parseInt(var10000));
      } catch (NumberFormatException var3) {
         return var1;
      }
   }

   public void updateItemStackNBT(NBTTagCompound var1) {
   }

   public Item setMaxStackSize(int var1) {
      this.maxStackSize = var1;
      return this;
   }

   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      return false;
   }

   public float getStrVsBlock(ItemStack var1, Block var2) {
      return 1.0F;
   }

   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public ItemStack onItemUseFinish(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public int getItemStackLimit() {
      return this.maxStackSize;
   }

   public int getMetadata(int var1) {
      return 0;
   }

   public boolean getHasSubtypes() {
      return this.hasSubtypes;
   }

   protected Item setHasSubtypes(boolean var1) {
      this.hasSubtypes = var1;
      return this;
   }

   public int getMaxDamage() {
      return this.maxDamage;
   }

   protected Item setMaxDamage(int var1) {
      this.maxDamage = var1;
      return this;
   }

   public boolean isDamageable() {
      return this.maxDamage > 0 && !this.hasSubtypes;
   }

   public boolean hitEntity(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      return false;
   }

   public boolean onBlockDestroyed(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      return false;
   }

   public boolean canHarvestBlock(Block var1) {
      return false;
   }

   public boolean itemInteractionForEntity(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      return false;
   }

   public Item setFull3D() {
      this.bFull3D = true;
      return this;
   }

   public boolean isFull3D() {
      return this.bFull3D;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return false;
   }

   public Item setUnlocalizedName(String var1) {
      this.unlocalizedName = var1;
      return this;
   }

   public String getUnlocalizedNameInefficiently(ItemStack var1) {
      String var2 = this.getUnlocalizedName(var1);
      return "";
   }

   public String getUnlocalizedName() {
      return "item." + this.unlocalizedName;
   }

   public String getUnlocalizedName(ItemStack var1) {
      return "item." + this.unlocalizedName;
   }

   public Item setContainerItem(Item var1) {
      this.containerItem = var1;
      return this;
   }

   public boolean getShareTag() {
      return true;
   }

   public Item getContainerItem() {
      return this.containerItem;
   }

   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   public int getColorFromItemStack(ItemStack var1, int var2) {
      return 16777215;
   }

   public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
   }

   public void onCreated(ItemStack var1, World var2, EntityPlayer var3) {
   }

   public boolean isMap() {
      return false;
   }

   public EnumAction getItemUseAction(ItemStack var1) {
      return EnumAction.NONE;
   }

   public int getMaxItemUseDuration(ItemStack var1) {
      return 0;
   }

   public void onPlayerStoppedUsing(ItemStack var1, World var2, EntityPlayer var3, int var4) {
   }

   protected Item setPotionEffect(String var1) {
      this.potionEffect = var1;
      return this;
   }

   public String getPotionEffect(ItemStack var1) {
      return this.potionEffect;
   }

   public boolean isPotionIngredient(ItemStack var1) {
      return this.getPotionEffect(var1) != null;
   }

   public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
   }

   public String getItemStackDisplayName(ItemStack var1) {
      return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(var1) + ".name")).trim();
   }

   public boolean hasEffect(ItemStack var1) {
      return var1.isItemEnchanted();
   }

   public EnumRarity getRarity(ItemStack var1) {
      return var1.isItemEnchanted()?EnumRarity.RARE:EnumRarity.COMMON;
   }

   public boolean isItemTool(ItemStack var1) {
      return this.getItemStackLimit() == 1 && this.isDamageable();
   }

   protected MovingObjectPosition getMovingObjectPositionFromPlayer(World var1, EntityPlayer var2, boolean var3) {
      float var4 = var2.rotationPitch;
      float var5 = var2.rotationYaw;
      double var6 = var2.posX;
      double var8 = var2.posY + (double)var2.getEyeHeight();
      double var10 = var2.posZ;
      Vec3 var12 = new Vec3(var6, var8, var10);
      float var13 = MathHelper.cos(-var5 * 0.017453292F - 3.1415927F);
      float var14 = MathHelper.sin(-var5 * 0.017453292F - 3.1415927F);
      float var15 = -MathHelper.cos(-var4 * 0.017453292F);
      float var16 = MathHelper.sin(-var4 * 0.017453292F);
      float var17 = var14 * var15;
      float var18 = var13 * var15;
      double var19 = 5.0D;
      Vec3 var21 = var12.addVector((double)var17 * var19, (double)var16 * var19, (double)var18 * var19);
      return var1.rayTraceBlocks(var12, var21, var3, true, false);
   }

   public int getItemEnchantability() {
      return 0;
   }

   public void getSubItems(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs getCreativeTab() {
      return this.tabToDisplayOn;
   }

   public Item setCreativeTab(CreativeTabs var1) {
      this.tabToDisplayOn = var1;
      return this;
   }

   public boolean canItemEditBlocks() {
      return false;
   }

   public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
      return false;
   }

   public Multimap getItemAttributeModifiers() {
      return HashMultimap.create();
   }

   public static void registerItems() {
      registerItemBlock(Blocks.stone, (new ItemMultiTexture(Blocks.stone, Blocks.stone, Item::lambda$registerItems$0)).setUnlocalizedName("stone"));
      registerItemBlock(Blocks.grass, new ItemColored(Blocks.grass, false));
      registerItemBlock(Blocks.dirt, (new ItemMultiTexture(Blocks.dirt, Blocks.dirt, Item::lambda$registerItems$1)).setUnlocalizedName("dirt"));
      registerItemBlock(Blocks.cobblestone);
      registerItemBlock(Blocks.planks, (new ItemMultiTexture(Blocks.planks, Blocks.planks, Item::lambda$registerItems$2)).setUnlocalizedName("wood"));
      registerItemBlock(Blocks.sapling, (new ItemMultiTexture(Blocks.sapling, Blocks.sapling, Item::lambda$registerItems$3)).setUnlocalizedName("sapling"));
      registerItemBlock(Blocks.bedrock);
      registerItemBlock(Blocks.sand, (new ItemMultiTexture(Blocks.sand, Blocks.sand, Item::lambda$registerItems$4)).setUnlocalizedName("sand"));
      registerItemBlock(Blocks.gravel);
      registerItemBlock(Blocks.gold_ore);
      registerItemBlock(Blocks.iron_ore);
      registerItemBlock(Blocks.coal_ore);
      registerItemBlock(Blocks.log, (new ItemMultiTexture(Blocks.log, Blocks.log, Item::lambda$registerItems$5)).setUnlocalizedName("log"));
      registerItemBlock(Blocks.log2, (new ItemMultiTexture(Blocks.log2, Blocks.log2, Item::lambda$registerItems$6)).setUnlocalizedName("log"));
      registerItemBlock(Blocks.leaves, (new ItemLeaves(Blocks.leaves)).setUnlocalizedName("leaves"));
      registerItemBlock(Blocks.leaves2, (new ItemLeaves(Blocks.leaves2)).setUnlocalizedName("leaves"));
      registerItemBlock(Blocks.sponge, (new ItemMultiTexture(Blocks.sponge, Blocks.sponge, Item::lambda$registerItems$7)).setUnlocalizedName("sponge"));
      registerItemBlock(Blocks.glass);
      registerItemBlock(Blocks.lapis_ore);
      registerItemBlock(Blocks.lapis_block);
      registerItemBlock(Blocks.dispenser);
      registerItemBlock(Blocks.sandstone, (new ItemMultiTexture(Blocks.sandstone, Blocks.sandstone, Item::lambda$registerItems$8)).setUnlocalizedName("sandStone"));
      registerItemBlock(Blocks.noteblock);
      registerItemBlock(Blocks.golden_rail);
      registerItemBlock(Blocks.detector_rail);
      registerItemBlock(Blocks.sticky_piston, new ItemPiston(Blocks.sticky_piston));
      registerItemBlock(Blocks.web);
      registerItemBlock(Blocks.tallgrass, (new ItemColored(Blocks.tallgrass, true)).setSubtypeNames(new String[]{"shrub", "grass", "fern"}));
      registerItemBlock(Blocks.deadbush);
      registerItemBlock(Blocks.piston, new ItemPiston(Blocks.piston));
      registerItemBlock(Blocks.wool, (new ItemCloth(Blocks.wool)).setUnlocalizedName("cloth"));
      registerItemBlock(Blocks.yellow_flower, (new ItemMultiTexture(Blocks.yellow_flower, Blocks.yellow_flower, Item::lambda$registerItems$9)).setUnlocalizedName("flower"));
      registerItemBlock(Blocks.red_flower, (new ItemMultiTexture(Blocks.red_flower, Blocks.red_flower, Item::lambda$registerItems$10)).setUnlocalizedName("rose"));
      registerItemBlock(Blocks.brown_mushroom);
      registerItemBlock(Blocks.red_mushroom);
      registerItemBlock(Blocks.gold_block);
      registerItemBlock(Blocks.iron_block);
      registerItemBlock(Blocks.stone_slab, (new ItemSlab(Blocks.stone_slab, Blocks.stone_slab, Blocks.double_stone_slab)).setUnlocalizedName("stoneSlab"));
      registerItemBlock(Blocks.brick_block);
      registerItemBlock(Blocks.tnt);
      registerItemBlock(Blocks.bookshelf);
      registerItemBlock(Blocks.mossy_cobblestone);
      registerItemBlock(Blocks.obsidian);
      registerItemBlock(Blocks.torch);
      registerItemBlock(Blocks.mob_spawner);
      registerItemBlock(Blocks.oak_stairs);
      registerItemBlock(Blocks.chest);
      registerItemBlock(Blocks.diamond_ore);
      registerItemBlock(Blocks.diamond_block);
      registerItemBlock(Blocks.crafting_table);
      registerItemBlock(Blocks.farmland);
      registerItemBlock(Blocks.furnace);
      registerItemBlock(Blocks.lit_furnace);
      registerItemBlock(Blocks.ladder);
      registerItemBlock(Blocks.rail);
      registerItemBlock(Blocks.stone_stairs);
      registerItemBlock(Blocks.lever);
      registerItemBlock(Blocks.stone_pressure_plate);
      registerItemBlock(Blocks.wooden_pressure_plate);
      registerItemBlock(Blocks.redstone_ore);
      registerItemBlock(Blocks.redstone_torch);
      registerItemBlock(Blocks.stone_button);
      registerItemBlock(Blocks.snow_layer, new ItemSnow(Blocks.snow_layer));
      registerItemBlock(Blocks.ice);
      registerItemBlock(Blocks.snow);
      registerItemBlock(Blocks.cactus);
      registerItemBlock(Blocks.clay);
      registerItemBlock(Blocks.jukebox);
      registerItemBlock(Blocks.oak_fence);
      registerItemBlock(Blocks.spruce_fence);
      registerItemBlock(Blocks.birch_fence);
      registerItemBlock(Blocks.jungle_fence);
      registerItemBlock(Blocks.dark_oak_fence);
      registerItemBlock(Blocks.acacia_fence);
      registerItemBlock(Blocks.pumpkin);
      registerItemBlock(Blocks.netherrack);
      registerItemBlock(Blocks.soul_sand);
      registerItemBlock(Blocks.glowstone);
      registerItemBlock(Blocks.lit_pumpkin);
      registerItemBlock(Blocks.trapdoor);
      registerItemBlock(Blocks.monster_egg, (new ItemMultiTexture(Blocks.monster_egg, Blocks.monster_egg, Item::lambda$registerItems$11)).setUnlocalizedName("monsterStoneEgg"));
      registerItemBlock(Blocks.stonebrick, (new ItemMultiTexture(Blocks.stonebrick, Blocks.stonebrick, Item::lambda$registerItems$12)).setUnlocalizedName("stonebricksmooth"));
      registerItemBlock(Blocks.brown_mushroom_block);
      registerItemBlock(Blocks.red_mushroom_block);
      registerItemBlock(Blocks.iron_bars);
      registerItemBlock(Blocks.glass_pane);
      registerItemBlock(Blocks.melon_block);
      registerItemBlock(Blocks.vine, new ItemColored(Blocks.vine, false));
      registerItemBlock(Blocks.oak_fence_gate);
      registerItemBlock(Blocks.spruce_fence_gate);
      registerItemBlock(Blocks.birch_fence_gate);
      registerItemBlock(Blocks.jungle_fence_gate);
      registerItemBlock(Blocks.dark_oak_fence_gate);
      registerItemBlock(Blocks.acacia_fence_gate);
      registerItemBlock(Blocks.brick_stairs);
      registerItemBlock(Blocks.stone_brick_stairs);
      registerItemBlock(Blocks.mycelium);
      registerItemBlock(Blocks.waterlily, new ItemLilyPad(Blocks.waterlily));
      registerItemBlock(Blocks.nether_brick);
      registerItemBlock(Blocks.nether_brick_fence);
      registerItemBlock(Blocks.nether_brick_stairs);
      registerItemBlock(Blocks.enchanting_table);
      registerItemBlock(Blocks.end_portal_frame);
      registerItemBlock(Blocks.end_stone);
      registerItemBlock(Blocks.dragon_egg);
      registerItemBlock(Blocks.redstone_lamp);
      registerItemBlock(Blocks.wooden_slab, (new ItemSlab(Blocks.wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab)).setUnlocalizedName("woodSlab"));
      registerItemBlock(Blocks.sandstone_stairs);
      registerItemBlock(Blocks.emerald_ore);
      registerItemBlock(Blocks.ender_chest);
      registerItemBlock(Blocks.tripwire_hook);
      registerItemBlock(Blocks.emerald_block);
      registerItemBlock(Blocks.spruce_stairs);
      registerItemBlock(Blocks.birch_stairs);
      registerItemBlock(Blocks.jungle_stairs);
      registerItemBlock(Blocks.command_block);
      registerItemBlock(Blocks.beacon);
      registerItemBlock(Blocks.cobblestone_wall, (new ItemMultiTexture(Blocks.cobblestone_wall, Blocks.cobblestone_wall, Item::lambda$registerItems$13)).setUnlocalizedName("cobbleWall"));
      registerItemBlock(Blocks.wooden_button);
      registerItemBlock(Blocks.anvil, (new ItemAnvilBlock(Blocks.anvil)).setUnlocalizedName("anvil"));
      registerItemBlock(Blocks.trapped_chest);
      registerItemBlock(Blocks.light_weighted_pressure_plate);
      registerItemBlock(Blocks.heavy_weighted_pressure_plate);
      registerItemBlock(Blocks.daylight_detector);
      registerItemBlock(Blocks.redstone_block);
      registerItemBlock(Blocks.quartz_ore);
      registerItemBlock(Blocks.hopper);
      registerItemBlock(Blocks.quartz_block, (new ItemMultiTexture(Blocks.quartz_block, Blocks.quartz_block, new String[]{"default", "chiseled", "lines"})).setUnlocalizedName("quartzBlock"));
      registerItemBlock(Blocks.quartz_stairs);
      registerItemBlock(Blocks.activator_rail);
      registerItemBlock(Blocks.dropper);
      registerItemBlock(Blocks.stained_hardened_clay, (new ItemCloth(Blocks.stained_hardened_clay)).setUnlocalizedName("clayHardenedStained"));
      registerItemBlock(Blocks.barrier);
      registerItemBlock(Blocks.iron_trapdoor);
      registerItemBlock(Blocks.hay_block);
      registerItemBlock(Blocks.carpet, (new ItemCloth(Blocks.carpet)).setUnlocalizedName("woolCarpet"));
      registerItemBlock(Blocks.hardened_clay);
      registerItemBlock(Blocks.coal_block);
      registerItemBlock(Blocks.packed_ice);
      registerItemBlock(Blocks.acacia_stairs);
      registerItemBlock(Blocks.dark_oak_stairs);
      registerItemBlock(Blocks.slime_block);
      registerItemBlock(Blocks.double_plant, (new ItemDoublePlant(Blocks.double_plant, Blocks.double_plant, Item::lambda$registerItems$14)).setUnlocalizedName("doublePlant"));
      registerItemBlock(Blocks.stained_glass, (new ItemCloth(Blocks.stained_glass)).setUnlocalizedName("stainedGlass"));
      registerItemBlock(Blocks.stained_glass_pane, (new ItemCloth(Blocks.stained_glass_pane)).setUnlocalizedName("stainedGlassPane"));
      registerItemBlock(Blocks.prismarine, (new ItemMultiTexture(Blocks.prismarine, Blocks.prismarine, Item::lambda$registerItems$15)).setUnlocalizedName("prismarine"));
      registerItemBlock(Blocks.sea_lantern);
      registerItemBlock(Blocks.red_sandstone, (new ItemMultiTexture(Blocks.red_sandstone, Blocks.red_sandstone, Item::lambda$registerItems$16)).setUnlocalizedName("redSandStone"));
      registerItemBlock(Blocks.red_sandstone_stairs);
      registerItemBlock(Blocks.stone_slab2, (new ItemSlab(Blocks.stone_slab2, Blocks.stone_slab2, Blocks.double_stone_slab2)).setUnlocalizedName("stoneSlab2"));
      registerItem(256, (String)"iron_shovel", (new ItemSpade(Item$ToolMaterial.IRON)).setUnlocalizedName("shovelIron"));
      registerItem(257, (String)"iron_pickaxe", (new ItemPickaxe(Item$ToolMaterial.IRON)).setUnlocalizedName("pickaxeIron"));
      registerItem(258, (String)"iron_axe", (new ItemAxe(Item$ToolMaterial.IRON)).setUnlocalizedName("hatchetIron"));
      registerItem(259, (String)"flint_and_steel", (new ItemFlintAndSteel()).setUnlocalizedName("flintAndSteel"));
      registerItem(260, (String)"apple", (new ItemFood(4, 0.3F, false)).setUnlocalizedName("apple"));
      registerItem(261, (String)"bow", (new ItemBow()).setUnlocalizedName("bow"));
      registerItem(262, (String)"arrow", (new Item()).setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat));
      registerItem(263, (String)"coal", (new ItemCoal()).setUnlocalizedName("coal"));
      registerItem(264, (String)"diamond", (new Item()).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(265, (String)"iron_ingot", (new Item()).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(266, (String)"gold_ingot", (new Item()).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(267, (String)"iron_sword", (new ItemSword(Item$ToolMaterial.IRON)).setUnlocalizedName("swordIron"));
      registerItem(268, (String)"wooden_sword", (new ItemSword(Item$ToolMaterial.WOOD)).setUnlocalizedName("swordWood"));
      registerItem(269, (String)"wooden_shovel", (new ItemSpade(Item$ToolMaterial.WOOD)).setUnlocalizedName("shovelWood"));
      registerItem(270, (String)"wooden_pickaxe", (new ItemPickaxe(Item$ToolMaterial.WOOD)).setUnlocalizedName("pickaxeWood"));
      registerItem(271, (String)"wooden_axe", (new ItemAxe(Item$ToolMaterial.WOOD)).setUnlocalizedName("hatchetWood"));
      registerItem(272, (String)"stone_sword", (new ItemSword(Item$ToolMaterial.STONE)).setUnlocalizedName("swordStone"));
      registerItem(273, (String)"stone_shovel", (new ItemSpade(Item$ToolMaterial.STONE)).setUnlocalizedName("shovelStone"));
      registerItem(274, (String)"stone_pickaxe", (new ItemPickaxe(Item$ToolMaterial.STONE)).setUnlocalizedName("pickaxeStone"));
      registerItem(275, (String)"stone_axe", (new ItemAxe(Item$ToolMaterial.STONE)).setUnlocalizedName("hatchetStone"));
      registerItem(276, (String)"diamond_sword", (new ItemSword(Item$ToolMaterial.EMERALD)).setUnlocalizedName("swordDiamond"));
      registerItem(277, (String)"diamond_shovel", (new ItemSpade(Item$ToolMaterial.EMERALD)).setUnlocalizedName("shovelDiamond"));
      registerItem(278, (String)"diamond_pickaxe", (new ItemPickaxe(Item$ToolMaterial.EMERALD)).setUnlocalizedName("pickaxeDiamond"));
      registerItem(279, (String)"diamond_axe", (new ItemAxe(Item$ToolMaterial.EMERALD)).setUnlocalizedName("hatchetDiamond"));
      registerItem(280, (String)"stick", (new Item()).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(281, (String)"bowl", (new Item()).setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(282, (String)"mushroom_stew", (new ItemSoup(6)).setUnlocalizedName("mushroomStew"));
      registerItem(283, (String)"golden_sword", (new ItemSword(Item$ToolMaterial.GOLD)).setUnlocalizedName("swordGold"));
      registerItem(284, (String)"golden_shovel", (new ItemSpade(Item$ToolMaterial.GOLD)).setUnlocalizedName("shovelGold"));
      registerItem(285, (String)"golden_pickaxe", (new ItemPickaxe(Item$ToolMaterial.GOLD)).setUnlocalizedName("pickaxeGold"));
      registerItem(286, (String)"golden_axe", (new ItemAxe(Item$ToolMaterial.GOLD)).setUnlocalizedName("hatchetGold"));
      registerItem(287, (String)"string", (new ItemReed(Blocks.tripwire)).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(288, (String)"feather", (new Item()).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(289, (String)"gunpowder", (new Item()).setUnlocalizedName("sulphur").setPotionEffect("+14&13-13").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(290, (String)"wooden_hoe", (new ItemHoe(Item$ToolMaterial.WOOD)).setUnlocalizedName("hoeWood"));
      registerItem(291, (String)"stone_hoe", (new ItemHoe(Item$ToolMaterial.STONE)).setUnlocalizedName("hoeStone"));
      registerItem(292, (String)"iron_hoe", (new ItemHoe(Item$ToolMaterial.IRON)).setUnlocalizedName("hoeIron"));
      registerItem(293, (String)"diamond_hoe", (new ItemHoe(Item$ToolMaterial.EMERALD)).setUnlocalizedName("hoeDiamond"));
      registerItem(294, (String)"golden_hoe", (new ItemHoe(Item$ToolMaterial.GOLD)).setUnlocalizedName("hoeGold"));
      registerItem(295, (String)"wheat_seeds", (new ItemSeeds(Blocks.wheat, Blocks.farmland)).setUnlocalizedName("seeds"));
      registerItem(296, (String)"wheat", (new Item()).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(297, (String)"bread", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("bread"));
      registerItem(298, (String)"leather_helmet", (new ItemArmor(ItemArmor$ArmorMaterial.LEATHER, 0, 0)).setUnlocalizedName("helmetCloth"));
      registerItem(299, (String)"leather_chestplate", (new ItemArmor(ItemArmor$ArmorMaterial.LEATHER, 0, 1)).setUnlocalizedName("chestplateCloth"));
      registerItem(300, (String)"leather_leggings", (new ItemArmor(ItemArmor$ArmorMaterial.LEATHER, 0, 2)).setUnlocalizedName("leggingsCloth"));
      registerItem(301, (String)"leather_boots", (new ItemArmor(ItemArmor$ArmorMaterial.LEATHER, 0, 3)).setUnlocalizedName("bootsCloth"));
      registerItem(302, (String)"chainmail_helmet", (new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 0)).setUnlocalizedName("helmetChain"));
      registerItem(303, (String)"chainmail_chestplate", (new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 1)).setUnlocalizedName("chestplateChain"));
      registerItem(304, (String)"chainmail_leggings", (new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 2)).setUnlocalizedName("leggingsChain"));
      registerItem(305, (String)"chainmail_boots", (new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 3)).setUnlocalizedName("bootsChain"));
      registerItem(306, (String)"iron_helmet", (new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 0)).setUnlocalizedName("helmetIron"));
      registerItem(307, (String)"iron_chestplate", (new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 1)).setUnlocalizedName("chestplateIron"));
      registerItem(308, (String)"iron_leggings", (new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 2)).setUnlocalizedName("leggingsIron"));
      registerItem(309, (String)"iron_boots", (new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 3)).setUnlocalizedName("bootsIron"));
      registerItem(310, (String)"diamond_helmet", (new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 0)).setUnlocalizedName("helmetDiamond"));
      registerItem(311, (String)"diamond_chestplate", (new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 1)).setUnlocalizedName("chestplateDiamond"));
      registerItem(312, (String)"diamond_leggings", (new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 2)).setUnlocalizedName("leggingsDiamond"));
      registerItem(313, (String)"diamond_boots", (new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 3)).setUnlocalizedName("bootsDiamond"));
      registerItem(314, (String)"golden_helmet", (new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 0)).setUnlocalizedName("helmetGold"));
      registerItem(315, (String)"golden_chestplate", (new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 1)).setUnlocalizedName("chestplateGold"));
      registerItem(316, (String)"golden_leggings", (new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 2)).setUnlocalizedName("leggingsGold"));
      registerItem(317, (String)"golden_boots", (new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 3)).setUnlocalizedName("bootsGold"));
      registerItem(318, (String)"flint", (new Item()).setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(319, (String)"porkchop", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("porkchopRaw"));
      registerItem(320, (String)"cooked_porkchop", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("porkchopCooked"));
      registerItem(321, (String)"painting", (new ItemHangingEntity(EntityPainting.class)).setUnlocalizedName("painting"));
      registerItem(322, (String)"golden_apple", (new ItemAppleGold(4, 1.2F, false)).setAlwaysEdible().setPotionEffect(Potion.regeneration.getId(), 5, 1, 1.0F).setUnlocalizedName("appleGold"));
      registerItem(323, (String)"sign", (new ItemSign()).setUnlocalizedName("sign"));
      registerItem(324, (String)"wooden_door", (new ItemDoor(Blocks.oak_door)).setUnlocalizedName("doorOak"));
      Item var0 = (new ItemBucket(Blocks.air)).setUnlocalizedName("bucket").setMaxStackSize(16);
      registerItem(325, (String)"bucket", var0);
      registerItem(326, (String)"water_bucket", (new ItemBucket(Blocks.flowing_water)).setUnlocalizedName("bucketWater").setContainerItem(var0));
      registerItem(327, (String)"lava_bucket", (new ItemBucket(Blocks.flowing_lava)).setUnlocalizedName("bucketLava").setContainerItem(var0));
      registerItem(328, (String)"minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.RIDEABLE)).setUnlocalizedName("minecart"));
      registerItem(329, (String)"saddle", (new ItemSaddle()).setUnlocalizedName("saddle"));
      registerItem(330, (String)"iron_door", (new ItemDoor(Blocks.iron_door)).setUnlocalizedName("doorIron"));
      registerItem(331, (String)"redstone", (new ItemRedstone()).setUnlocalizedName("redstone").setPotionEffect("-5+6-7"));
      registerItem(332, (String)"snowball", (new ItemSnowball()).setUnlocalizedName("snowball"));
      registerItem(333, (String)"boat", (new ItemBoat()).setUnlocalizedName("boat"));
      registerItem(334, (String)"leather", (new Item()).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(335, (String)"milk_bucket", (new ItemBucketMilk()).setUnlocalizedName("milk").setContainerItem(var0));
      registerItem(336, (String)"brick", (new Item()).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(337, (String)"clay_ball", (new Item()).setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(338, (String)"reeds", (new ItemReed(Blocks.reeds)).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(339, (String)"paper", (new Item()).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc));
      registerItem(340, (String)"book", (new ItemBook()).setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc));
      registerItem(341, (String)"slime_ball", (new Item()).setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc));
      registerItem(342, (String)"chest_minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.CHEST)).setUnlocalizedName("minecartChest"));
      registerItem(343, (String)"furnace_minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.FURNACE)).setUnlocalizedName("minecartFurnace"));
      registerItem(344, (String)"egg", (new ItemEgg()).setUnlocalizedName("egg"));
      registerItem(345, (String)"compass", (new Item()).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools));
      registerItem(346, (String)"fishing_rod", (new ItemFishingRod()).setUnlocalizedName("fishingRod"));
      registerItem(347, (String)"clock", (new Item()).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools));
      registerItem(348, (String)"glowstone_dust", (new Item()).setUnlocalizedName("yellowDust").setPotionEffect("+5-6-7").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(349, (String)"fish", (new ItemFishFood(false)).setUnlocalizedName("fish").setHasSubtypes(true));
      registerItem(350, (String)"cooked_fish", (new ItemFishFood(true)).setUnlocalizedName("fish").setHasSubtypes(true));
      registerItem(351, (String)"dye", (new ItemDye()).setUnlocalizedName("dyePowder"));
      registerItem(352, (String)"bone", (new Item()).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc));
      registerItem(353, (String)"sugar", (new Item()).setUnlocalizedName("sugar").setPotionEffect("-0+1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(354, (String)"cake", (new ItemReed(Blocks.cake)).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood));
      registerItem(355, (String)"bed", (new ItemBed()).setMaxStackSize(1).setUnlocalizedName("bed"));
      registerItem(356, (String)"repeater", (new ItemReed(Blocks.unpowered_repeater)).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone));
      registerItem(357, (String)"cookie", (new ItemFood(2, 0.1F, false)).setUnlocalizedName("cookie"));
      registerItem(358, (String)"filled_map", (new ItemMap()).setUnlocalizedName("map"));
      registerItem(359, (String)"shears", (new ItemShears()).setUnlocalizedName("shears"));
      registerItem(360, (String)"melon", (new ItemFood(2, 0.3F, false)).setUnlocalizedName("melon"));
      registerItem(361, (String)"pumpkin_seeds", (new ItemSeeds(Blocks.pumpkin_stem, Blocks.farmland)).setUnlocalizedName("seeds_pumpkin"));
      registerItem(362, (String)"melon_seeds", (new ItemSeeds(Blocks.melon_stem, Blocks.farmland)).setUnlocalizedName("seeds_melon"));
      registerItem(363, (String)"beef", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("beefRaw"));
      registerItem(364, (String)"cooked_beef", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("beefCooked"));
      registerItem(365, (String)"chicken", (new ItemFood(2, 0.3F, true)).setPotionEffect(Potion.hunger.getId(), 30, 0, 0.3F).setUnlocalizedName("chickenRaw"));
      registerItem(366, (String)"cooked_chicken", (new ItemFood(6, 0.6F, true)).setUnlocalizedName("chickenCooked"));
      registerItem(367, (String)"rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.getId(), 30, 0, 0.8F).setUnlocalizedName("rottenFlesh"));
      registerItem(368, (String)"ender_pearl", (new ItemEnderPearl()).setUnlocalizedName("enderPearl"));
      registerItem(369, (String)"blaze_rod", (new Item()).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials).setFull3D());
      registerItem(370, (String)"ghast_tear", (new Item()).setUnlocalizedName("ghastTear").setPotionEffect("+0-1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(371, (String)"gold_nugget", (new Item()).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(372, (String)"nether_wart", (new ItemSeeds(Blocks.nether_wart, Blocks.soul_sand)).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4"));
      registerItem(373, (String)"potion", (new ItemPotion()).setUnlocalizedName("potion"));
      registerItem(374, (String)"glass_bottle", (new ItemGlassBottle()).setUnlocalizedName("glassBottle"));
      registerItem(375, (String)"spider_eye", (new ItemFood(2, 0.8F, false)).setPotionEffect(Potion.poison.getId(), 5, 0, 1.0F).setUnlocalizedName("spiderEye").setPotionEffect("-0-1+2-3&4-4+13"));
      registerItem(376, (String)"fermented_spider_eye", (new Item()).setUnlocalizedName("fermentedSpiderEye").setPotionEffect("-0+3-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(377, (String)"blaze_powder", (new Item()).setUnlocalizedName("blazePowder").setPotionEffect("+0-1-2+3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(378, (String)"magma_cream", (new Item()).setUnlocalizedName("magmaCream").setPotionEffect("+0+1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(379, (String)"brewing_stand", (new ItemReed(Blocks.brewing_stand)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(380, (String)"cauldron", (new ItemReed(Blocks.cauldron)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(381, (String)"ender_eye", (new ItemEnderEye()).setUnlocalizedName("eyeOfEnder"));
      registerItem(382, (String)"speckled_melon", (new Item()).setUnlocalizedName("speckledMelon").setPotionEffect("+0-1+2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(383, (String)"spawn_egg", (new ItemMonsterPlacer()).setUnlocalizedName("monsterPlacer"));
      registerItem(384, (String)"experience_bottle", (new ItemExpBottle()).setUnlocalizedName("expBottle"));
      registerItem(385, (String)"fire_charge", (new ItemFireball()).setUnlocalizedName("fireball"));
      registerItem(386, (String)"writable_book", (new a5z()).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc));
      registerItem(387, (String)"written_book", (new ItemEditableBook()).setUnlocalizedName("writtenBook").setMaxStackSize(16));
      registerItem(388, (String)"emerald", (new Item()).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(389, (String)"item_frame", (new ItemHangingEntity(EntityItemFrame.class)).setUnlocalizedName("frame"));
      registerItem(390, (String)"flower_pot", (new ItemReed(Blocks.flower_pot)).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations));
      registerItem(391, (String)"carrot", (new ItemSeedFood(3, 0.6F, Blocks.carrots, Blocks.farmland)).setUnlocalizedName("carrots"));
      registerItem(392, (String)"potato", (new ItemSeedFood(1, 0.3F, Blocks.potatoes, Blocks.farmland)).setUnlocalizedName("potato"));
      registerItem(393, (String)"baked_potato", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("potatoBaked"));
      registerItem(394, (String)"poisonous_potato", (new ItemFood(2, 0.3F, false)).setPotionEffect(Potion.poison.getId(), 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous"));
      registerItem(395, (String)"map", (new ItemEmptyMap()).setUnlocalizedName("emptyMap"));
      registerItem(396, (String)"golden_carrot", (new ItemFood(6, 1.2F, false)).setUnlocalizedName("carrotGolden").setPotionEffect("-0+1+2-3+13&4-4").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(397, (String)"skull", (new ItemSkull()).setUnlocalizedName("skull"));
      registerItem(398, (String)"carrot_on_a_stick", (new ItemCarrotOnAStick()).setUnlocalizedName("carrotOnAStick"));
      registerItem(399, (String)"nether_star", (new ItemSimpleFoiled()).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(400, (String)"pumpkin_pie", (new ItemFood(8, 0.3F, false)).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood));
      registerItem(401, (String)"fireworks", (new ItemFirework()).setUnlocalizedName("fireworks"));
      registerItem(402, (String)"firework_charge", (new ItemFireworkCharge()).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc));
      registerItem(403, (String)"enchanted_book", (new ItemEnchantedBook()).setMaxStackSize(1).setUnlocalizedName("enchantedBook"));
      registerItem(404, (String)"comparator", (new ItemReed(Blocks.unpowered_comparator)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone));
      registerItem(405, (String)"netherbrick", (new Item()).setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(406, (String)"quartz", (new Item()).setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(407, (String)"tnt_minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.TNT)).setUnlocalizedName("minecartTnt"));
      registerItem(408, (String)"hopper_minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.HOPPER)).setUnlocalizedName("minecartHopper"));
      registerItem(409, (String)"prismarine_shard", (new Item()).setUnlocalizedName("prismarineShard").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(410, (String)"prismarine_crystals", (new Item()).setUnlocalizedName("prismarineCrystals").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(411, (String)"rabbit", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("rabbitRaw"));
      registerItem(412, (String)"cooked_rabbit", (new ItemFood(5, 0.6F, true)).setUnlocalizedName("rabbitCooked"));
      registerItem(413, (String)"rabbit_stew", (new ItemSoup(10)).setUnlocalizedName("rabbitStew"));
      registerItem(414, (String)"rabbit_foot", (new Item()).setUnlocalizedName("rabbitFoot").setPotionEffect("+0+1-2+3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing));
      registerItem(415, (String)"rabbit_hide", (new Item()).setUnlocalizedName("rabbitHide").setCreativeTab(CreativeTabs.tabMaterials));
      registerItem(416, (String)"armor_stand", (new ajY()).setUnlocalizedName("armorStand").setMaxStackSize(16));
      registerItem(417, (String)"iron_horse_armor", (new Item()).setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
      registerItem(418, (String)"golden_horse_armor", (new Item()).setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
      registerItem(419, (String)"diamond_horse_armor", (new Item()).setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
      registerItem(420, (String)"lead", (new ItemLead()).setUnlocalizedName("leash"));
      registerItem(421, (String)"name_tag", (new ItemNameTag()).setUnlocalizedName("nameTag"));
      registerItem(422, (String)"command_block_minecart", (new ItemMinecart(EntityMinecart$EnumMinecartType.COMMAND_BLOCK)).setUnlocalizedName("minecartCommandBlock").setCreativeTab((CreativeTabs)null));
      registerItem(423, (String)"mutton", (new ItemFood(2, 0.3F, true)).setUnlocalizedName("muttonRaw"));
      registerItem(424, (String)"cooked_mutton", (new ItemFood(6, 0.8F, true)).setUnlocalizedName("muttonCooked"));
      registerItem(425, (String)"banner", (new ItemBanner()).setUnlocalizedName("banner"));
      registerItem(427, (String)"spruce_door", (new ItemDoor(Blocks.spruce_door)).setUnlocalizedName("doorSpruce"));
      registerItem(428, (String)"birch_door", (new ItemDoor(Blocks.birch_door)).setUnlocalizedName("doorBirch"));
      registerItem(429, (String)"jungle_door", (new ItemDoor(Blocks.jungle_door)).setUnlocalizedName("doorJungle"));
      registerItem(430, (String)"acacia_door", (new ItemDoor(Blocks.acacia_door)).setUnlocalizedName("doorAcacia"));
      registerItem(431, (String)"dark_oak_door", (new ItemDoor(Blocks.dark_oak_door)).setUnlocalizedName("doorDarkOak"));
      registerItem(2256, (String)"record_13", (new ItemRecord("13")).setUnlocalizedName("record"));
      registerItem(2257, (String)"record_cat", (new ItemRecord("cat")).setUnlocalizedName("record"));
      registerItem(2258, (String)"record_blocks", (new ItemRecord("blocks")).setUnlocalizedName("record"));
      registerItem(2259, (String)"record_chirp", (new ItemRecord("chirp")).setUnlocalizedName("record"));
      registerItem(2260, (String)"record_far", (new ItemRecord("far")).setUnlocalizedName("record"));
      registerItem(2261, (String)"record_mall", (new ItemRecord("mall")).setUnlocalizedName("record"));
      registerItem(2262, (String)"record_mellohi", (new ItemRecord("mellohi")).setUnlocalizedName("record"));
      registerItem(2263, (String)"record_stal", (new ItemRecord("stal")).setUnlocalizedName("record"));
      registerItem(2264, (String)"record_strad", (new ItemRecord("strad")).setUnlocalizedName("record"));
      registerItem(2265, (String)"record_ward", (new ItemRecord("ward")).setUnlocalizedName("record"));
      registerItem(2266, (String)"record_11", (new ItemRecord("11")).setUnlocalizedName("record"));
      registerItem(2267, (String)"record_wait", (new ItemRecord("wait")).setUnlocalizedName("record"));
   }

   private static void registerItemBlock(Block var0) {
      registerItemBlock(var0, new ItemBlock(var0));
   }

   protected static void registerItemBlock(Block var0, Item var1) {
      registerItem(Block.getIdFromBlock(var0), (ResourceLocation)Block.blockRegistry.getNameForObject(var0), var1);
      BLOCK_TO_ITEM.put(var0, var1);
   }

   private static void registerItem(int var0, String var1, Item var2) {
      registerItem(var0, new ResourceLocation(var1), var2);
   }

   private static void registerItem(int var0, ResourceLocation var1, Item var2) {
      itemRegistry.register(var0, var1, var2);
   }

   private static String lambda$registerItems$16(ItemStack var0) {
      return BlockRedSandstone$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$15(ItemStack var0) {
      return BlockPrismarine$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$14(ItemStack var0) {
      return BlockDoublePlant$EnumPlantType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$13(ItemStack var0) {
      return BlockWall$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$12(ItemStack var0) {
      return BlockStoneBrick$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$11(ItemStack var0) {
      return aXp.a(var0.getMetadata()).b();
   }

   private static String lambda$registerItems$10(ItemStack var0) {
      return BlockFlower$EnumFlowerType.getType(BlockFlower$EnumFlowerColor.RED, var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$9(ItemStack var0) {
      return BlockFlower$EnumFlowerType.getType(BlockFlower$EnumFlowerColor.YELLOW, var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$8(ItemStack var0) {
      return BlockSandStone$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$7(ItemStack var0) {
      return (var0.getMetadata() & 1) == 1?"wet":"dry";
   }

   private static String lambda$registerItems$6(ItemStack var0) {
      return BlockPlanks$EnumType.byMetadata(var0.getMetadata() + 4).getUnlocalizedName();
   }

   private static String lambda$registerItems$5(ItemStack var0) {
      return BlockPlanks$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$4(ItemStack var0) {
      return BlockSand$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$3(ItemStack var0) {
      return BlockPlanks$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$2(ItemStack var0) {
      return BlockPlanks$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$1(ItemStack var0) {
      return BlockDirt$DirtType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static String lambda$registerItems$0(ItemStack var0) {
      return BlockStone$EnumType.byMetadata(var0.getMetadata()).getUnlocalizedName();
   }

   private static NumberFormatException a(NumberFormatException var0) {
      return var0;
   }
}
