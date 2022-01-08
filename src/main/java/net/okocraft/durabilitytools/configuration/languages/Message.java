package net.okocraft.durabilitytools.configuration.languages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class Message {

    private @NonNull String prefix;
    private @NonNull String value;

    public Message(String value) {
        this("", value);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + value));
    }
}