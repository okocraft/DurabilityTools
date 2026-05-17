package net.okocraft.durabilitytools.configuration.languages;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record Message(@NotNull String prefix, @NotNull String value) {

    public Message(String value) {
        this("", value);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix + this.value));
    }

    public @NotNull String value() {
        return this.value;
    }
}
