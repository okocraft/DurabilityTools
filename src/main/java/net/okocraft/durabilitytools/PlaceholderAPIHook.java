package net.okocraft.durabilitytools;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.okocraft.durabilitytools.command.BaseCommand;
import net.okocraft.durabilitytools.command.Commands;
import net.okocraft.durabilitytools.command.RepairCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;

    PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dt";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null || !identifier.startsWith(getIdentifier()) || !(plugin instanceof DurabilityTools)) {
            return "0";
        }

        Commands commands = ((DurabilityTools) plugin).commands();
        if (commands == null) {
            return "0";
        }

        BaseCommand baseCommand = commands.getSubCommand("repair");
        if (!(baseCommand instanceof RepairCommand)) {
            return "0";
        }

        RepairCommand repairCommand = (RepairCommand) baseCommand;
        return String.valueOf(repairCommand.getCost(player.getInventory().getItemInMainHand()));
    }
}
