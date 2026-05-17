package net.okocraft.durabilitytools.configuration.languages.language.command.helpcommand;

import net.okocraft.durabilitytools.configuration.languages.BiPlaceholderMessage;
import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import net.okocraft.durabilitytools.configuration.languages.language.command.helpcommand.description.Description;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record HelpCommand(@NotNull Message noPermittedCommand, @NotNull Message line,
                          @NotNull BiPlaceholderMessage<String, String> content,
                          @NotNull Description description) {

    public static final HelpCommand DEFAULT_CONTENTS = new HelpCommand();

    private HelpCommand() {
        this(
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&cYou don''t have permissions for any of the commands.."
            ),
            new Message("&b========================================="),
            new BiPlaceholderMessage<>(
                "&6(&a%usage%&6)&2 - %description%",
                "%usage%",
                "%description%"
            ),
            Description.DEFAULT_CONTENTS
        );
    }

    public static HelpCommand deserialize(ConfigurationSection section) {
        HelpCommand def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        String pluginPrefix = Optional.ofNullable(section.getParent())
            .map(ConfigurationSection::getParent)
            .map(parent -> parent.getString("plugin-prefix"))
            .orElse(Language.DEFAULT_PLUGIN_PREFIX);

        return new HelpCommand(
            new Message(pluginPrefix, section.getString("no-permitted-command", def.noPermittedCommand.value())),
            new Message(section.getString("line", def.line.value())),
            new BiPlaceholderMessage<>(section.getString("content", def.content.value()), "%usage%", "%description%"),
            Description.deserialize(section.getConfigurationSection("description"))
        );
    }
}
