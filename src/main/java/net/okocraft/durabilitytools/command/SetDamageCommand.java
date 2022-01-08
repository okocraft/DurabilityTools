package net.okocraft.durabilitytools.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SetDamageCommand extends BaseCommand {

    protected SetDamageCommand(Commands registration) {
        super(
            registration,
            "setdamage",
            "durabilitytools.commands.setdamage",
            2,
            true,
            "/dt setdamage <damage>"
        );
    }


    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (item.getType().isAir() || item instanceof Damageable) {
            return false;
        }

        int damage = 0;
        try {
            damage = Math.max(0, Math.min(item.getType().getMaxDurability() - 1, Integer.parseInt(args[1])));
        } catch (NumberFormatException e) {
            return false;
        }

        Damageable damageable = (Damageable) meta;
        damageable.setDamage(damage);
        item.setItemMeta(damageable);
        player.getInventory().setItemInMainHand(item);
        return true;
    }
}
