package viaversion.viaversion.api.command;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ViaVersionCommand {
    /**
     * Register your own subcommand inside ViaVersion
     *
     * @param command Your own SubCommand instance to handle it.
     * @throws Exception throws an exception when the subcommand already exists or if it's not valid, example: spacee
     */
    void registerSubCommand(ViaSubCommand command) throws Exception;

    /**
     * Check if a subcommand is registered.
     *
     * @param name Subcommand name
     * @return true if it exists
     */
    boolean hasSubCommand(String name);

    /**
     * Get subcommand instance by name
     *
     * @param name subcommand name
     * @return ViaSubCommand instance
     */
    @Nullable
    ViaSubCommand getSubCommand(String name);

    /**
     * Executed when the Command sender executes the commands
     *
     * @param sender Sender object
     * @param args   arguments provided
     * @return was successful
     */
    boolean onCommand(ViaCommandSender sender, String[] args);

    /**
     * Executed when the Command sender tab-completes
     *
     * @param sender Sender object
     * @param args   arguments provided
     * @return was successful
     */
    List<String> onTabComplete(ViaCommandSender sender, String[] args);
}
