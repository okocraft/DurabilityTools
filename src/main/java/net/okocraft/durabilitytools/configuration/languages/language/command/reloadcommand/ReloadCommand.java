package net.okocraft.durabilitytools.configuration.languages.language.command.reloadcommand;

import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ReloadCommand(@NotNull Message start, @NotNull Message complete) {

    public static final ReloadCommand DEFAULT_CONTENTS = new ReloadCommand();

    private ReloadCommand() {
        this(
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&bReloading.."
            ),
            new Message(
                Language.DEFAULT_PLUGIN_PREFIX,
                "&bReloaded!"
            )
        );

    }

    public static ReloadCommand deserialize(ConfigurationSection section) {
        ReloadCommand def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        String pluginPrefix = Optional.ofNullable(section.getParent())
            .map(ConfigurationSection::getParent)
            .map(parent -> parent.getString("plugin-prefix"))
            .orElse(Language.DEFAULT_PLUGIN_PREFIX);

        return new ReloadCommand(
            new Message(pluginPrefix, section.getString("start", def.start.value())),
            new Message(pluginPrefix, section.getString("complete", def.complete.value()))
        );
    }
}
