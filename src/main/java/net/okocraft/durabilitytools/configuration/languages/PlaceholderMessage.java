package net.okocraft.durabilitytools.configuration.languages;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record PlaceholderMessage<P1>(@NotNull String prefix, @NotNull String value, @NotNull String placeholderKey) {

    public PlaceholderMessage(String value, String placeholderKey) {
        this("", value, placeholderKey);
    }

    public void sendTo(CommandSender sender, P1 placeholderValue) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
            '&',
            this.prefix + this.value.replace(this.placeholderKey, placeholderValue.toString())
        ));
    }

    public @NotNull String value() {
        return this.value;
    }

}
