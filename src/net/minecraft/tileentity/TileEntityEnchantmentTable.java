package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements ITickable, IInteractionObject {
   public int tickCount;
   public float pageFlip;
   public float pageFlipPrev;
   public float field_145932_k;
   public float field_145929_l;
   public float bookSpread;
   public float bookSpreadPrev;
   public float bookRotation;
   public float bookRotationPrev;
   public float field_145924_q;
   private static Random rand = new Random();
   private String customName;

   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      if(this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
      }

   }

   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      if(var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }

   }

   public void update() {
      this.bookSpreadPrev = this.bookSpread;
      this.bookRotationPrev = this.bookRotation;
      EntityPlayer var1 = this.worldObj.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0D);
      double var2 = var1.posX - (double)((float)this.pos.getX() + 0.5F);
      double var4 = var1.posZ - (double)((float)this.pos.getZ() + 0.5F);
      this.field_145924_q = (float)MathHelper.func_181159_b(var4, var2);
      this.bookSpread += 0.1F;
      if(this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
         float var6 = this.field_145932_k;

         while(true) {
            this.field_145932_k += (float)(rand.nextInt(4) - rand.nextInt(4));
            if(var6 != this.field_145932_k) {
               break;
            }
         }
      }

      while(this.bookRotation >= 3.1415927F) {
         this.bookRotation -= 6.2831855F;
      }

      while(this.bookRotation < -3.1415927F) {
         this.bookRotation += 6.2831855F;
      }

      while(this.field_145924_q >= 3.1415927F) {
         this.field_145924_q -= 6.2831855F;
      }

      while(this.field_145924_q < -3.1415927F) {
         this.field_145924_q += 6.2831855F;
      }

      for(var7 = this.field_145924_q - this.bookRotation; var7 >= 3.1415927F; var7 -= 6.2831855F) {
         ;
      }

      while(var7 < -3.1415927F) {
         var7 += 6.2831855F;
      }

      this.bookRotation += var7 * 0.4F;
      this.bookSpread = MathHelper.clamp_float(this.bookSpread, 0.0F, 1.0F);
      ++this.tickCount;
      this.pageFlipPrev = this.pageFlip;
      float var3 = (this.field_145932_k - this.pageFlip) * 0.4F;
      float var9 = 0.2F;
      var3 = MathHelper.clamp_float(var3, -var9, var9);
      this.field_145929_l += (var3 - this.field_145929_l) * 0.9F;
      this.pageFlip += this.field_145929_l;
   }

   public String getName() {
      return this.hasCustomName()?this.customName:"container.enchant";
   }

   public boolean hasCustomName() {
      return this.customName != null && !this.customName.isEmpty();
   }

   public void setCustomName(String var1) {
      this.customName = var1;
   }

   public IChatComponent getDisplayName() {
      return (IChatComponent)(this.hasCustomName()?new ChatComponentText(this.getName()):new ChatComponentTranslation(this.getName(), new Object[0]));
   }

   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerEnchantment(var1, this.worldObj, this.pos);
   }

   public String getGuiID() {
      return "minecraft:enchanting_table";
   }
}
