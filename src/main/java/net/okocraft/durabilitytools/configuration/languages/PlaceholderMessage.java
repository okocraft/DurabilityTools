package net.okocraft.durabilitytools.configuration.languages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class PlaceholderMessage<P1> {

    private @NonNull String prefix;
    private @NonNull String value;
    private @NonNull String placeholderKey;

    public PlaceholderMessage(String value, String placeholderKey) {
        this("", value, placeholderKey);
    }

    public void sendTo(CommandSender sender, P1 placeholderValue) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                prefix + value.replace(placeholderKey, placeholderValue.toString())
        ));
    }
}
