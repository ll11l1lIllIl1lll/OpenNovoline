package net.minecraft.command.server;

import java.util.concurrent.Callable;
import net.minecraft.command.server.CommandBlockLogic;

class CommandBlockLogic$1 implements Callable {
   final CommandBlockLogic this$0;

   CommandBlockLogic$1(CommandBlockLogic var1) {
      this.this$0 = var1;
   }

   public String call() throws Exception {
      return this.this$0.getCommand();
   }
}
