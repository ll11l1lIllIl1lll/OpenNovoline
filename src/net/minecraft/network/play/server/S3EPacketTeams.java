package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team$EnumVisible;

public class S3EPacketTeams implements Packet {
   private String field_149320_a = "";
   private String field_149318_b = "";
   private String field_149319_c = "";
   private String field_149316_d = "";
   private String field_179816_e;
   private int field_179815_f;
   private Collection field_149317_e;
   private int field_149314_f;
   private int field_149315_g;

   public S3EPacketTeams() {
      this.field_179816_e = Team$EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
   }

   public S3EPacketTeams(ScorePlayerTeam var1, int var2) {
      this.field_179816_e = Team$EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
      this.field_149320_a = var1.getRegisteredName();
      this.field_149314_f = var2;
      if(var2 == 2) {
         this.field_149318_b = var1.getTeamName();
         this.field_149319_c = var1.getColorPrefix();
         this.field_149316_d = var1.getColorSuffix();
         this.field_149315_g = var1.func_98299_i();
         this.field_179816_e = var1.getNameTagVisibility().field_178830_e;
         this.field_179815_f = var1.getChatFormat().getColorIndex();
      }

      this.field_149317_e.addAll(var1.getMembershipCollection());
   }

   public S3EPacketTeams(ScorePlayerTeam var1, Collection var2, int var3) {
      this.field_179816_e = Team$EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
      if(var3 != 3 && var3 != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if(!var2.isEmpty()) {
         this.field_149314_f = var3;
         this.field_149320_a = var1.getRegisteredName();
         this.field_149317_e.addAll(var2);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void readPacketData(PacketBuffer var1) throws IOException {
      this.field_149320_a = var1.a(16);
      this.field_149314_f = var1.readByte();
      if(this.field_149314_f == 0 || this.field_149314_f == 2) {
         this.field_149318_b = var1.a(32);
         this.field_149319_c = var1.a(16);
         this.field_149316_d = var1.a(16);
         this.field_149315_g = var1.readByte();
         this.field_179816_e = var1.a(32);
         this.field_179815_f = var1.readByte();
      }

      if(this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
         int var2 = var1.readVarIntFromBuffer();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.field_149317_e.add(var1.a(40));
         }
      }

   }

   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.field_149320_a);
      var1.writeByte(this.field_149314_f);
      if(this.field_149314_f == 0 || this.field_149314_f == 2) {
         var1.writeString(this.field_149318_b);
         var1.writeString(this.field_149319_c);
         var1.writeString(this.field_149316_d);
         var1.writeByte(this.field_149315_g);
         var1.writeString(this.field_179816_e);
         var1.writeByte(this.field_179815_f);
      }

      if(this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
         var1.writeVarIntToBuffer(this.field_149317_e.size());

         for(String var3 : this.field_149317_e) {
            var1.writeString(var3);
         }
      }

   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleTeams(this);
   }

   public String func_149312_c() {
      return this.field_149320_a;
   }

   public String func_149306_d() {
      return this.field_149318_b;
   }

   public String func_149311_e() {
      return this.field_149319_c;
   }

   public String func_149309_f() {
      return this.field_149316_d;
   }

   public Collection func_149310_g() {
      return this.field_149317_e;
   }

   public int func_149307_h() {
      return this.field_149314_f;
   }

   public int func_149308_i() {
      return this.field_149315_g;
   }

   public int func_179813_h() {
      return this.field_179815_f;
   }

   public String func_179814_i() {
      return this.field_179816_e;
   }

   private static Exception a(Exception var0) {
      return var0;
   }
}
