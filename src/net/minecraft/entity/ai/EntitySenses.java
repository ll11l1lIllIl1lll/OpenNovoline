package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
   EntityLiving entityObj;
   List seenEntities = Lists.newArrayList();
   List unseenEntities = Lists.newArrayList();

   public EntitySenses(EntityLiving var1) {
      this.entityObj = var1;
   }

   public void clearSensingCache() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public boolean canSee(Entity var1) {
      if(this.seenEntities.contains(var1)) {
         return true;
      } else if(this.unseenEntities.contains(var1)) {
         return false;
      } else {
         this.entityObj.worldObj.theProfiler.startSection("canSee");
         boolean var2 = this.entityObj.canEntityBeSeen(var1);
         this.entityObj.worldObj.theProfiler.endSection();
         this.seenEntities.add(var1);
         return var2;
      }
   }
}
