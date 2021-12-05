package viaversion.viaversion.commands.defaultsubs;

import io.netty.util.ResourceLeakDetector;
import viaversion.viaversion.api.command.ViaCommandSender;
import viaversion.viaversion.api.command.ViaSubCommand;

public class DisplayLeaksSubCmd extends ViaSubCommand {
    @Override
    public String name() {
        return "displayleaks";
    }

    @Override
    public String description() {
        return "Try to hunt memory leaks!";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        if (ResourceLeakDetector.getLevel() != ResourceLeakDetector.Level.ADVANCED)
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        else
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        sendMessage(sender, "&6Leak detector is now %s", (ResourceLeakDetector.getLevel() == ResourceLeakDetector.Level.ADVANCED ? "&aenabled" : "&cdisabled"));
        return true;
    }
}
