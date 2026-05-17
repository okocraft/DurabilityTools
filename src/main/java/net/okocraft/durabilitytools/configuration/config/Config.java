package net.okocraft.durabilitytools.configuration.config;

import net.okocraft.durabilitytools.configuration.config.reapircommand.RepairCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Config(boolean debug, int maxItemDamage,
                     @NotNull @Unmodifiable List<EquipmentSlot> appliedSlotsDropBroken,
                     @NotNull RepairCommand repairCommand) {

    public static final Config DEFAULT_CONTENTS = new Config();

    private Config() {
        this(
            true,
            3,
            List.of(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD),
            RepairCommand.DEFAULT_CONTENTS
        );
    }

    public static Config deserialize(ConfigurationSection contents) {
        if (contents == null) {
            return DEFAULT_CONTENTS;
        }

        List<EquipmentSlot> appliedSlotsDropBroken = new ArrayList<>();
        for (String slotName : contents.getStringList("applied-slots-drop-broken")) {
            try {
                appliedSlotsDropBroken.add(EquipmentSlot.valueOf(slotName));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new Config(
            contents.getBoolean("debug"),
            contents.getInt("max-item-damage", DEFAULT_CONTENTS.maxItemDamage),
            Collections.unmodifiableList(appliedSlotsDropBroken),
            RepairCommand.deserialize(contents.getConfigurationSection("repair-command"))
        );
    }
}
