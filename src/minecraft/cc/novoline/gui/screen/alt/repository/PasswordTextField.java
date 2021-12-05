package cc.novoline.gui.screen.alt.repository;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;

import static cc.novoline.utils.fonts.impl.Fonts.SFBOLD.SFBOLD_16.SFBOLD_16;

/**
 * @author xDelsy
 */
public class PasswordTextField extends GuiTextField {

    private final String shit;

    public PasswordTextField(int id, FontRenderer fontRenderer, int x, int y, int width, int height, String shit) {
        super(id, fontRenderer, x, y, width, height);
        this.shit = shit;
    }

    public void drawTextBox() {
        if (this.visible) {
            SFBOLD_16.drawString(this.shit, this.xPosition - SFBOLD_16.stringWidth(this.shit) - 5,
                    this.yPosition + 5, new Color(198, 198, 198).getRGB());
            drawRect(this.xPosition, this.yPosition + 15, this.xPosition + this.width, this.yPosition + 16,
                    new Color(198, 198, 198).getRGB());
            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRendererInstance
                    .trimStringToWidth(this.text.substring(this.lineScrollOffset).replaceAll("(?s).", "*"),
                            this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? (int) this.xPosition + 4 : (int) this.xPosition;
            int i1 = this.enableBackgroundDrawing ? (int) this.yPosition + (this.height - 8) / 2 : (int) this.yPosition;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRendererInstance.drawStringWithShadow(s1, (float) l, (float) i1, i);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.fontRendererInstance.drawStringWithShadow(s.substring(j), (float) j1, (float) i1, i);
            }

            if (flag1) {
                if (flag2) {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.getHeight(), -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", (float) k1, (float) i1, i);
                }
            }

            if (k != j) {
                int l1 = l + this.fontRendererInstance.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.getHeight());
            }
        }
    }

}
