package net.minecraft.command;

import net.F1;
import net.F8;
import net.FI;
import net.FO;
import net.Fm;
import net.Fs;
import net.GZ;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandClearInventory;
import net.minecraft.command.CommandCompare;
import net.minecraft.command.CommandDebug;
import net.minecraft.command.CommandDefaultGameMode;
import net.minecraft.command.CommandDifficulty;
import net.minecraft.command.CommandEffect;
import net.minecraft.command.CommandEntityData;
import net.minecraft.command.CommandExecuteAt;
import net.minecraft.command.CommandFill;
import net.minecraft.command.CommandGameMode;
import net.minecraft.command.CommandGameRule;
import net.minecraft.command.CommandGive;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.CommandKill;
import net.minecraft.command.CommandPlaySound;
import net.minecraft.command.CommandReplaceItem;
import net.minecraft.command.CommandSetPlayerTimeout;
import net.minecraft.command.CommandSetSpawnpoint;
import net.minecraft.command.CommandShowSeed;
import net.minecraft.command.CommandSpreadPlayers;
import net.minecraft.command.CommandStats;
import net.minecraft.command.CommandTime;
import net.minecraft.command.CommandTitle;
import net.minecraft.command.CommandToggleDownfall;
import net.minecraft.command.CommandTrigger;
import net.minecraft.command.CommandWeather;
import net.minecraft.command.CommandWorldBorder;
import net.minecraft.command.CommandXP;
import net.minecraft.command.IAdminCommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandOp;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandPublishLocalServer;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

public class ServerCommandManager extends CommandHandler implements IAdminCommand {
   public ServerCommandManager(@NotNull String var1) {
      super(var1);
      this.registerCommand(new CommandTime());
      this.registerCommand(new CommandGameMode());
      this.registerCommand(new CommandDifficulty());
      this.registerCommand(new CommandDefaultGameMode());
      this.registerCommand(new CommandKill());
      this.registerCommand(new CommandToggleDownfall());
      this.registerCommand(new CommandWeather());
      this.registerCommand(new CommandXP());
      this.registerCommand(new CommandTeleport());
      this.registerCommand(new CommandGive());
      this.registerCommand(new CommandReplaceItem());
      this.registerCommand(new CommandStats());
      this.registerCommand(new CommandEffect());
      this.registerCommand(new Fs());
      this.registerCommand(new F1());
      this.registerCommand(new CommandEmote());
      this.registerCommand(new CommandShowSeed());
      this.registerCommand(new CommandHelp());
      this.registerCommand(new CommandDebug());
      this.registerCommand(new CommandMessage());
      this.registerCommand(new CommandBroadcast());
      this.registerCommand(new CommandSetSpawnpoint());
      this.registerCommand(new CommandSetDefaultSpawnpoint());
      this.registerCommand(new CommandGameRule());
      this.registerCommand(new CommandClearInventory());
      this.registerCommand(new CommandTestFor());
      this.registerCommand(new CommandSpreadPlayers());
      this.registerCommand(new CommandPlaySound());
      this.registerCommand(new CommandScoreboard());
      this.registerCommand(new CommandExecuteAt());
      this.registerCommand(new CommandTrigger());
      this.registerCommand(new FO());
      this.registerCommand(new CommandSummon());
      this.registerCommand(new CommandSetBlock());
      this.registerCommand(new CommandFill());
      this.registerCommand(new Fm());
      this.registerCommand(new CommandCompare());
      this.registerCommand(new F8());
      this.registerCommand(new GZ());
      this.registerCommand(new CommandMessageRaw());
      this.registerCommand(new CommandWorldBorder());
      this.registerCommand(new CommandTitle());
      this.registerCommand(new CommandEntityData());
      if(MinecraftServer.getServer().isDedicatedServer()) {
         this.registerCommand(new CommandOp());
         this.registerCommand(new CommandDeOp());
         this.registerCommand(new CommandStop());
         this.registerCommand(new CommandSaveAll());
         this.registerCommand(new CommandSaveOff());
         this.registerCommand(new CommandSaveOn());
         this.registerCommand(new CommandBanIp());
         this.registerCommand(new CommandPardonIp());
         this.registerCommand(new CommandBanPlayer());
         this.registerCommand(new CommandListBans());
         this.registerCommand(new CommandPardonPlayer());
         this.registerCommand(new FI());
         this.registerCommand(new CommandListPlayers());
         this.registerCommand(new CommandWhitelist());
         this.registerCommand(new CommandSetPlayerTimeout());
      } else {
         this.registerCommand(new CommandPublishLocalServer());
      }

      CommandBase.setAdminCommander(this);
   }

   public void notifyOperators(ICommandSender var1, ICommand var2, int var3, String var4, Object... var5) {
      boolean var6 = true;
      MinecraftServer var7 = MinecraftServer.getServer();
      if(!var1.sendCommandFeedback()) {
         var6 = false;
      }

      ChatComponentTranslation var8 = new ChatComponentTranslation("chat.type.admin", new Object[]{var1.getName(), new ChatComponentTranslation(var4, var5)});
      ChatStyle var9 = var8.getChatStyle();
      var9.setColor(EnumChatFormatting.GRAY);
      var9.setItalic(Boolean.TRUE);

      for(EntityPlayer var11 : var7.getConfigurationManager().func_181057_v()) {
         if(var11 != var1 && var7.getConfigurationManager().canSendCommands(var11.getGameProfile()) && var2.canCommandSenderUseCommand(var1)) {
            boolean var12 = var1 instanceof MinecraftServer && MinecraftServer.getServer().func_183002_r();
            boolean var13 = var1 instanceof RConConsoleSource && MinecraftServer.getServer().func_181034_q();
            if(!(var1 instanceof RConConsoleSource) && !(var1 instanceof MinecraftServer)) {
               var11.addChatMessage(var8);
            }
         }
      }

      if(var1 != var7 && var7.worldServers[0].getGameRules().getBoolean("logAdminCommands")) {
         var7.addChatMessage(var8);
      }

      boolean var15 = var7.worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
      if(var1 instanceof CommandBlockLogic) {
         var15 = ((CommandBlockLogic)var1).shouldTrackOutput();
      }

      if((var3 & 1) != 1) {
         ;
      }

      if(var1 instanceof MinecraftServer) {
         var1.addChatMessage(new ChatComponentTranslation(var4, var5));
      }

   }
}
