package net.minecraft.client.particle;

import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;

public class EntityCrit2FX$MagicFactory implements IParticleFactory {
   public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      EntityCrit2FX var16 = new EntityCrit2FX(var2, var3, var5, var7, var9, var11, var13);
      var16.setRBGColorF(var16.getRedColorF() * 0.3F, var16.getGreenColorF() * 0.8F, var16.getBlueColorF());
      var16.nextTextureIndexX();
      return var16;
   }
}
