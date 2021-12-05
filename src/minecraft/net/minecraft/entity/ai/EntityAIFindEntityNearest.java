package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EntityAIFindEntityNearest extends EntityAIBase {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EntityLiving field_179442_b;
    private final Predicate<EntityLivingBase> field_179443_c;
    private final EntityAINearestAttackableTarget.Sorter field_179440_d;
    private EntityLivingBase field_179441_e;
    private final Class<? extends EntityLivingBase> field_179439_f;

    public EntityAIFindEntityNearest(EntityLiving entityLiving, Class<? extends EntityLivingBase> p_i45884_2_) {
        this.field_179442_b = entityLiving;
        this.field_179439_f = p_i45884_2_;

        if (entityLiving instanceof EntityCreature)
            LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");

        this.field_179443_c = entityLivingBase -> {
            double d0 = EntityAIFindEntityNearest.this.func_179438_f();

            if (entityLivingBase.isSneaking()) {
                d0 *= 0.800000011920929D;
            }

            return !entityLivingBase.isInvisible() && !((double) entityLivingBase.getDistanceToEntity(EntityAIFindEntityNearest.this.field_179442_b) > d0) && EntityAITarget.isSuitableTarget(EntityAIFindEntityNearest.this.field_179442_b, entityLivingBase, false, true);
        };
        this.field_179440_d = new EntityAINearestAttackableTarget.Sorter(entityLiving);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        final double d0 = this.func_179438_f();
        final List<EntityLivingBase> list = this.field_179442_b.worldObj.getEntitiesWithinAABB(this.field_179439_f, this.field_179442_b.getEntityBoundingBox().expand(d0, 4.0D, d0), this.field_179443_c);

        list.sort(this.field_179440_d);

        if (list.isEmpty()) {
            return false;
        } else {
            this.field_179441_e = list.get(0);
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        final EntityLivingBase entitylivingbase = this.field_179442_b.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else {
            final double d0 = this.func_179438_f();
            return !(this.field_179442_b.getDistanceSqToEntity(entitylivingbase) > d0 * d0) && (!(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP) entitylivingbase).theItemInWorldManager.isCreative());
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.field_179442_b.setAttackTarget(this.field_179441_e);
        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.field_179442_b.setAttackTarget(null);
        super.startExecuting();
    }

    protected double func_179438_f() {
        final IAttributeInstance iattributeinstance = this.field_179442_b.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }

}
