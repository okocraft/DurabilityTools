package net.okocraft.durabilitytools.system;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.okocraft.durabilitytools.DurabilityTools;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class DropBeforeListener implements Listener {

    private static final boolean COMPONENT_SUPPORTED;

    static {
        boolean componentSupported;

        try {
            Class.forName("net.kyori.adventure.text.Component");
            componentSupported = true;
        } catch (ClassNotFoundException e) {
            componentSupported = false;
        }

        COMPONENT_SUPPORTED = componentSupported;
    }

    private final DurabilityTools plugin;

    private static final NamespacedKey DROPPED = Objects.requireNonNull(NamespacedKey.fromString("dropped"));

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("durabilitytools.system.dropbefore")) {
            return;
        }

        ItemStack used = event.getBrokenItem();

        EquipmentSlot itemSlot = null;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (used.equals(player.getInventory().getItem(slot))) {
                itemSlot = slot;
                break;
            }
        }
        if (itemSlot == null || !plugin.mainConfig().appliedSlotsDropBroken().contains(itemSlot)) {
            return;
        }

        player.setMetadata(DROPPED.getKey(), new FixedMetadataValue(plugin, null));

        if (itemSlot == EquipmentSlot.HAND) {
            used.setAmount(used.getAmount() + 1);
            player.dropItem(false);
        } else {
            ItemStack drop = used.clone();
            drop.setAmount(1);
            ItemStack handItem = player.getInventory().getItemInMainHand();
            player.getInventory().setItemInMainHand(drop);
            player.dropItem(false);
            player.getInventory().setItemInMainHand(handItem);
        }

        if (plugin.mainConfig().debug()) {
            plugin.getLogger().info("debug: " + player.getName() + " broke " + used.getType().name() + " but cancelled and dropped it.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata(DROPPED.getKey())) {
            event.getPlayer().removeMetadata(DROPPED.getKey(), plugin);
            ItemStack item = event.getItemDrop().getItemStack().clone();
            Damageable damageable = Objects.requireNonNull((Damageable) item.getItemMeta());
            damageable.setDamage(damageable.getDamage() - 1);
            item.setItemMeta(damageable);
            event.getItemDrop().setItemStack(item);
            event.getItemDrop().getPersistentDataContainer()
                    .set(DROPPED, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
            event.getItemDrop().setGlowing(true);

            if (COMPONENT_SUPPORTED) {
                Component itemDisplayName = damageable.displayName();
                event.getItemDrop().customName(
                        Component.text()
                                .append(Component.text(event.getPlayer().getName() + "'s "))
                                .append(itemDisplayName != null ? itemDisplayName : Component.translatable(item.getType().translationKey()))
                                .build()
                );
                event.getItemDrop().setCustomNameVisible(true);
            }
        }
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
