package net.okocraft.durabilitytools.configuration.languages.language.command.reloadcommand;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import net.okocraft.durabilitytools.configuration.Serializable;
import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.language.Language;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class ReloadCommand implements Serializable {

    private @NonNull Message start;
    private @NonNull Message complete;

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

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("start", start.value());
        section.set("complete", complete.value());
    }
}
