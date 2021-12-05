package net.minecraft.client.gui;

import java.util.function.Predicate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList$GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField extends Gui {
   protected final int id;
   protected final FontRenderer fontRendererInstance;
   public float xPosition;
   public float yPosition;
   protected final int width;
   protected final int height;
   protected String text = "";
   private int maxStringLength = 64;
   protected int cursorCounter;
   protected boolean enableBackgroundDrawing = true;
   protected boolean canLoseFocus = true;
   protected boolean isFocused;
   protected boolean isEnabled = true;
   protected int lineScrollOffset;
   protected int cursorPosition;
   protected int selectionEnd;
   protected int enabledColor = 14737632;
   protected int disabledColor = 7368816;
   protected boolean visible = true;
   protected GuiPageButtonList$GuiResponder field_175210_x;
   protected Predicate field_175209_y = GuiTextField::lambda$new$0;

   public GuiTextField(int var1, FontRenderer var2, int var3, int var4, int var5, int var6) {
      this.id = var1;
      this.fontRendererInstance = var2;
      this.xPosition = (float)var3;
      this.yPosition = (float)var4;
      this.width = var5;
      this.height = var6;
   }

   public void func_175207_a(GuiPageButtonList$GuiResponder var1) {
      this.field_175210_x = var1;
   }

   public void updateCursorCounter() {
      ++this.cursorCounter;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      if(this.field_175209_y.test(var1)) {
         if(var1.length() > this.maxStringLength) {
            this.text = var1.substring(0, this.maxStringLength);
         } else {
            this.text = var1;
         }

         this.setCursorPositionEnd();
      }

   }

   public String getSelectedText() {
      int var1 = Math.min(this.cursorPosition, this.selectionEnd);
      int var2 = Math.max(this.cursorPosition, this.selectionEnd);
      return this.text.substring(var1, var2);
   }

   public void func_175205_a(Predicate var1) {
      this.field_175209_y = var1;
   }

   public void writeText(String var1) {
      String var2 = "";
      String var3 = ChatAllowedCharacters.filterAllowedCharacters(var1);
      int var4 = Math.min(this.cursorPosition, this.selectionEnd);
      int var5 = Math.max(this.cursorPosition, this.selectionEnd);
      int var6 = this.maxStringLength - this.text.length() - (var4 - var5);
      if(!this.text.isEmpty()) {
         var2 = var2 + this.text.substring(0, var4);
      }

      int var7;
      if(var6 < var3.length()) {
         var2 = var2 + var3.substring(0, var6);
         var7 = var6;
      } else {
         var2 = var2 + var3;
         var7 = var3.length();
      }

      if(!this.text.isEmpty() && var5 < this.text.length()) {
         var2 = var2 + this.text.substring(var5);
      }

      if(this.field_175209_y.test(var2)) {
         this.text = var2;
         this.moveCursorBy(var4 - this.selectionEnd + var7);
         if(this.field_175210_x != null) {
            this.field_175210_x.func_175319_a(this.id, this.text);
         }
      }

   }

   public void deleteWords(int var1) {
      if(!this.text.isEmpty()) {
         if(this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(var1) - this.cursorPosition);
         }
      }

   }

   public void deleteFromCursor(int var1) {
      if(!this.text.isEmpty()) {
         if(this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean var2 = true;
            int var3 = this.cursorPosition + var1;
            int var4 = this.cursorPosition;
            String var5 = "";
            var5 = this.text.substring(0, var3);
            if(var4 < this.text.length()) {
               var5 = var5 + this.text.substring(var4);
            }

            if(this.field_175209_y.test(var5)) {
               this.text = var5;
               this.moveCursorBy(var1);
               if(this.field_175210_x != null) {
                  this.field_175210_x.func_175319_a(this.id, this.text);
               }
            }
         }
      }

   }

   public int getId() {
      return this.id;
   }

   public int getNthWordFromCursor(int var1) {
      return this.getNthWordFromPos(var1, this.getCursorPosition());
   }

   public int getNthWordFromPos(int var1, int var2) {
      return this.func_146197_a(var1, var2, true);
   }

   public int func_146197_a(int var1, int var2, boolean var3) {
      int var4 = var2;
      boolean var5 = true;
      int var6 = Math.abs(var1);

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = this.text.length();
         var4 = this.text.indexOf(32, var4);
         if(var4 == -1) {
            var4 = var8;
         } else {
            while(var4 < var8 && this.text.charAt(var4) == 32) {
               ++var4;
            }
         }
      }

      return var4;
   }

   public void moveCursorBy(int var1) {
      this.setCursorPosition(this.selectionEnd + var1);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean textboxKeyTyped(char var1, int var2) {
      if(!this.isFocused) {
         return false;
      } else if(GuiScreen.isKeyComboCtrlA(var2)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      } else if(GuiScreen.isKeyComboCtrlC(var2)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      } else if(GuiScreen.isKeyComboCtrlV(var2)) {
         if(this.isEnabled) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if(GuiScreen.isKeyComboCtrlX(var2)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if(this.isEnabled) {
            this.writeText("");
         }

         return true;
      } else {
         switch(var2) {
         case 14:
            if(GuiScreen.isCtrlKeyDown()) {
               if(this.isEnabled) {
                  this.deleteWords(-1);
               }
            } else if(this.isEnabled) {
               this.deleteFromCursor(-1);
            }

            return true;
         case 199:
            if(GuiScreen.isShiftKeyDown()) {
               this.setSelectionPos(0);
            } else {
               this.setCursorPositionZero();
            }

            return true;
         case 203:
            if(GuiScreen.isShiftKeyDown()) {
               if(GuiScreen.isCtrlKeyDown()) {
                  this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
               } else {
                  this.setSelectionPos(this.getSelectionEnd() - 1);
               }
            } else if(GuiScreen.isCtrlKeyDown()) {
               this.setCursorPosition(this.getNthWordFromCursor(-1));
            } else {
               this.moveCursorBy(-1);
            }

            return true;
         case 205:
            if(GuiScreen.isShiftKeyDown()) {
               if(GuiScreen.isCtrlKeyDown()) {
                  this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
               } else {
                  this.setSelectionPos(this.getSelectionEnd() + 1);
               }
            } else if(GuiScreen.isCtrlKeyDown()) {
               this.setCursorPosition(this.getNthWordFromCursor(1));
            } else {
               this.moveCursorBy(1);
            }

            return true;
         case 207:
            if(GuiScreen.isShiftKeyDown()) {
               this.setSelectionPos(this.text.length());
            } else {
               this.setCursorPositionEnd();
            }

            return true;
         case 211:
            if(this.isEnabled) {
               this.deleteFromCursor(1);
            } else if(GuiScreen.isCtrlKeyDown() && this.isEnabled) {
               this.deleteWords(1);
            }

            return true;
         default:
            if(ChatAllowedCharacters.isAllowedCharacter(var1)) {
               if(this.isEnabled) {
                  this.writeText(Character.toString(var1));
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void mouseClicked(int var1, int var2, int var3) {
      boolean var4 = (float)var1 >= this.xPosition && (float)var1 < this.xPosition + (float)this.width && (float)var2 >= this.yPosition && (float)var2 < this.yPosition + (float)this.height;
      if(this.canLoseFocus) {
         this.setFocused(var4);
      }

      if(this.isFocused) {
         int var5 = (int)((float)var1 - this.xPosition);
         if(this.enableBackgroundDrawing) {
            var5 -= 4;
         }

         String var6 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(var6, var5).length() + this.lineScrollOffset);
      }

   }

   public void drawTextBox() {
      if(this.getVisible()) {
         if(this.getEnableBackgroundDrawing()) {
            drawRect((double)(this.xPosition - 1.0F), (double)(this.yPosition - 1.0F), (double)(this.xPosition + (float)this.width + 1.0F), (double)(this.yPosition + (float)this.height + 1.0F), -6250336);
            drawRect((double)this.xPosition, (double)this.yPosition, (double)(this.xPosition + (float)this.width), (double)(this.yPosition + (float)this.height), -16777216);
         }

         int var1 = this.isEnabled?this.enabledColor:this.disabledColor;
         int var2 = this.cursorPosition - this.lineScrollOffset;
         int var3 = this.selectionEnd - this.lineScrollOffset;
         String var4 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         boolean var5 = var2 <= var4.length();
         if(this.isFocused && this.cursorCounter / 6 % 2 == 0) {
            boolean var13 = true;
         } else {
            boolean var10000 = false;
         }

         int var7 = (int)(this.enableBackgroundDrawing?this.xPosition + 4.0F:this.xPosition);
         int var8 = (int)(this.enableBackgroundDrawing?this.yPosition + (float)((this.height - 8) / 2):this.yPosition);
         int var9 = var7;
         if(var3 > var4.length()) {
            var3 = var4.length();
         }

         if(!var4.isEmpty()) {
            String var10 = var4.substring(0, var2);
            var9 = this.fontRendererInstance.drawStringWithShadow(var10, (float)var7, (float)var8, var1);
         }

         if(this.cursorPosition >= this.text.length() && this.text.length() < this.getMaxStringLength()) {
            boolean var15 = false;
         } else {
            boolean var14 = true;
         }

         int var11 = var7 + this.width;
         if(!var4.isEmpty() && var2 < var4.length()) {
            this.fontRendererInstance.drawStringWithShadow(var4.substring(var2), (float)var9, (float)var8, var1);
         }

         drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRendererInstance.getHeight(), -3092272);
         if(var3 != var2) {
            int var12 = var7 + this.fontRendererInstance.d(var4.substring(0, var3));
            this.drawCursorVertical(var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRendererInstance.getHeight());
         }
      }

   }

   protected void drawCursorVertical(int var1, int var2, int var3, int var4) {
      if(var1 < var3) {
         int var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      if(var2 < var4) {
         int var7 = var2;
         var2 = var4;
         var4 = var7;
      }

      if((float)var3 > this.xPosition + (float)this.width) {
         var3 = (int)(this.xPosition + (float)this.width);
      }

      if((float)var1 > this.xPosition + (float)this.width) {
         var1 = (int)(this.xPosition + (float)this.width);
      }

      Tessellator var8 = Tessellator.getInstance();
      WorldRenderer var6 = var8.getWorldRenderer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      var6.begin(7, DefaultVertexFormats.POSITION);
      var6.pos((double)var1, (double)var4, 0.0D).endVertex();
      var6.pos((double)var3, (double)var4, 0.0D).endVertex();
      var6.pos((double)var3, (double)var2, 0.0D).endVertex();
      var6.pos((double)var1, (double)var2, 0.0D).endVertex();
      var8.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   public int getMaxStringLength() {
      return this.maxStringLength;
   }

   public void setMaxStringLength(int var1) {
      this.maxStringLength = var1;
      if(this.text.length() > var1) {
         this.text = this.text.substring(0, var1);
      }

   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public void setCursorPosition(int var1) {
      this.cursorPosition = var1;
      int var2 = this.text.length();
      this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, var2);
      this.setSelectionPos(this.cursorPosition);
   }

   public boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setEnableBackgroundDrawing(boolean var1) {
      this.enableBackgroundDrawing = var1;
   }

   public void setTextColor(int var1) {
      this.enabledColor = var1;
   }

   public void setDisabledTextColour(int var1) {
      this.disabledColor = var1;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public void setFocused(boolean var1) {
      if(!this.isFocused) {
         this.cursorCounter = 0;
      }

      this.isFocused = var1;
   }

   public void setEnabled(boolean var1) {
      this.isEnabled = var1;
   }

   public int getSelectionEnd() {
      return this.selectionEnd;
   }

   public int getWidth() {
      return this.getEnableBackgroundDrawing()?this.width - 8:this.width;
   }

   public void setSelectionPos(int var1) {
      int var2 = this.text.length();
      if(var1 > var2) {
         ;
      }

      var1 = 0;
      this.selectionEnd = var1;
      if(this.fontRendererInstance != null) {
         if(this.lineScrollOffset > var2) {
            this.lineScrollOffset = var2;
         }

         int var3 = this.getWidth();
         String var4 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), var3);
         int var5 = var4.length() + this.lineScrollOffset;
         if(var1 == this.lineScrollOffset) {
            this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, var3, true).length();
         }

         if(var1 > var5) {
            this.lineScrollOffset += var1 - var5;
         } else if(var1 <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - var1;
         }

         this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, var2);
      }

   }

   public void setCanLoseFocus(boolean var1) {
      this.canLoseFocus = var1;
   }

   public boolean getVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public boolean isEnabled() {
      return this.isEnabled;
   }

   private static boolean lambda$new$0(String var0) {
      return true;
   }
}
