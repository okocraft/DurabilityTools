package net.okocraft.durabilitytools.system;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import net.okocraft.durabilitytools.DurabilityTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ReduceDamageListener implements Listener {

    private final DurabilityTools plugin;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        List<ItemStack> armorContents = Arrays.asList(event.getPlayer().getInventory().getArmorContents());
        int damage = event.getDamage();
        if (!armorContents.contains(event.getItem()) || damage == 0) {
            return;
        }

        int maxItemDamage = plugin.mainConfig().maxItemDamage();
        if (damage > maxItemDamage) {
            event.setDamage(maxItemDamage);
        }
    }
}
