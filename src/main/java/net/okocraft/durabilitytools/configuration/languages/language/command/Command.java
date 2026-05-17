package net.okocraft.durabilitytools.configuration.languages.language.command;

import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.PlaceholderMessage;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import net.okocraft.durabilitytools.configuration.languages.language.command.helpcommand.HelpCommand;
import net.okocraft.durabilitytools.configuration.languages.language.command.reloadcommand.ReloadCommand;
import net.okocraft.durabilitytools.configuration.languages.language.command.repaircommand.RepairCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Command(@NotNull Message playerOnly, @NotNull Message notEnoughArgument, @NotNull Message noSuchCommand,
                      @NotNull PlaceholderMessage<String> noPermission, @NotNull PlaceholderMessage<String> usage,
                      @NotNull PlaceholderMessage<String> noArgMessage, @NotNull HelpCommand helpCommand,
                      @NotNull ReloadCommand reloadCommand,
                      @NotNull RepairCommand repairCommand) {

    public static final Command DEFAULT_CONTENTS = new Command();

    private Command() {
        this(
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&cYou are a console, you cannot do that!"
            ),
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&cNot enough arguments!"
            ),
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&cNo such command! Try /dt help"
            ),
            new PlaceholderMessage<>(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&cNo permission for this command! (%permission%)",
                "%permission%"
            ),
            new PlaceholderMessage<>(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&7usage: %usage%",
                "%usage%"
            ),
            new PlaceholderMessage<>(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&b&lEnchants&9&l+ &bv%version% | Commands: &3/dt help",
                "%version%"
            ),
            HelpCommand.DEFAULT_CONTENTS,
            ReloadCommand.DEFAULT_CONTENTS,
            RepairCommand.DEFAULT_CONTENTS
        );
    }

    public static Command deserialize(ConfigurationSection section) {
        Command def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        String pluginPrefix = Optional.ofNullable(section.getParent())
            .map(parent -> parent.getString("plugin-prefix"))
            .orElse(Language.DEFAULT_PLUGIN_PREFIX);

        return new Command(
            new Message(pluginPrefix, section.getString("player-only", def.playerOnly.value())),
            new Message(pluginPrefix, section.getString("not-enough-argument", def.notEnoughArgument.value())),
            new Message(pluginPrefix, section.getString("no-such-command", def.noSuchCommand.value())),
            new PlaceholderMessage<>(pluginPrefix, section.getString("no-permission", def.noPermission.value()), "%permission%"),
            new PlaceholderMessage<>(pluginPrefix, section.getString("usage", def.usage.value()), "%usage%"),
            new PlaceholderMessage<>(section.getString("no-arg-message", def.noArgMessage.value()), "%version%"),
            HelpCommand.deserialize(section.getConfigurationSection("help-command")),
            ReloadCommand.deserialize(section.getConfigurationSection("reload-command")),
            RepairCommand.deserialize(section.getConfigurationSection("repair-command"))
        );
    }
}
