package net.okocraft.durabilitytools.system;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.okocraft.durabilitytools.DurabilityTools;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

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

        ItemStack item = event.getBrokenItem().asOne();
        boolean canDrop = false;
        if (item.getItemMeta() instanceof Damageable meta) {
            if (meta.hasMaxDamage()) {
                meta.setDamage(meta.getMaxDamage() - 1);
                canDrop = true;
                item.setItemMeta(meta);
            } else if (1 < item.getType().getMaxDurability()) {
                meta.setDamage(item.getType().getMaxDurability() - 1);
                canDrop = true;
                item.setItemMeta(meta);
            }
        }

        if (!canDrop) {
            return;
        }

        Location spawnLocation = player.getEyeLocation().add(0, -0.3, 0);
        Item drop = player.getWorld().createEntity(spawnLocation, Item.class);
        this.prepareItemEntityToDrop(drop, player, item);

        if (!new PlayerDropItemEvent(player, drop).callEvent()) {
            player.getInventory().addItem(item);
            return;
        }

        this.decorateItemEntity(drop, player);
        this.increaseDropStat(player, item);
        drop.spawnAt(spawnLocation, CreatureSpawnEvent.SpawnReason.DEFAULT);

        if (plugin.mainConfig().debug()) {
            plugin.getLogger().info("debug: " + player.getName() + " broke " + item.getType().name() + " but cancelled and dropped it.");
        }
    }

    private void prepareItemEntityToDrop(Item item, Player owner, ItemStack originalItem) {
        // See: ServerPlayer#createItemStackToDrop
        item.setItemStack(originalItem);
        item.setThrower(owner.getUniqueId());
        item.setPickupDelay(40);

        item.setVelocity(owner.getEyeLocation().getDirection().multiply(0.3));
    }

    private void decorateItemEntity(Item item, Player owner) {
        item.getPersistentDataContainer().set(DROPPED, PersistentDataType.STRING, owner.getUniqueId().toString());
        item.setGlowing(true);

        if (COMPONENT_SUPPORTED) {
            ItemStack itemStack = item.getItemStack();
            Component itemDisplayName = itemStack.getItemMeta().displayName();
            item.customName(
                    Component.text()
                            .append(Component.text(owner.getName() + "'s "))
                            .append(itemDisplayName != null ? itemDisplayName : Component.translatable(itemStack.getType().translationKey()))
                            .build()
            );
            item.setCustomNameVisible(true);
        }
    }

    // See: ServerPlayer#drop
    private void increaseDropStat(Player player, ItemStack item) {
        if (!item.isEmpty()) {
            player.incrementStatistic(Statistic.DROP, item.getType());
        }

        player.incrementStatistic(Statistic.DROP_COUNT);
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
