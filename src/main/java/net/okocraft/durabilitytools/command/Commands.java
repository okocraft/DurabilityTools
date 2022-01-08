package net.okocraft.durabilitytools.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import java.util.Optional;
import java.util.logging.Level;
import net.okocraft.durabilitytools.configuration.languages.Languages;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.okocraft.durabilitytools.DurabilityTools;

public class Commands implements CommandExecutor, TabCompleter {

    protected final DurabilityTools plugin;
    protected final Languages languages;
    protected final Map<String, BaseCommand> registeredSubCommands = new LinkedHashMap<>();

    protected void register(BaseCommand subCommand) {
        String commandName = subCommand.name().toLowerCase(Locale.ROOT);
        if (registeredSubCommands.containsKey(commandName)) {
            plugin.getLogger().warning("The command " + commandName + " is already registered.");
            return;
        }

        registeredSubCommands.put(commandName, subCommand);
    }

    public List<BaseCommand> getRegisteredCommands() {
        return new ArrayList<>(registeredSubCommands.values());
    }

    @Nullable
    public BaseCommand getSubCommand(String name) {
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.name().equalsIgnoreCase(name)) {
                return subCommand;
            }
            if (subCommand.alias().contains(name.toLowerCase(Locale.ROOT))) {
                return subCommand;
            }
        }

        return null;
    }

    public Commands(DurabilityTools plugin) {
        this.plugin = plugin;
        this.languages = plugin.languagesConfig();
        PluginCommand pluginCommand = Objects.requireNonNull(plugin.getCommand("durabilitytools"), "The command 'durabilitytools' is not written in plugin.yml");
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);

        register(new HelpCommand(this));
        register(new ReloadCommand(this));
        try {
            register(new RepairCommand(this));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Cannot load vault. repair command has been disabled.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        BaseCommand subCommand;
        Language language = languages.language(sender);
        var command = language.command();
        
        if (args.length == 0) {
            command.noArgMessage().sendTo(sender, plugin.getDescription().getVersion());
            return true;
        }
        
        if ((subCommand = getSubCommand(args[0])) == null) {
            command.noSuchCommand().sendTo(sender);
            return true;
        }

        if (subCommand.playerOnly() && !(sender instanceof Player)) {
            command.playerOnly().sendTo(sender);
            return false;
        }

        if (!subCommand.hasPermission(sender)) {
            command.noPermission().sendTo(sender, subCommand.permissionNode());
            return false;
        }

        if (!subCommand.validateArgsLength(args.length)) {
            command.notEnoughArgument().sendTo(sender);
            command.usage().sendTo(sender, subCommand.usage());
            return false;
        }

        return subCommand.runCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> permittedCommands = getPermittedCommandNames(sender);
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], permittedCommands, new ArrayList<>());
        }

        if (!permittedCommands.contains(args[0].toLowerCase(Locale.ROOT))) {
            return List.of();
        }

        return Optional.ofNullable(getSubCommand(args[0]))
                .map(c -> c.runTabComplete(sender, args))
                .orElseGet(ArrayList::new);
    }

    public List<BaseCommand> getPermittedCommands(CommandSender sender) {
        List<BaseCommand> result = new ArrayList<>();
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.hasPermission(sender)) {
                result.add(subCommand);
            }
        }
        return result;
    }

    private List<String> getPermittedCommandNames(CommandSender sender) {
        List<String> result = new ArrayList<>();
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.hasPermission(sender)) {
                result.add(subCommand.name().toLowerCase(Locale.ROOT));
                result.addAll(subCommand.alias());
            }
        }
        return result;
    }
}