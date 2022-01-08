package net.okocraft.durabilitytools.configuration.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import net.okocraft.durabilitytools.configuration.config.reapircommand.RepairCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Unmodifiable;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class Config implements Serializable {

    private int maxItemDamage;
    private @NonNull @Unmodifiable List<EquipmentSlot> appliedSlotsDropBroken;
    private @NonNull RepairCommand repairCommand;

    public static final Config DEFAULT_CONTENTS = new Config();

    private Config() {
        this(
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
                contents.getInt("max-item-damage", DEFAULT_CONTENTS.maxItemDamage),
                Collections.unmodifiableList(appliedSlotsDropBroken),
                RepairCommand.deserialize(contents.getConfigurationSection("repair-command"))
        );
    }
    
    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("max-item-damage", maxItemDamage);
        section.set(
                "applied-slots-drop-broken",
                appliedSlotsDropBroken.stream()
                        .map(EquipmentSlot::name)
                        .collect(Collectors.toList())
        );
        repairCommand.storeContents(section.createSection("repair-command"));
    }
}
