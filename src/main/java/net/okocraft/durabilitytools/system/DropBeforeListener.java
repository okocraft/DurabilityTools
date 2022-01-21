package net.okocraft.durabilitytools.system;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.okocraft.durabilitytools.DurabilityTools;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
        if (!event.getPlayer().hasPermission("durabilitytools.system.dropbefore")) {
            return;
        }
        ItemStack item = event.getItem();

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }
        Damageable damageable = (Damageable) meta;

        if (item.getType().getMaxDurability() > event.getDamage() + damageable.getDamage()) {
            return;
        }

        EquipmentSlot itemSlotTemp = null;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotItem = event.getPlayer().getInventory().getItem(slot);
            if (slotItem != null && !slotItem.getType().isAir() && slotItem.equals(item)) {
                itemSlotTemp = slot;
                break;
            }
        }
        final EquipmentSlot itemSlot = itemSlotTemp;
        if (itemSlot == null || !plugin.mainConfig().appliedSlotsDropBroken().contains(itemSlot)) {
            return;
        }

        event.setCancelled(true);

        ItemStack drop = item.clone();
        drop.setAmount(1);
        damageable.setDamage(drop.getType().getMaxDurability() - 1);
        damageable.getPersistentDataContainer().set(DROPPED, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
        drop.setItemMeta(damageable);

        if (event.getPlayer().getInventory().getItem(itemSlot).equals(item)) {
            event.getPlayer().getInventory().setItem(itemSlot, null);
        } else {
            event.getPlayer().getInventory().removeItem(drop);
        }

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        event.getPlayer().getInventory().setItemInMainHand(drop);
        event.getPlayer().dropItem(false);
        if (plugin.mainConfig().debug()) {
            plugin.getLogger().info("debug: " + event.getPlayer().getName() + " broke " + drop.getType().name() + " but cancelled and dropped it.");
        }
        event.getPlayer().getInventory().setItemInMainHand(handItem);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void beforeItemBreak(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE
                || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (!event.getPlayer().hasPermission("durabilitytools.system.dropbefore")) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }
        Damageable damageable = (Damageable) meta;
        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability == 0 || damageable.getDamage() < maxDurability - 1) {
            return;
        }

        EquipmentSlot itemSlotTemp = null;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotItem = event.getPlayer().getInventory().getItem(slot);
            if (slotItem != null && !slotItem.getType().isAir() && slotItem.equals(item)) {
                itemSlotTemp = slot;
                break;
            }
        }
        final EquipmentSlot itemSlot = itemSlotTemp;
        if (itemSlot == null || !plugin.mainConfig().appliedSlotsDropBroken().contains(itemSlot)) {
            return;
        }

        event.setCancelled(true);

        ItemStack drop = item.clone();
        drop.setAmount(1);
        damageable.setDamage(drop.getType().getMaxDurability() - 1);
        damageable.getPersistentDataContainer().set(DROPPED, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
        drop.setItemMeta(damageable);

        if (event.getPlayer().getInventory().getItem(itemSlot).equals(item)) {
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                event.getPlayer().getInventory().setItem(itemSlot, item);
            } else {
                event.getPlayer().getInventory().setItem(itemSlot, null);
            }
        } else {
            event.getPlayer().getInventory().removeItem(drop);
        }

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        event.getPlayer().getInventory().setItemInMainHand(drop);
        event.getPlayer().dropItem(false);
        if (plugin.mainConfig().debug()) {
            plugin.getLogger().info("debug: " + event.getPlayer().getName() + " broke " + drop.getType().name() + " but cancelled and dropped it.");
        }
        event.getPlayer().getInventory().setItemInMainHand(handItem);
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
