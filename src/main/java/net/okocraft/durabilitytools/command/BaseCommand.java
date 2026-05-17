package net.okocraft.durabilitytools.command;

import net.okocraft.durabilitytools.DurabilityTools;
import net.okocraft.durabilitytools.configuration.languages.Languages;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand {

    protected final Commands registration;
    protected final DurabilityTools plugin;
    protected final Languages languages;

    private final String name;
    private final String permissionNode;
    private final int leastArgLength;
    private final boolean playerOnly;
    private final String usage;
    private final List<String> alias;

    protected BaseCommand(Commands registration, String name, String permissionNode, int leastArgLength,
                          boolean playerOnly, String usage, String... alias) {
        this.registration = registration;
        this.plugin = registration.plugin;
        this.languages = registration.languages;
        this.name = name;
        this.permissionNode = permissionNode;
        this.leastArgLength = leastArgLength;
        this.playerOnly = playerOnly;
        this.usage = usage;
        this.alias = Arrays.asList(alias);
    }

    public abstract boolean runCommand(CommandSender sender, String[] args);

    public List<String> runTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public boolean validateArgsLength(int argsLength) {
        return leastArgLength() <= argsLength;
    }

    public boolean hasPermission(CommandSender sender) {
        if (permissionNode == null || permissionNode.isEmpty()) {
            return true;
        }

        return sender.hasPermission(permissionNode());
    }

    public String name() {
        return this.name;
    }

    public String permissionNode() {
        return this.permissionNode;
    }

    public int leastArgLength() {
        return this.leastArgLength;
    }

    public boolean playerOnly() {
        return this.playerOnly;
    }

    public String usage() {
        return this.usage;
    }

    public List<String> alias() {
        return this.alias;
    }
}
