package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.nA;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer$1;
import net.minecraft.world.WorldServer$ServerBlockEventList;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   private static final Logger aa = LogManager.getLogger();
   private final MinecraftServer mcServer;
   private final EntityTracker theEntityTracker;
   private final PlayerManager thePlayerManager;
   private final Set pendingTickListEntriesHashSet = Sets.newHashSet();
   private final TreeSet pendingTickListEntriesTreeSet = new TreeSet();
   private final Map entitiesByUuid = Maps.newHashMap();
   public ChunkProviderServer theChunkProviderServer;
   public boolean disableLevelSaving;
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   private final Teleporter worldTeleporter;
   private final SpawnerAnimals mobSpawner = new SpawnerAnimals();
   protected final VillageSiege villageSiege = new VillageSiege(this);
   private final WorldServer$ServerBlockEventList[] field_147490_S = new WorldServer$ServerBlockEventList[]{new WorldServer$ServerBlockEventList((WorldServer$1)null), new WorldServer$ServerBlockEventList((WorldServer$1)null)};
   private int blockEventCacheIndex;
   private static final List bonusChestContent = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10), new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)});
   private final List pendingTickListEntriesThisTick = Lists.newArrayList();

   public WorldServer(MinecraftServer var1, ISaveHandler var2, WorldInfo var3, int var4, Profiler var5) {
      super(var2, var3, WorldProvider.getProviderForDimension(var4), var5, false);
      this.mcServer = var1;
      this.theEntityTracker = new EntityTracker(this);
      this.thePlayerManager = new PlayerManager(this);
      this.provider.registerWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(var1.getMaxWorldSize());
   }

   public World init() {
      this.mapStorage = new MapStorage(this.saveHandler);
      String var1 = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection var2 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, var1);
      this.villageCollectionObj = new VillageCollection(this);
      this.mapStorage.setData(var1, this.villageCollectionObj);
      this.worldScoreboard = new ServerScoreboard(this.mcServer);
      ScoreboardSaveData var3 = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
      var3 = new ScoreboardSaveData();
      this.mapStorage.setData("scoreboard", var3);
      var3.setScoreboard(this.worldScoreboard);
      ((ServerScoreboard)this.worldScoreboard).func_96547_a(var3);
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if(this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      return this;
   }

   public void tick() {
      super.tick();
      if(this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.provider.getWorldChunkManager().cleanupCache();
      if(this.areAllPlayersAsleep()) {
         if(this.getGameRules().getBoolean("doDaylightCycle")) {
            long var1 = this.worldInfo.getWorldTime() + 24000L;
            this.worldInfo.setWorldTime(var1 - var1 % 24000L);
         }

         this.wakeAllPlayers();
      }

      this.theProfiler.startSection("mobSpawner");
      if(this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.mobSpawner.a(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
      }

      this.theProfiler.endStartSection("chunkSource");
      this.chunkProvider.unloadQueuedChunks();
      int var3 = this.calculateSkylightSubtracted(1.0F);
      if(var3 != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(var3);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);
      if(this.getGameRules().getBoolean("doDaylightCycle")) {
         this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
      }

      this.theProfiler.endStartSection("tickPending");
      this.tickUpdates(false);
      this.theProfiler.endStartSection("tickBlocks");
      this.updateBlocks();
      this.theProfiler.endStartSection("chunkMap");
      this.thePlayerManager.updatePlayerInstances();
      this.theProfiler.endStartSection("village");
      this.villageCollectionObj.tick();
      this.villageSiege.tick();
      this.theProfiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      this.theProfiler.endSection();
      this.sendQueuedBlockEvents();
   }

   public nA a(EnumCreatureType var1, BlockPos var2) {
      List var3 = this.getChunkProvider().getPossibleCreatures(var1, var2);
      return !var3.isEmpty()?(nA)WeightedRandom.getRandomItem(this.rand, var3):null;
   }

   public boolean a(EnumCreatureType var1, nA var2, BlockPos var3) {
      List var4 = this.getChunkProvider().getPossibleCreatures(var1, var3);
      return !var4.isEmpty() && var4.contains(var2);
   }

   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if(!this.getPlayerEntities().isEmpty()) {
         int var1 = 0;
         int var2 = 0;

         for(EntityPlayer var4 : this.getPlayerEntities()) {
            if(var4.isSpectator()) {
               ++var1;
            } else if(var4.isPlayerSleeping()) {
               ++var2;
            }
         }

         this.allPlayersSleeping = var2 >= this.getPlayerEntities().size() - var1;
      }

   }

   protected void wakeAllPlayers() {
      this.allPlayersSleeping = false;

      for(EntityPlayer var2 : this.getPlayerEntities()) {
         if(var2.isPlayerSleeping()) {
            var2.wakeUpPlayer(false, false, true);
         }
      }

      this.resetRainAndThunder();
   }

   private void resetRainAndThunder() {
      this.worldInfo.setRainTime(0);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThunderTime(0);
      this.worldInfo.setThundering(false);
   }

   public boolean areAllPlayersAsleep() {
      if(this.allPlayersSleeping && !this.isRemote) {
         for(EntityPlayer var2 : this.getPlayerEntities()) {
            if(var2.isSpectator() || !var2.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void setInitialSpawnLocation() {
      if(this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.func_181545_F() + 1);
      }

      int var1 = this.worldInfo.getSpawnX();
      int var2 = this.worldInfo.getSpawnZ();
      int var3 = 0;

      while(this.getGroundAboveSeaLevel(new BlockPos(var1, 0, var2)).getMaterial() == Material.air) {
         var1 += this.rand.nextInt(8) - this.rand.nextInt(8);
         var2 += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++var3;
         if(var3 == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(var1);
      this.worldInfo.setSpawnZ(var2);
   }

   protected void updateBlocks() {
      super.updateBlocks();
      if(this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         for(ChunkCoordIntPair var2 : this.activeChunkSet) {
            this.getChunkFromChunkCoords(var2.chunkXPos, var2.chunkZPos).func_150804_b(false);
         }
      } else {
         int var20 = 0;
         int var21 = 0;

         for(ChunkCoordIntPair var4 : this.activeChunkSet) {
            int var5 = var4.chunkXPos * 16;
            int var6 = var4.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
            this.playMoodSoundAndCheckLight(var5, var6, var7);
            this.theProfiler.endStartSection("tickChunk");
            var7.func_150804_b(false);
            this.theProfiler.endStartSection("thunder");
            if(this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int var8 = this.updateLCG >> 2;
               BlockPos var9 = this.adjustPosToNearbyEntity(new BlockPos(var5 + (var8 & 15), 0, var6 + (var8 >> 8 & 15)));
               if(this.canLightningStrike(var9)) {
                  this.addWeatherEffect(new EntityLightningBolt(this, (double)var9.getX(), (double)var9.getY(), (double)var9.getZ()));
               }
            }

            this.theProfiler.endStartSection("iceandsnow");
            if(this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int var22 = this.updateLCG >> 2;
               BlockPos var24 = this.getPrecipitationHeight(new BlockPos(var5 + (var22 & 15), 0, var6 + (var22 >> 8 & 15)));
               BlockPos var10 = var24.down();
               if(this.canBlockFreezeNoWater(var10)) {
                  this.setBlockState(var10, Blocks.ice.getDefaultState());
               }

               if(this.isRaining() && this.f(var24, true)) {
                  this.setBlockState(var24, Blocks.snow_layer.getDefaultState());
               }

               if(this.isRaining() && this.getBiomeGenForCoords(var10).canSpawnLightningBolt()) {
                  this.getBlockState(var10).getBlock().fillWithRain(this, var10);
               }
            }

            this.theProfiler.endStartSection("tickBlocks");
            int var23 = this.getGameRules().getInt("randomTickSpeed");

            for(ExtendedBlockStorage var12 : var7.getBlockStorageArray()) {
               if(var12.getNeedsRandomTick()) {
                  for(int var13 = 0; var13 < var23; ++var13) {
                     this.updateLCG = this.updateLCG * 3 + 1013904223;
                     int var14 = this.updateLCG >> 2;
                     int var15 = var14 & 15;
                     int var16 = var14 >> 8 & 15;
                     int var17 = var14 >> 16 & 15;
                     ++var21;
                     IBlockState var18 = var12.get(var15, var17, var16);
                     Block var19 = var18.getBlock();
                     if(var19.getTickRandomly()) {
                        ++var20;
                        var19.randomTick(this, new BlockPos(var15 + var5, var17 + var12.getYLocation(), var16 + var6), var18, this.rand);
                     }
                  }
               }
            }

            this.theProfiler.endSection();
         }
      }

   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos var1) {
      BlockPos var2 = this.getPrecipitationHeight(var1);
      AxisAlignedBB var3 = (new AxisAlignedBB(var2, new BlockPos(var2.getX(), this.getHeight(), var2.getZ()))).expand(3.0D, 3.0D, 3.0D);
      List var4 = this.getEntitiesWithinAABB(EntityLivingBase.class, var3, new WorldServer$1(this));
      return !var4.isEmpty()?((EntityLivingBase)var4.get(this.rand.nextInt(var4.size()))).getPosition():var2;
   }

   public boolean isBlockTickPending(BlockPos var1, Block var2) {
      NextTickListEntry var3 = new NextTickListEntry(var1, var2);
      return this.pendingTickListEntriesThisTick.contains(var3);
   }

   public void scheduleUpdate(BlockPos var1, Block var2, int var3) {
      this.updateBlockTick(var1, var2, var3, 0);
   }

   public void updateBlockTick(BlockPos var1, Block var2, int var3, int var4) {
      NextTickListEntry var5 = new NextTickListEntry(var1, var2);
      byte var6 = 0;
      if(this.scheduledUpdatesAreImmediate && var2.getMaterial() != Material.air) {
         if(var2.requiresUpdates()) {
            var6 = 8;
            if(this.isAreaLoaded(var5.position.a(-var6, -var6, -var6), var5.position.a(var6, var6, var6))) {
               IBlockState var7 = this.getBlockState(var5.position);
               if(var7.getBlock().getMaterial() != Material.air && var7.getBlock() == var5.getBlock()) {
                  var7.getBlock().updateTick(this, var5.position, var7, this.rand);
               }
            }

            return;
         }

         var3 = 1;
      }

      if(this.isAreaLoaded(var1.a(-var6, -var6, -var6), var1.a(var6, var6, var6))) {
         if(var2.getMaterial() != Material.air) {
            var5.setScheduledTime((long)var3 + this.worldInfo.getWorldTotalTime());
            var5.setPriority(var4);
         }

         if(!this.pendingTickListEntriesHashSet.contains(var5)) {
            this.pendingTickListEntriesHashSet.add(var5);
            this.pendingTickListEntriesTreeSet.add(var5);
         }
      }

   }

   public void scheduleBlockUpdate(BlockPos var1, Block var2, int var3, int var4) {
      NextTickListEntry var5 = new NextTickListEntry(var1, var2);
      var5.setPriority(var4);
      if(var2.getMaterial() != Material.air) {
         var5.setScheduledTime((long)var3 + this.worldInfo.getWorldTotalTime());
      }

      if(!this.pendingTickListEntriesHashSet.contains(var5)) {
         this.pendingTickListEntriesHashSet.add(var5);
         this.pendingTickListEntriesTreeSet.add(var5);
      }

   }

   public void updateEntities() {
      if(this.getPlayerEntities().isEmpty()) {
         if(this.updateEntityTick++ >= 1200) {
            return;
         }
      } else {
         this.resetUpdateEntityTick();
      }

      super.updateEntities();
   }

   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   public void tickUpdates(boolean var1) {
      if(this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         int var2 = this.pendingTickListEntriesTreeSet.size();
         if(var2 != this.pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
         }

         if(var2 > 1000) {
            var2 = 1000;
         }

         this.theProfiler.startSection("cleaning");

         for(int var3 = 0; var3 < var2; ++var3) {
            NextTickListEntry var4 = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();
            if(var4.scheduledTime > this.worldInfo.getWorldTotalTime()) {
               break;
            }

            this.pendingTickListEntriesTreeSet.remove(var4);
            this.pendingTickListEntriesHashSet.remove(var4);
            this.pendingTickListEntriesThisTick.add(var4);
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("ticking");
         Iterator var11 = this.pendingTickListEntriesThisTick.iterator();

         while(var11.hasNext()) {
            NextTickListEntry var12 = (NextTickListEntry)var11.next();
            var11.remove();
            boolean var5 = false;
            if(this.isAreaLoaded(var12.position.a(0, 0, 0), var12.position.a(0, 0, 0))) {
               IBlockState var6 = this.getBlockState(var12.position);
               if(var6.getBlock().getMaterial() != Material.air && Block.isEqualTo(var6.getBlock(), var12.getBlock())) {
                  IBlockState var10000 = var6;

                  try {
                     var10000.getBlock().updateTick(this, var12.position, var6, this.rand);
                  } catch (Throwable var10) {
                     CrashReport var8 = CrashReport.makeCrashReport(var10, "Exception while ticking a block");
                     CrashReportCategory var9 = var8.makeCategory("Block being ticked");
                     CrashReportCategory.addBlockInfo(var9, var12.position, var6);
                     throw new ReportedException(var8);
                  }
               }
            } else {
               this.scheduleUpdate(var12.position, var12.getBlock(), 0);
            }
         }

         this.theProfiler.endSection();
         this.pendingTickListEntriesThisTick.clear();
      }

   }

   public List getPendingBlockUpdates(Chunk var1, boolean var2) {
      ChunkCoordIntPair var3 = var1.getChunkCoordIntPair();
      int var4 = (var3.chunkXPos << 4) - 2;
      int var5 = var4 + 16 + 2;
      int var6 = (var3.chunkZPos << 4) - 2;
      int var7 = var6 + 16 + 2;
      return this.func_175712_a(new StructureBoundingBox(var4, 0, var6, var5, 256, var7), var2);
   }

   public List func_175712_a(StructureBoundingBox var1, boolean var2) {
      ArrayList var3 = null;

      for(int var4 = 0; var4 < 2; ++var4) {
         Iterator var5 = this.pendingTickListEntriesTreeSet.iterator();

         while(var5.hasNext()) {
            NextTickListEntry var6 = (NextTickListEntry)var5.next();
            BlockPos var7 = var6.position;
            if(var7.getX() >= var1.minX && var7.getX() < var1.maxX && var7.getZ() >= var1.minZ && var7.getZ() < var1.maxZ) {
               this.pendingTickListEntriesHashSet.remove(var6);
               var5.remove();
               var3 = Lists.newArrayList();
               var3.add(var6);
            }
         }
      }

      return var3;
   }

   public void updateEntityWithOptionalForce(Entity var1, boolean var2) {
      if(!this.canSpawnAnimals() && (var1 instanceof EntityAnimal || var1 instanceof EntityWaterMob)) {
         var1.setDead();
      }

      if(!this.canSpawnNPCs() && var1 instanceof INpc) {
         var1.setDead();
      }

      super.updateEntityWithOptionalForce(var1, var2);
   }

   private boolean canSpawnNPCs() {
      return this.mcServer.getCanSpawnNPCs();
   }

   private boolean canSpawnAnimals() {
      return this.mcServer.getCanSpawnAnimals();
   }

   protected IChunkProvider createChunkProvider() {
      IChunkLoader var1 = this.saveHandler.getChunkLoader(this.provider);
      this.theChunkProviderServer = new ChunkProviderServer(this, var1, this.provider.createChunkGenerator());
      return this.theChunkProviderServer;
   }

   public List getTileEntitiesIn(int var1, int var2, int var3, int var4, int var5, int var6) {
      ArrayList var7 = Lists.newArrayList();

      for(TileEntity var9 : this.getLoadedTileEntityList()) {
         BlockPos var11 = var9.getPos();
         if(var11.getX() >= var1 && var11.getY() >= var2 && var11.getZ() >= var3 && var11.getX() < var4 && var11.getY() < var5 && var11.getZ() < var6) {
            var7.add(var9);
         }
      }

      return var7;
   }

   public boolean isBlockModifiable(EntityPlayer var1, BlockPos var2) {
      return !this.mcServer.isBlockProtected(this, var2, var1) && this.getWorldBorder().contains(var2);
   }

   public void initialize(WorldSettings param1) {
      // $FF: Couldn't be decompiled
   }

   private void setDebugWorldSettings() {
      this.worldInfo.setMapFeaturesEnabled(false);
      this.worldInfo.setAllowCommands(true);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setCleanWeatherTime(1000000000);
      this.worldInfo.setWorldTime(6000L);
      this.worldInfo.setGameType(WorldSettings$GameType.SPECTATOR);
      this.worldInfo.setHardcore(false);
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
   }

   private void createSpawnPosition(WorldSettings var1) {
      if(!this.provider.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.provider.getAverageGroundLevel()));
      } else if(this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         this.findingSpawnPoint = true;
         WorldChunkManager var2 = this.provider.getWorldChunkManager();
         List var3 = var2.getBiomesToSpawnIn();
         Random var4 = new Random(this.getSeed());
         BlockPos var5 = var2.findBiomePosition(0, 0, 256, var3, var4);
         int var6 = 0;
         int var7 = this.provider.getAverageGroundLevel();
         int var8 = 0;
         var6 = var5.getX();
         var8 = var5.getZ();
         int var9 = 0;

         while(!this.provider.canCoordinateBeSpawn(var6, var8)) {
            var6 += var4.nextInt(64) - var4.nextInt(64);
            var8 += var4.nextInt(64) - var4.nextInt(64);
            ++var9;
            if(var9 == 1000) {
               break;
            }
         }

         this.worldInfo.setSpawn(new BlockPos(var6, var7, var8));
         this.findingSpawnPoint = false;
         if(var1.isBonusChestEnabled()) {
            this.createBonusChest();
         }
      }

   }

   protected void createBonusChest() {
      WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(bonusChestContent, 10);

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos var5 = this.getTopSolidOrLiquidBlock(new BlockPos(var3, 0, var4)).up();
         if(var1.generate(this, this.rand, var5)) {
            break;
         }
      }

   }

   public BlockPos getSpawnCoordinate() {
      return this.provider.getSpawnCoordinate();
   }

   public void saveAllChunks(boolean var1, IProgressUpdate var2) throws MinecraftException {
      if(this.chunkProvider.canSave()) {
         var2.displaySavingString("Saving level");
         this.saveLevel();
         var2.displayLoadingString("Saving chunks");
         this.chunkProvider.saveChunks(var1, var2);

         for(Chunk var4 : Lists.newArrayList(this.theChunkProviderServer.func_152380_a())) {
            if(!this.thePlayerManager.hasPlayerInstance(var4.xPosition, var4.zPosition)) {
               this.theChunkProviderServer.dropChunk(var4.xPosition, var4.zPosition);
            }
         }
      }

   }

   public void saveChunkData() {
      if(this.chunkProvider.canSave()) {
         this.chunkProvider.saveExtraData();
      }

   }

   protected void saveLevel() throws MinecraftException {
      this.checkSessionLock();
      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
      this.mapStorage.saveAllData();
   }

   protected void onEntityAdded(Entity var1) {
      super.onEntityAdded(var1);
      this.entitiesById.addKey(var1.getEntityID(), var1);
      this.entitiesByUuid.put(var1.getUniqueID(), var1);
      Entity[] var2 = var1.getParts();

      for(Entity var6 : var2) {
         this.entitiesById.addKey(var6.getEntityID(), var6);
      }

   }

   protected void onEntityRemoved(Entity var1) {
      super.onEntityRemoved(var1);
      this.entitiesById.removeObject(var1.getEntityID());
      this.entitiesByUuid.remove(var1.getUniqueID());
      Entity[] var2 = var1.getParts();

      for(Entity var6 : var2) {
         this.entitiesById.removeObject(var6.getEntityID());
      }

   }

   public boolean addWeatherEffect(Entity var1) {
      if(super.addWeatherEffect(var1)) {
         this.mcServer.getConfigurationManager().sendToAllNear(var1.posX, var1.posY, var1.posZ, 512.0D, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(var1));
         return true;
      } else {
         return false;
      }
   }

   public void setEntityState(Entity var1, byte var2) {
      this.getEntityTracker().func_151248_b(var1, new S19PacketEntityStatus(var1, var2));
   }

   public void newExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
      Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8, var9, var10);
      var11.doExplosionA();
      var11.doExplosionB(false);
      var11.func_180342_d();

      for(EntityPlayer var13 : this.getPlayerEntities()) {
         if(var13.getDistanceSq(var2, var4, var6) < 4096.0D) {
            ((EntityPlayerMP)var13).playerNetServerHandler.sendPacket(new S27PacketExplosion(var2, var4, var6, var8, var11.getAffectedBlockPositions(), (Vec3)var11.getPlayerKnockbackMap().get(var13)));
         }
      }

   }

   public void addBlockEvent(BlockPos var1, Block var2, int var3, int var4) {
      BlockEventData var5 = new BlockEventData(var1, var2, var3, var4);

      for(BlockEventData var7 : this.field_147490_S[this.blockEventCacheIndex]) {
         if(var7.equals(var5)) {
            return;
         }
      }

      this.field_147490_S[this.blockEventCacheIndex].add(var5);
   }

   private void sendQueuedBlockEvents() {
      while(!this.field_147490_S[this.blockEventCacheIndex].isEmpty()) {
         int var1 = this.blockEventCacheIndex;
         this.blockEventCacheIndex ^= 1;

         for(BlockEventData var3 : this.field_147490_S[var1]) {
            if(this.fireBlockEvent(var3)) {
               this.mcServer.getConfigurationManager().sendToAllNear((double)var3.getPosition().getX(), (double)var3.getPosition().getY(), (double)var3.getPosition().getZ(), 64.0D, this.provider.getDimensionId(), new S24PacketBlockAction(var3.getPosition(), var3.getBlock(), var3.getEventID(), var3.getEventParameter()));
            }
         }

         this.field_147490_S[var1].clear();
      }

   }

   private boolean fireBlockEvent(BlockEventData var1) {
      IBlockState var2 = this.getBlockState(var1.getPosition());
      return var2.getBlock() == var1.getBlock() && var2.getBlock().onBlockEventReceived(this, var1.getPosition(), var2, var1.getEventID(), var1.getEventParameter());
   }

   public void flush() {
      this.saveHandler.flush();
   }

   protected void updateWeather() {
      boolean var1 = this.isRaining();
      super.updateWeather();
      if(this.prevRainingStrength != this.rainingStrength) {
         this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
      }

      if(this.prevThunderingStrength != this.thunderingStrength) {
         this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
      }

      if(var1 != this.isRaining()) {
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
      }

   }

   protected int getRenderDistanceChunks() {
      return this.mcServer.getConfigurationManager().getViewDistance();
   }

   public MinecraftServer getMinecraftServer() {
      return this.mcServer;
   }

   public EntityTracker getEntityTracker() {
      return this.theEntityTracker;
   }

   public PlayerManager getPlayerManager() {
      return this.thePlayerManager;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public void spawnParticle(EnumParticleTypes var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15, int... var17) {
      this.spawnParticle(var1, false, var2, var4, var6, var8, var9, var11, var13, var15, var17);
   }

   public void spawnParticle(EnumParticleTypes var1, boolean var2, double var3, double var5, double var7, int var9, double var10, double var12, double var14, double var16, int... var18) {
      S2APacketParticles var19 = new S2APacketParticles(var1, var2, (float)var3, (float)var5, (float)var7, (float)var10, (float)var12, (float)var14, (float)var16, var9, var18);

      for(EntityPlayer var21 : this.getPlayerEntities()) {
         EntityPlayerMP var22 = (EntityPlayerMP)var21;
         BlockPos var23 = var22.getPosition();
         double var24 = var23.distanceSq(var3, var5, var7);
         if(var24 <= 256.0D || var24 <= 65536.0D) {
            var22.playerNetServerHandler.sendPacket(var19);
         }
      }

   }

   public Entity getEntityFromUuid(UUID var1) {
      return (Entity)this.entitiesByUuid.get(var1);
   }

   public ListenableFuture addScheduledTask(Runnable var1) {
      return this.mcServer.addScheduledTask(var1);
   }

   public boolean isCallingFromMinecraftThread() {
      return this.mcServer.isCallingFromMinecraftThread();
   }

   private static Throwable b(Throwable var0) {
      return var0;
   }
}
