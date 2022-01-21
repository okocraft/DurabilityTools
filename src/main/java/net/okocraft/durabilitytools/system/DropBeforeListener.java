package net.okocraft.durabilitytools.system;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.okocraft.durabilitytools.DurabilityTools;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class DropBeforeListener implements Listener {

    private final DurabilityTools plugin;

    private static final NamespacedKey DROPPED =
            Objects.requireNonNull(NamespacedKey.fromString("dropped"));

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void beforeItemBreak(PlayerItemDamageEvent event) {
        beforeItemBreak(event, event.getPlayer(), event.getItem());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void beforeItemBreak(PlayerInteractEvent event) {
        // (ignoreCancelled = true) for PlayerInteractEvent
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.useInteractedBlock() == Event.Result.DENY && event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        beforeItemBreak(event, event.getPlayer(), event.getItem());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void beforeItemBreak(PlayerInteractEntityEvent event) {
        beforeItemBreak(event, event.getPlayer(), event.getPlayer().getInventory().getItem(event.getHand()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void beforeItemBreak(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        beforeItemBreak(event, player, player.getInventory().getItemInMainHand());
    }

    public void beforeItemBreak(Cancellable event, Player player, ItemStack used) {
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (!player.hasPermission("durabilitytools.system.dropbefore")) {
            return;
        }
        if (used == null) {
            return;
        }
        ItemMeta meta = used.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }
        Damageable damageable = (Damageable) meta;
        int maxDurability = used.getType().getMaxDurability();
        if (maxDurability == 0 || damageable.getDamage() < maxDurability - 1) {
            return;
        }

        EquipmentSlot itemSlotTemp = null;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotItem = player.getInventory().getItem(slot);
            if (slotItem != null && !slotItem.getType().isAir() && slotItem.equals(used)) {
                itemSlotTemp = slot;
                break;
            }
        }
        final EquipmentSlot itemSlot = itemSlotTemp;
        if (itemSlot == null || !plugin.mainConfig().appliedSlotsDropBroken().contains(itemSlot)) {
            return;
        }

        event.setCancelled(true);

        ItemStack drop = used.clone();
        drop.setAmount(1);
        damageable.setDamage(drop.getType().getMaxDurability() - 1);
        damageable.getPersistentDataContainer().set(DROPPED, PersistentDataType.STRING, player.getUniqueId().toString());
        drop.setItemMeta(damageable);

        if (player.getInventory().getItem(itemSlot).equals(used)) {
            if (used.getAmount() > 1) {
                used.setAmount(used.getAmount() - 1);
                player.getInventory().setItem(itemSlot, used);
            } else {
                player.getInventory().setItem(itemSlot, null);
            }
        } else {
            player.getInventory().removeItem(drop);
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(drop);
        player.dropItem(false);
        if (plugin.mainConfig().debug()) {
            plugin.getLogger().info("debug: " + player.getName() + " broke " + drop.getType().name() + " but cancelled and dropped it.");
        }
        player.getInventory().setItemInMainHand(handItem);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        String droppedPlayerUidStr = meta.getPersistentDataContainer().get(DROPPED, PersistentDataType.STRING);
        if (droppedPlayerUidStr == null) {
            return;
        }

        UUID droppedPlayerUid;
        try {
            droppedPlayerUid = UUID.fromString(droppedPlayerUidStr);
        } catch (IllegalArgumentException e) {
            return;
        }

        meta.getPersistentDataContainer().remove(DROPPED);
        itemStack.setItemMeta(meta);
        item.setItemStack(itemStack);
        item.getPersistentDataContainer().set(DROPPED, PersistentDataType.STRING, droppedPlayerUid.toString());
    }

    private UUID getDroppedEntityUid(Item item) {
        try {
            return UUID.fromString(
                    item.getPersistentDataContainer().getOrDefault(DROPPED, PersistentDataType.STRING, "")
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickupItem(EntityPickupItemEvent event) {
        UUID dropped = getDroppedEntityUid(event.getItem());
        if (dropped != null && !dropped.equals(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onItemDamaged(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Item)) {
            return;
        }
        Item item = (Item) entity;
        if (getDroppedEntityUid(item) == null) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
        }
    }
}
