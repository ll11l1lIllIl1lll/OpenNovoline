package net;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import viaversion.viaversion.api.entities.EntityType;

public enum axs implements EntityType {
   ENTITY(-1),
   AREA_EFFECT_CLOUD(0, ENTITY),
   END_CRYSTAL(18, ENTITY),
   EVOKER_FANGS(23, ENTITY),
   EXPERIENCE_ORB(24, ENTITY),
   EYE_OF_ENDER(25, ENTITY),
   FALLING_BLOCK(26, ENTITY),
   FIREWORK_ROCKET(27, ENTITY),
   ITEM(37, ENTITY),
   LLAMA_SPIT(43, ENTITY),
   TNT(63, ENTITY),
   SHULKER_BULLET(70, ENTITY),
   FISHING_BOBBER(106, ENTITY),
   LIVINGENTITY(-1, ENTITY),
   ARMOR_STAND(1, LIVINGENTITY),
   PLAYER(105, LIVINGENTITY),
   ABSTRACT_INSENTIENT(-1, LIVINGENTITY),
   ENDER_DRAGON(19, ABSTRACT_INSENTIENT),
   BEE(4, ABSTRACT_INSENTIENT),
   ABSTRACT_CREATURE(-1, ABSTRACT_INSENTIENT),
   ABSTRACT_AGEABLE(-1, ABSTRACT_CREATURE),
   VILLAGER(92, ABSTRACT_AGEABLE),
   WANDERING_TRADER(94, ABSTRACT_AGEABLE),
   ABSTRACT_ANIMAL(-1, ABSTRACT_AGEABLE),
   DOLPHIN(13, ABSTRACT_INSENTIENT),
   CHICKEN(9, ABSTRACT_ANIMAL),
   COW(11, ABSTRACT_ANIMAL),
   MOOSHROOM(53, COW),
   PANDA(56, ABSTRACT_INSENTIENT),
   PIG(59, ABSTRACT_ANIMAL),
   POLAR_BEAR(62, ABSTRACT_ANIMAL),
   RABBIT(65, ABSTRACT_ANIMAL),
   SHEEP(68, ABSTRACT_ANIMAL),
   TURTLE(90, ABSTRACT_ANIMAL),
   FOX(28, ABSTRACT_ANIMAL),
   ABSTRACT_TAMEABLE_ANIMAL(-1, ABSTRACT_ANIMAL),
   CAT(7, ABSTRACT_TAMEABLE_ANIMAL),
   OCELOT(54, ABSTRACT_TAMEABLE_ANIMAL),
   WOLF(99, ABSTRACT_TAMEABLE_ANIMAL),
   ABSTRACT_PARROT(-1, ABSTRACT_TAMEABLE_ANIMAL),
   PARROT(57, ABSTRACT_PARROT),
   ABSTRACT_HORSE(-1, ABSTRACT_ANIMAL),
   CHESTED_HORSE(-1, ABSTRACT_HORSE),
   DONKEY(14, CHESTED_HORSE),
   MULE(52, CHESTED_HORSE),
   LLAMA(42, CHESTED_HORSE),
   TRADER_LLAMA(88, CHESTED_HORSE),
   HORSE(33, ABSTRACT_HORSE),
   SKELETON_HORSE(73, ABSTRACT_HORSE),
   ZOMBIE_HORSE(102, ABSTRACT_HORSE),
   ABSTRACT_GOLEM(-1, ABSTRACT_CREATURE),
   SNOW_GOLEM(76, ABSTRACT_GOLEM),
   IRON_GOLEM(36, ABSTRACT_GOLEM),
   SHULKER(69, ABSTRACT_GOLEM),
   ABSTRACT_FISHES(-1, ABSTRACT_CREATURE),
   COD(10, ABSTRACT_FISHES),
   PUFFERFISH(64, ABSTRACT_FISHES),
   SALMON(67, ABSTRACT_FISHES),
   TROPICAL_FISH(89, ABSTRACT_FISHES),
   ABSTRACT_MONSTER(-1, ABSTRACT_CREATURE),
   BLAZE(5, ABSTRACT_MONSTER),
   CREEPER(12, ABSTRACT_MONSTER),
   ENDERMITE(21, ABSTRACT_MONSTER),
   ENDERMAN(20, ABSTRACT_MONSTER),
   GIANT(30, ABSTRACT_MONSTER),
   SILVERFISH(71, ABSTRACT_MONSTER),
   VEX(91, ABSTRACT_MONSTER),
   WITCH(95, ABSTRACT_MONSTER),
   WITHER(96, ABSTRACT_MONSTER),
   RAVAGER(66, ABSTRACT_MONSTER),
   PIGLIN(60, ABSTRACT_MONSTER),
   HOGLIN(32, ABSTRACT_ANIMAL),
   STRIDER(82, ABSTRACT_ANIMAL),
   ZOGLIN(100, ABSTRACT_MONSTER),
   ABSTRACT_ILLAGER_BASE(-1, ABSTRACT_MONSTER),
   ABSTRACT_EVO_ILLU_ILLAGER(-1, ABSTRACT_ILLAGER_BASE),
   EVOKER(22, ABSTRACT_EVO_ILLU_ILLAGER),
   ILLUSIONER(35, ABSTRACT_EVO_ILLU_ILLAGER),
   VINDICATOR(93, ABSTRACT_ILLAGER_BASE),
   PILLAGER(61, ABSTRACT_ILLAGER_BASE),
   ABSTRACT_SKELETON(-1, ABSTRACT_MONSTER),
   SKELETON(72, ABSTRACT_SKELETON),
   STRAY(81, ABSTRACT_SKELETON),
   WITHER_SKELETON(97, ABSTRACT_SKELETON),
   GUARDIAN(31, ABSTRACT_MONSTER),
   ELDER_GUARDIAN(17, GUARDIAN),
   SPIDER(79, ABSTRACT_MONSTER),
   CAVE_SPIDER(8, SPIDER),
   ZOMBIE(101, ABSTRACT_MONSTER),
   DROWNED(16, ZOMBIE),
   HUSK(34, ZOMBIE),
   ZOMBIFIED_PIGLIN(104, ZOMBIE),
   ZOMBIE_VILLAGER(103, ZOMBIE),
   ABSTRACT_FLYING(-1, ABSTRACT_INSENTIENT),
   GHAST(29, ABSTRACT_FLYING),
   PHANTOM(58, ABSTRACT_FLYING),
   ABSTRACT_AMBIENT(-1, ABSTRACT_INSENTIENT),
   BAT(3, ABSTRACT_AMBIENT),
   ABSTRACT_WATERMOB(-1, ABSTRACT_INSENTIENT),
   SQUID(80, ABSTRACT_WATERMOB),
   SLIME(74, ABSTRACT_INSENTIENT),
   MAGMA_CUBE(44, SLIME),
   ABSTRACT_HANGING(-1, ENTITY),
   LEASH_KNOT(40, ABSTRACT_HANGING),
   ITEM_FRAME(38, ABSTRACT_HANGING),
   PAINTING(55, ABSTRACT_HANGING),
   ABSTRACT_LIGHTNING(-1, ENTITY),
   LIGHTNING_BOLT(41, ABSTRACT_LIGHTNING),
   ABSTRACT_ARROW(-1, ENTITY),
   ARROW(2, ABSTRACT_ARROW),
   SPECTRAL_ARROW(78, ABSTRACT_ARROW),
   TRIDENT(87, ABSTRACT_ARROW),
   ABSTRACT_FIREBALL(-1, ENTITY),
   DRAGON_FIREBALL(15, ABSTRACT_FIREBALL),
   FIREBALL(39, ABSTRACT_FIREBALL),
   SMALL_FIREBALL(75, ABSTRACT_FIREBALL),
   WITHER_SKULL(98, ABSTRACT_FIREBALL),
   PROJECTILE_ABSTRACT(-1, ENTITY),
   SNOWBALL(77, PROJECTILE_ABSTRACT),
   ENDER_PEARL(84, PROJECTILE_ABSTRACT),
   EGG(83, PROJECTILE_ABSTRACT),
   POTION(86, PROJECTILE_ABSTRACT),
   EXPERIENCE_BOTTLE(85, PROJECTILE_ABSTRACT),
   MINECART_ABSTRACT(-1, ENTITY),
   CHESTED_MINECART_ABSTRACT(-1, MINECART_ABSTRACT),
   CHEST_MINECART(46, CHESTED_MINECART_ABSTRACT),
   HOPPER_MINECART(49, CHESTED_MINECART_ABSTRACT),
   MINECART(45, MINECART_ABSTRACT),
   FURNACE_MINECART(48, MINECART_ABSTRACT),
   COMMAND_BLOCK_MINECART(47, MINECART_ABSTRACT),
   TNT_MINECART(51, MINECART_ABSTRACT),
   SPAWNER_MINECART(50, MINECART_ABSTRACT),
   BOAT(6, ENTITY);

   private static final Map d = new HashMap();
   private final int a;
   private final axs b;
   private static final axs[] c = new axs[]{ENTITY, AREA_EFFECT_CLOUD, END_CRYSTAL, EVOKER_FANGS, EXPERIENCE_ORB, EYE_OF_ENDER, FALLING_BLOCK, FIREWORK_ROCKET, ITEM, LLAMA_SPIT, TNT, SHULKER_BULLET, FISHING_BOBBER, LIVINGENTITY, ARMOR_STAND, PLAYER, ABSTRACT_INSENTIENT, ENDER_DRAGON, BEE, ABSTRACT_CREATURE, ABSTRACT_AGEABLE, VILLAGER, WANDERING_TRADER, ABSTRACT_ANIMAL, DOLPHIN, CHICKEN, COW, MOOSHROOM, PANDA, PIG, POLAR_BEAR, RABBIT, SHEEP, TURTLE, FOX, ABSTRACT_TAMEABLE_ANIMAL, CAT, OCELOT, WOLF, ABSTRACT_PARROT, PARROT, ABSTRACT_HORSE, CHESTED_HORSE, DONKEY, MULE, LLAMA, TRADER_LLAMA, HORSE, SKELETON_HORSE, ZOMBIE_HORSE, ABSTRACT_GOLEM, SNOW_GOLEM, IRON_GOLEM, SHULKER, ABSTRACT_FISHES, COD, PUFFERFISH, SALMON, TROPICAL_FISH, ABSTRACT_MONSTER, BLAZE, CREEPER, ENDERMITE, ENDERMAN, GIANT, SILVERFISH, VEX, WITCH, WITHER, RAVAGER, PIGLIN, HOGLIN, STRIDER, ZOGLIN, ABSTRACT_ILLAGER_BASE, ABSTRACT_EVO_ILLU_ILLAGER, EVOKER, ILLUSIONER, VINDICATOR, PILLAGER, ABSTRACT_SKELETON, SKELETON, STRAY, WITHER_SKELETON, GUARDIAN, ELDER_GUARDIAN, SPIDER, CAVE_SPIDER, ZOMBIE, DROWNED, HUSK, ZOMBIFIED_PIGLIN, ZOMBIE_VILLAGER, ABSTRACT_FLYING, GHAST, PHANTOM, ABSTRACT_AMBIENT, BAT, ABSTRACT_WATERMOB, SQUID, SLIME, MAGMA_CUBE, ABSTRACT_HANGING, LEASH_KNOT, ITEM_FRAME, PAINTING, ABSTRACT_LIGHTNING, LIGHTNING_BOLT, ABSTRACT_ARROW, ARROW, SPECTRAL_ARROW, TRIDENT, ABSTRACT_FIREBALL, DRAGON_FIREBALL, FIREBALL, SMALL_FIREBALL, WITHER_SKULL, PROJECTILE_ABSTRACT, SNOWBALL, ENDER_PEARL, EGG, POTION, EXPERIENCE_BOTTLE, MINECART_ABSTRACT, CHESTED_MINECART_ABSTRACT, CHEST_MINECART, HOPPER_MINECART, MINECART, FURNACE_MINECART, COMMAND_BLOCK_MINECART, TNT_MINECART, SPAWNER_MINECART, BOAT};

   private axs(int var3) {
      this.a = var3;
      this.b = null;
   }

   private axs(int var3, axs var4) {
      this.a = var3;
      this.b = var4;
   }

   public int getId() {
      return this.a;
   }

   public axs a() {
      return this.b;
   }

   public static Optional a(int var0) {
      return var0 == -1?Optional.empty():Optional.ofNullable(d.get(Integer.valueOf(var0)));
   }

   static {
      for(axs var11 : values()) {
         d.put(Integer.valueOf(var11.a), var11);
      }

   }
}
