package net.minecraft.client.stream;

import net.minecraft.client.stream.Metadata;
import net.minecraft.entity.EntityLivingBase;

public class MetadataPlayerDeath extends Metadata {
   public MetadataPlayerDeath(EntityLivingBase var1, EntityLivingBase var2) {
      super("player_death");
      this.func_152808_a("player", var1.getName());
      this.func_152808_a("killer", var2.getName());
   }
}
