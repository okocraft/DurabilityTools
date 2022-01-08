package net.okocraft.durabilitytools.configuration.languages.language;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import net.okocraft.durabilitytools.configuration.Serializable;
import net.okocraft.durabilitytools.configuration.languages.language.command.Command;

@Accessors(fluent = true)
@RequiredArgsConstructor
public @Data class Language implements Serializable {

    private @NonNull String pluginPrefix;
    private @NonNull Command command;

    public static final String DEFAULT_PLUGIN_PREFIX = "&7[&eD&aT&7]&r ";
    /** default en_us */
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

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("plugin-prefix", pluginPrefix);
        command.storeContents(section.createSection("command"));
    }
}
