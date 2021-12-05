package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class CombatTracker {
   private final List combatEntries = Lists.newArrayList();
   private final EntityLivingBase fighter;
   private int field_94555_c;
   private int field_152775_d;
   private int field_152776_e;
   private boolean field_94552_d;
   private boolean field_94553_e;
   private String field_94551_f;

   public CombatTracker(EntityLivingBase var1) {
      this.fighter = var1;
   }

   public void func_94545_a() {
      this.func_94542_g();
      if(this.fighter.isOnLadder()) {
         Block var1 = this.fighter.worldObj.getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ)).getBlock();
         if(var1 == Blocks.ladder) {
            this.field_94551_f = "ladder";
         } else if(var1 == Blocks.vine) {
            this.field_94551_f = "vines";
         }
      } else if(this.fighter.isInWater()) {
         this.field_94551_f = "water";
      }

   }

   public void trackDamage(DamageSource var1, float var2, float var3) {
      this.reset();
      this.func_94545_a();
      CombatEntry var4 = new CombatEntry(var1, this.fighter.ticksExisted, var2, var3, this.field_94551_f, this.fighter.fallDistance);
      this.combatEntries.add(var4);
      this.field_94555_c = this.fighter.ticksExisted;
      this.field_94553_e = true;
      if(var4.isLivingDamageSrc() && !this.field_94552_d && this.fighter.isEntityAlive()) {
         this.field_94552_d = true;
         this.field_152775_d = this.fighter.ticksExisted;
         this.field_152776_e = this.field_152775_d;
         this.fighter.sendEnterCombat();
      }

   }

   public IChatComponent getDeathMessage() {
      if(this.combatEntries.isEmpty()) {
         return new ChatComponentTranslation("death.attack.generic", new Object[]{this.fighter.getDisplayName()});
      } else {
         CombatEntry var1 = this.func_94544_f();
         CombatEntry var2 = (CombatEntry)this.combatEntries.get(this.combatEntries.size() - 1);
         IChatComponent var3 = var2.getDamageSrcDisplayName();
         Entity var4 = var2.getDamageSrc().getEntity();
         Object var5;
         if(var2.getDamageSrc() == DamageSource.fall) {
            IChatComponent var6 = var1.getDamageSrcDisplayName();
            if(var1.getDamageSrc() != DamageSource.fall && var1.getDamageSrc() != DamageSource.outOfWorld) {
               if(!var6.equals(var3)) {
                  Entity var7 = var1.getDamageSrc().getEntity();
                  ItemStack var8 = var7 instanceof EntityLivingBase?((EntityLivingBase)var7).getHeldItem():null;
                  if(var8.hasDisplayName()) {
                     var5 = new ChatComponentTranslation("death.fell.assist.item", new Object[]{this.fighter.getDisplayName(), var6, var8.getChatComponent()});
                  } else {
                     var5 = new ChatComponentTranslation("death.fell.assist", new Object[]{this.fighter.getDisplayName(), var6});
                  }
               } else {
                  ItemStack var9 = var4 instanceof EntityLivingBase?((EntityLivingBase)var4).getHeldItem():null;
                  if(var9.hasDisplayName()) {
                     var5 = new ChatComponentTranslation("death.fell.finish.item", new Object[]{this.fighter.getDisplayName(), var3, var9.getChatComponent()});
                  } else {
                     var5 = new ChatComponentTranslation("death.fell.finish", new Object[]{this.fighter.getDisplayName(), var3});
                  }
               }
            } else {
               var5 = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(var1), new Object[]{this.fighter.getDisplayName()});
            }
         } else {
            var5 = var2.getDamageSrc().getDeathMessage(this.fighter);
         }

         return (IChatComponent)var5;
      }
   }

   public EntityLivingBase func_94550_c() {
      EntityLivingBase var1 = null;
      EntityPlayer var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(CombatEntry var6 : this.combatEntries) {
         if(var6.getDamageSrc().getEntity() instanceof EntityPlayer && var6.func_94563_c() > var4) {
            var4 = var6.func_94563_c();
            var2 = (EntityPlayer)var6.getDamageSrc().getEntity();
         }

         if(var6.getDamageSrc().getEntity() instanceof EntityLivingBase && var6.func_94563_c() > var3) {
            var3 = var6.func_94563_c();
            var1 = (EntityLivingBase)var6.getDamageSrc().getEntity();
         }
      }

      if(var4 >= var3 / 3.0F) {
         return var2;
      } else {
         return var1;
      }
   }

   private CombatEntry func_94544_f() {
      CombatEntry var1 = null;
      CombatEntry var2 = null;
      byte var3 = 0;
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.combatEntries.size(); ++var5) {
         CombatEntry var6 = (CombatEntry)this.combatEntries.get(var5);
         CombatEntry var7 = (CombatEntry)this.combatEntries.get(var5 - 1);
         if((var6.getDamageSrc() == DamageSource.fall || var6.getDamageSrc() == DamageSource.outOfWorld) && var6.getDamageAmount() > 0.0F && var6.getDamageAmount() > var4) {
            var1 = var7;
            var4 = var6.getDamageAmount();
         }

         if(var6.func_94562_g() != null && var6.func_94563_c() > (float)var3) {
            var2 = var6;
         }
      }

      if(var4 > 5.0F) {
         return var1;
      } else if(var3 > 5) {
         return var2;
      } else {
         return null;
      }
   }

   private String func_94548_b(CombatEntry var1) {
      return var1.func_94562_g() == null?"generic":var1.func_94562_g();
   }

   public int func_180134_f() {
      return this.field_94552_d?this.fighter.ticksExisted - this.field_152775_d:this.field_152776_e - this.field_152775_d;
   }

   private void func_94542_g() {
      this.field_94551_f = null;
   }

   public void reset() {
      int var1 = this.field_94552_d?300:100;
      if(this.field_94553_e && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.field_94555_c > var1)) {
         boolean var2 = this.field_94552_d;
         this.field_94553_e = false;
         this.field_94552_d = false;
         this.field_152776_e = this.fighter.ticksExisted;
         this.fighter.sendEndCombat();
         this.combatEntries.clear();
      }

   }

   public EntityLivingBase getFighter() {
      return this.fighter;
   }
}
