package net.okocraft.durabilitytools.configuration.languages.language.command.helpcommand.description;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public record Description(@NotNull String help, @NotNull String reload, @NotNull String repair) {

    public static final Description DEFAULT_CONTENTS = new Description();

    private Description() {
        this(
            "&7Show command help.",
            "&7Reloads the config.",
            "&7Repairs item in main hand."
        );
    }

    public String get(String command) {
        return switch (command) {
            case "help" -> this.help;
            case "reload" -> this.reload;
            case "repair" -> this.repair;
            default -> null;
        };
    }

    public static Description deserialize(ConfigurationSection section) {
        Description def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        return new Description(
            section.getString("help", def.help),
            section.getString("reload", def.reload),
            section.getString("repair", def.repair)
        );
    }
}
