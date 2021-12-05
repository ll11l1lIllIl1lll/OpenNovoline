package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.ayD;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackRenderer {
   public static TileEntityItemStackRenderer instance = new TileEntityItemStackRenderer();
   private TileEntityChest field_147717_b = new TileEntityChest(0);
   private TileEntityChest field_147718_c = new TileEntityChest(1);
   private TileEntityEnderChest enderChest = new TileEntityEnderChest();
   private TileEntityBanner banner = new TileEntityBanner();
   private TileEntitySkull skull = new TileEntitySkull();

   public void renderByItem(ItemStack var1) {
      if(var1.getItem() == Items.banner) {
         this.banner.setItemValues(var1);
         TileEntityRendererDispatcher.instance.renderTileEntityAt(this.banner, 0.0D, 0.0D, 0.0D, 0.0F);
      } else if(var1.getItem() == Items.skull) {
         GameProfile var2 = null;
         if(var1.hasTagCompound()) {
            NBTTagCompound var3 = var1.getTagCompound();
            if(var3.hasKey("SkullOwner", 10)) {
               var2 = NBTUtil.readGameProfileFromNBT(var3.getCompoundTag("SkullOwner"));
            } else if(var3.hasKey("SkullOwner", 8) && !var3.getString("SkullOwner").isEmpty()) {
               var2 = new GameProfile((UUID)null, var3.getString("SkullOwner"));
               var2 = TileEntitySkull.updateGameprofile(var2);
               var3.removeTag("SkullOwner");
               var3.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), var2));
            }
         }

         if(TileEntitySkullRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.disableCull();
            ayD.a(TileEntitySkullRenderer.instance, 0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, var1.getMetadata(), var2, -1);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else {
         Block var5 = Block.getBlockFromItem(var1.getItem());
         if(var5 == Blocks.ender_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.enderChest, 0.0D, 0.0D, 0.0D, 0.0F);
         } else if(var5 == Blocks.trapped_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147718_c, 0.0D, 0.0D, 0.0D, 0.0F);
         } else {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147717_b, 0.0D, 0.0D, 0.0D, 0.0F);
         }
      }

   }
}
