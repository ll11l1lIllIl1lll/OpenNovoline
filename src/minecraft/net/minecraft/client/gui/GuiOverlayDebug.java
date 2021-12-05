package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.optifine.Reflector;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import static net.minecraft.util.MathHelper.clamp_int;
import static net.minecraft.util.MathHelper.wrapAngleTo180_float;

public class GuiOverlayDebug extends Gui {
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private static final String __OBFID = "CL_00001956";

    public GuiOverlayDebug(Minecraft mc) {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();
        this.mc.mcProfiler.endSection();
    }

    private boolean isReducedDebug() {
        return this.mc.player.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft() {
        List list = this.call();

        for (int i = 0; i < list.size(); ++i) {
            String s = (String) list.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int j = this.fontRenderer.getHeight();
                int k = this.fontRenderer.getStringWidth(s);
                boolean flag = true;
                int l = 2 + j * i;
                drawRect(1, l - 1, 2 + k + 1, l + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2, l, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution p_175239_1_) {
        List list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i) {
            String s = (String) list.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int j = this.fontRenderer.getHeight();
                int k = this.fontRenderer.getStringWidth(s);
                int l = p_175239_1_.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, l, i1, 14737632);
            }
        }
    }

    protected List call() {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.isReducedDebug()) {
            return Lists.newArrayList("Minecraft 1.8.8 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
        } else {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (GuiOverlayDebug.GuiOverlayDebug$1.field_178907_a[enumfacing.ordinal()]) {
                case 1:
                    s = "Towards negative Z";
                    break;

                case 2:
                    s = "Towards positive Z";
                    break;

                case 3:
                    s = "Towards negative X";
                    break;

                case 4:
                    s = "Towards positive X";
            }

            ArrayList arraylist = Lists.newArrayList("Minecraft 1.8.8 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics()
                            + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", String.format("XYZ: %.3f / %.2f / %.3f",
                            this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ),
                    String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()), String.format("Chunk: %d %d %d in %d %d %d",
                            blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4),
                    String.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s, wrapAngleTo180_float(entity.rotationYaw), wrapAngleTo180_float(entity.rotationPitch)));

            if (this.mc.world != null && this.mc.world.isBlockLoaded(blockpos)) {
                Chunk chunk = this.mc.world.getChunkFromBlockCoords(blockpos);
                arraylist.add("Biome: " + chunk.getBiome(blockpos, this.mc.world.getWorldChunkManager()).biomeName);
                arraylist.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
                DifficultyInstance difficultyinstance = this.mc.world.getDifficultyForLocation(blockpos);

                if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
                    EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.player.getUniqueID());

                    if (entityplayermp != null) {
                        difficultyinstance = entityplayermp.worldObj.getDifficultyForLocation(new BlockPos(entityplayermp));
                    }
                }

                arraylist.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), this.mc.world.getWorldTime() / 24000L));
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                arraylist.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
                BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
                arraylist.add(String.format("Looking at: %d %d %d", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
            }

            return arraylist;
        }
    }

    protected List getDebugInfoRight() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        ArrayList arraylist = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)),
                String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", String.format("CPU: %s", OpenGlHelper.func_183029_j()), "", String.format("Display: %dx%d (%s)", Display.getWidth(),
                        Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));

        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            arraylist.add("");
            arraylist.addAll((Collection) Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, new Object[]{Boolean.FALSE}));
        }

        if (!this.isReducedDebug()) {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);

                if (this.mc.world.getWorldType() != WorldType.DEBUG_WORLD) {
                    iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.world, blockpos);
                }

                arraylist.add("");
                arraylist.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));
                Entry entry;
                String s;

                for (Iterator iterator = iblockstate.getProperties().entrySet().iterator(); iterator.hasNext(); arraylist.add(((IProperty) entry.getKey()).getName() + ": " + s)) {
                    entry = (Entry) iterator.next();
                    s = entry.getValue().toString();

                    if (entry.getValue() == Boolean.TRUE) {
                        s = EnumChatFormatting.GREEN + s;
                    } else if (entry.getValue() == Boolean.FALSE) {
                        s = EnumChatFormatting.RED + s;
                    }
                }
            }

        }
        return arraylist;
    }

    private void func_181554_e() {
        GlStateManager.disableDepth();
        FrameTimer frametimer = this.mc.func_181539_aj();
        int i = frametimer.func_181749_a();
        int j = frametimer.func_181750_b();
        long[] along = frametimer.func_181746_c();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int k = i;
        int l = 0;
        drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

        while (k != j) {
            int i1 = frametimer.func_181748_a(along[k], 30);
            int j1 = this.func_181552_c(clamp_int(i1, 0, 60), 0, 30, 60);
            this.drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
            ++l;
            k = frametimer.func_181751_b(k + 1);
        }

        drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
        drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
        this.drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
        this.drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120) {
            this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepth();
    }

    private int func_181552_c(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
        return p_181552_1_ < p_181552_3_ ? this.func_181553_a(-16711936, -256, (float) p_181552_1_ / (float) p_181552_3_) : this.func_181553_a(-256, -65536, (float) (p_181552_1_ - p_181552_3_) / (float) (p_181552_4_ - p_181552_3_));
    }

    private int func_181553_a(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = clamp_int((int) ((float) i + (float) (i1 - i) * p_181553_3_), 0, 255);
        int j2 = clamp_int((int) ((float) j + (float) (j1 - j) * p_181553_3_), 0, 255);
        int k2 = clamp_int((int) ((float) k + (float) (k1 - k) * p_181553_3_), 0, 255);
        int l2 = clamp_int((int) ((float) l + (float) (l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }

    static final class GuiOverlayDebug$1 {
        static final int[] field_178907_a = new int[EnumFacing.values().length];
        private static final String __OBFID = "CL_00001955";

        static {
            try {
                field_178907_a[EnumFacing.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError var4) {
            }

            try {
                field_178907_a[EnumFacing.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError var3) {
            }

            try {
                field_178907_a[EnumFacing.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError var2) {
            }

            try {
                field_178907_a[EnumFacing.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError var1) {
            }
        }
    }
}
