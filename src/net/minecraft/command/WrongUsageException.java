package net.minecraft.command;

import net.minecraft.command.SyntaxErrorException;

public class WrongUsageException extends SyntaxErrorException {
   public WrongUsageException(String var1, Object... var2) {
      super(var1, var2);
   }
}
