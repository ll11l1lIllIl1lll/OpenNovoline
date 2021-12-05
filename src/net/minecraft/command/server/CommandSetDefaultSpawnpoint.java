package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetDefaultSpawnpoint extends CommandBase {
   public String getCommandName() {
      return "setworldspawn";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.setworldspawn.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      BlockPos var3;
      if(var2.length == 0) {
         var3 = getCommandSenderAsPlayer(var1).getPosition();
      } else {
         if(var2.length != 3 || var1.getEntityWorld() == null) {
            throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
         }

         var3 = parseBlockPos(var1, var2, 0, true);
      }

      var1.getEntityWorld().setSpawnPoint(var3);
      MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(var3));
      notifyOperators(var1, this, "commands.setworldspawn.success", new Object[]{Integer.valueOf(var3.getX()), Integer.valueOf(var3.getY()), Integer.valueOf(var3.getZ())});
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3?b(var2, 0, var3):null;
   }

   private static CommandException a(CommandException var0) {
      return var0;
   }
}
