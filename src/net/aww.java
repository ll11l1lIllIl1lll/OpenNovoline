package net;

import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.aqA;
import net.as0;
import net.d3;
import net.gZ;
import net.t8;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public final class aww {
   private static int a;

   public static void a() {
      String[] var0 = d3.e();
      if(Keyboard.getEventKeyState()) {
         int var1 = Keyboard.getEventKey() == 0?Keyboard.getEventCharacter() + 256:Keyboard.getEventKey();
         ObjectIterator var2 = gZ.g().d().e().values().iterator();
         if(var2.hasNext()) {
            t8 var3 = (t8)var2.next();
            as0 var4 = var3.b();
            if(var4.o().a() instanceof aqA && ((ProtocolPathEntry)var4.o().a()).getOutputProtocolVersion() == var1) {
               if(Keyboard.isKeyDown(var1)) {
                  ++a;
               }

               if(a == 1) {
                  var4.e();
               }
            }
         }
      }

      a = 0;
   }

   public static boolean a(@NotNull ICommandSender var0, @NotNull ICommandSender var1) {
      d3.e();
      String var3 = "§" + b(var0);
      return var0.getDisplayName().getFormattedText().contains(var3) && var1.getDisplayName().getFormattedText().contains(var3);
   }

   public static boolean a(@NotNull ICommandSender var0) {
      String[] var1 = d3.e();
      return Minecraft.getMinecraft().thePlayer != null && a(Minecraft.getMinecraft().thePlayer, var0);
   }

   @NotNull
   public static String b(@NotNull ICommandSender var0) {
      d3.e();
      Matcher var2 = Pattern.compile("§(.).*§r").matcher(var0.getDisplayName().getFormattedText());
      return var2.find()?var2.group(1):"f";
   }

   public static Color c(@NotNull ICommandSender var0) {
      d3.e();
      String var2 = b(var0);
      byte var4 = -1;
      switch(var2.hashCode()) {
      case 48:
         if(!var2.equals("0")) {
            break;
         }

         var4 = 0;
      case 49:
         if(!var2.equals("1")) {
            break;
         }

         var4 = 1;
      case 50:
         if(!var2.equals("2")) {
            break;
         }

         var4 = 2;
      case 51:
         if(!var2.equals("3")) {
            break;
         }

         var4 = 3;
      case 52:
         if(!var2.equals("4")) {
            break;
         }

         var4 = 4;
      case 53:
         if(!var2.equals("5")) {
            break;
         }

         var4 = 5;
      case 54:
         if(!var2.equals("6")) {
            break;
         }

         var4 = 6;
      case 55:
         if(!var2.equals("7")) {
            break;
         }

         var4 = 7;
      case 56:
         if(!var2.equals("8")) {
            break;
         }

         var4 = 8;
      case 57:
         if(!var2.equals("9")) {
            break;
         }

         var4 = 9;
      case 97:
         if(!var2.equals("a")) {
            break;
         }

         var4 = 10;
      case 98:
         if(!var2.equals("b")) {
            break;
         }

         var4 = 11;
      case 99:
         if(!var2.equals("c")) {
            break;
         }

         var4 = 12;
      case 100:
         if(!var2.equals("d")) {
            break;
         }

         var4 = 13;
      case 101:
         if(!var2.equals("e")) {
            break;
         }

         var4 = 14;
      case 102:
         if(!var2.equals("f")) {
            break;
         }

         var4 = 15;
      case 103:
         if(var2.equals("g")) {
            var4 = 16;
         }
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 65:
      case 66:
      case 67:
      case 68:
      case 69:
      case 70:
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 82:
      case 83:
      case 84:
      case 85:
      case 86:
      case 87:
      case 88:
      case 89:
      case 90:
      case 91:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      }

      switch(var4) {
      case 0:
         return new Color(0, 0, 0);
      case 1:
         return new Color(0, 0, 170);
      case 2:
         return new Color(0, 170, 0);
      case 3:
         return new Color(0, 170, 170);
      case 4:
         return new Color(170, 0, 0);
      case 5:
         return new Color(170, 0, 170);
      case 6:
         return new Color(255, 170, 0);
      case 7:
         return new Color(170, 170, 170);
      case 8:
         return new Color(85, 85, 85);
      case 9:
         return new Color(85, 85, 255);
      case 10:
         return new Color(85, 255, 85);
      case 11:
         return new Color(85, 255, 255);
      case 12:
         return new Color(255, 85, 85);
      case 13:
         return new Color(255, 85, 255);
      case 14:
         return new Color(255, 255, 85);
      case 15:
         return new Color(255, 255, 255);
      case 16:
         return new Color(221, 214, 5);
      default:
         return new Color(100, 100, 100, 100);
      }
   }

   @Contract(
      value = "-> fail",
      pure = true
   )
   private aww() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static UnsupportedOperationException a(UnsupportedOperationException var0) {
      return var0;
   }
}
