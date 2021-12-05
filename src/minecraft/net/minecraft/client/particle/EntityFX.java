package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;
    protected float particleTextureJitterX;
    protected float particleTextureJitterY;
    protected int particleAge;
    protected int particleMaxAge;
    protected float particleScale;
    protected float particleGravity;

    /**
     * The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleRed;

    /**
     * The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleGreen;

    /**
     * The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleBlue;

    /**
     * Particle alpha
     */
    protected float particleAlpha;

    /**
     * The icon field from which the given particle pulls its texture.
     */
    protected TextureAtlasSprite particleIcon;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    protected EntityFX(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn);
        this.particleAlpha = 1.0F;
        setSize(0.2F, 0.2F);
        setPosition(posXIn, posYIn, posZIn);
        this.lastTickPosX = this.prevPosX = posXIn;
        this.lastTickPosY = this.prevPosY = posYIn;
        this.lastTickPosZ = this.prevPosZ = posZIn;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = rand.nextFloat() * 3.0F;
        this.particleScale = (rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        float f = (float) (Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        this.motionX = motionX / (double) f1 * (double) f * 0.4000000059604645D;
        this.motionY = motionY / (double) f1 * (double) f * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = motionZ / (double) f1 * (double) f * 0.4000000059604645D;
    }

    public EntityFX multiplyVelocity(float multiplier) {
        this.motionX *= multiplier;
        this.motionY = (motionY - 0.10000000149011612D) * (double) multiplier + 0.10000000149011612D;
        this.motionZ *= multiplier;
        return this;
    }

    public EntityFX multipleParticleScaleBy(float p_70541_1_) {
        setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
        this.particleScale *= p_70541_1_;
        return this;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        this.particleRed = particleRedIn;
        this.particleGreen = particleGreenIn;
        this.particleBlue = particleBlueIn;
    }

    /**
     * Sets the particle alpha (float)
     */
    public void setAlphaF(float alpha) {
        if (particleAlpha == 1.0F && alpha < 1.0F) {
            Minecraft.getInstance().effectRenderer.moveToAlphaLayer(this);
        } else if (particleAlpha < 1.0F && alpha == 1.0F) {
            Minecraft.getInstance().effectRenderer.moveToNoAlphaLayer(this);
        }

        this.particleAlpha = alpha;
    }

    public float getRedColorF() {
        return particleRed;
    }

    public float getGreenColorF() {
        return particleGreen;
    }

    public float getBlueColorF() {
        return particleBlue;
    }

    public float getAlpha() {
        return particleAlpha;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        if (this.particleAge++ >= particleMaxAge) {
            setDead();
        }

        this.motionY -= 0.04D * (double) particleGravity;
        moveEntity(motionX, motionY, motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = (float) particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float) particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * particleScale;

        if (particleIcon != null) {
            f = particleIcon.getMinU();
            f1 = particleIcon.getMaxU();
            f2 = particleIcon.getMinV();
            f3 = particleIcon.getMaxV();
        }

        float f5 = (float) (prevPosX + (posX - prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (prevPosY + (posY - prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (prevPosZ + (posZ - prevPosZ) * (double) partialTicks - interpPosZ);
        int i = getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRendererIn.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4,
                f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(
                f1,
                f3).color(
                particleRed,
                particleGreen,
                particleBlue,
                particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4,
                f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(
                f1,
                f2).color(
                particleRed,
                particleGreen,
                particleBlue,
                particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4,
                f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(
                f,
                f2).color(
                particleRed,
                particleGreen,
                particleBlue,
                particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4,
                f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(
                f,
                f3).color(
                particleRed,
                particleGreen,
                particleBlue,
                particleAlpha).lightmap(j, k).endVertex();
    }

    public int getFXLayer() {
        return 0;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
    }

    /**
     * Sets the particle's icon.
     */
    public void setParticleIcon(TextureAtlasSprite icon) {
        int i = getFXLayer();

        if (i == 1) {
            this.particleIcon = icon;
        } else {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
    }

    /**
     * Public method to set private field particleTextureIndex.
     */
    public void setParticleTextureIndex(int particleTextureIndex) {
        if (getFXLayer() != 0) {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        } else {
            this.particleTextureIndexX = particleTextureIndex % 16;
            this.particleTextureIndexY = particleTextureIndex / 16;
        }
    }

    public void nextTextureIndexX() {
        ++this.particleTextureIndexX;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    public String toString() {
        return getClass().getCanonicalName() + ", Pos (" + posX + "," + posY + "," + posZ + "), RGBA (" + particleRed + "," + particleGreen + "," + particleBlue + "," + particleAlpha + "), Age " + particleAge;
    }
}
