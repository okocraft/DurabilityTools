package net.okocraft.durabilitytools.configuration.languages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class BiPlaceholderMessage<P1, P2> {

    private @NotNull String prefix;
    private @NotNull String value;
    private @NotNull String placeholder1Key;
    private @NotNull String placeholder2Key;

    public BiPlaceholderMessage(String value, String placeholder1Key, String placeholder2Key) {
        this("", value, placeholder1Key, placeholder2Key);
    }

    public void sendTo(CommandSender sender, P1 placeholder1Value, P2 placeholder2Value) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                prefix + value
                        .replace(placeholder1Key, placeholder1Value.toString())
                        .replace(placeholder2Key, placeholder2Value.toString())
        ));
    }
}
