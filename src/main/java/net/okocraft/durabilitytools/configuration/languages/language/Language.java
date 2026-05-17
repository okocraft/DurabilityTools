package net.okocraft.durabilitytools.configuration.languages.language;

import net.okocraft.durabilitytools.configuration.languages.language.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public record Language(@NotNull String pluginPrefix, @NotNull Command command) {

    public static final String DEFAULT_PLUGIN_PREFIX = "&7[&eD&aT&7]&r ";
    /**
     * default en_us
     */
    public static final Language DEFAULT_CONTENTS = new Language();

    private Language() {
        this(
            DEFAULT_PLUGIN_PREFIX,
            Command.DEFAULT_CONTENTS
        );
    }

    public static Language deserialize(ConfigurationSection section) {
        if (section == null) {
            return DEFAULT_CONTENTS;
        }

        return new Language(
            section.getString("plugin-prefix", DEFAULT_CONTENTS.pluginPrefix),
            Command.deserialize(section.getConfigurationSection("command"))
        );
    }
}
