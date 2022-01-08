package net.okocraft.durabilitytools.configuration.languages.language.command.helpcommand.description;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import org.bukkit.configuration.ConfigurationSection;

@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "descriptions")
public @Data class Description implements Serializable {

    private @NonNull String help;
    private @NonNull String reload;
    private @NonNull String repair;

    private final Map<String, String> descriptions = new HashMap<>();

    public static final Description DEFAULT_CONTENTS = new Description();

    private Description() {
        this(
                "&7Show command help.",
                "&7Reloads the config.",
                "&7Repairs item in main hand."
        );
        putDescriptions();
    }

    private void putDescriptions() {
        descriptions.put("help", help);
        descriptions.put("reload", reload);
        descriptions.put("repair", repair);
    }

    public String get(String command) {
        return descriptions.get(command);
    }

    public static Description deserialize(ConfigurationSection section) {
        Description def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        Description description = new Description(
                section.getString("help", def.help),
                section.getString("reload", def.reload),
                section.getString("repair", def.repair)
        );
        description.putDescriptions();
        return description;
    }

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("help", help);
        section.set("reload", reload);
        section.set("repair", repair);
    }
}
