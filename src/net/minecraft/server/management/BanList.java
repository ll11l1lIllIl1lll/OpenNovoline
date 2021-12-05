package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;

public class BanList extends UserList {
   public BanList(File var1) {
      super(var1);
   }

   protected UserListEntry createEntry(JsonObject var1) {
      return new IPBanEntry(var1);
   }

   public boolean isBanned(SocketAddress var1) {
      String var2 = this.addressToString(var1);
      return this.hasEntry(var2);
   }

   public IPBanEntry getBanEntry(SocketAddress var1) {
      String var2 = this.addressToString(var1);
      return (IPBanEntry)this.getEntry(var2);
   }

   private String addressToString(SocketAddress var1) {
      String var2 = var1.toString();
      if(var2.contains("/")) {
         var2 = var2.substring(var2.indexOf(47) + 1);
      }

      if(var2.contains(":")) {
         var2 = var2.substring(0, var2.indexOf(58));
      }

      return var2;
   }
}
