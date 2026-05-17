package net.okocraft.durabilitytools.configuration.languages;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record BiPlaceholderMessage<P1, P2>(@NotNull String prefix, @NotNull String value,
                                           @NotNull String placeholder1Key, @NotNull String placeholder2Key) {

    public BiPlaceholderMessage(String value, String placeholder1Key, String placeholder2Key) {
        this("", value, placeholder1Key, placeholder2Key);
    }

    public void sendTo(CommandSender sender, P1 placeholder1Value, P2 placeholder2Value) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
            '&',
            this.prefix + this.value
                .replace(this.placeholder1Key, placeholder1Value.toString())
                .replace(this.placeholder2Key, placeholder2Value.toString())
        ));
    }
}
