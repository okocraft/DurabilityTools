package net.okocraft.durabilitytools.configuration.config.reapircommand.enchantmultiplierperlevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import org.bukkit.configuration.ConfigurationSection;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class EnchantMultiplierPerLevel implements Serializable {

    private double maxLevel1;
    private double maxLevel2;
    private double maxLevel3;
    private double maxLevel4;
    private double maxLevel5;
    private double cursed;

    public static final EnchantMultiplierPerLevel DEFAULT_CONTENTS = new EnchantMultiplierPerLevel();

    private EnchantMultiplierPerLevel() {
        this(
                3.0,
                2.0,
                1.6,
                1.2,
                1.0,
                -1
        );
    }

    public static EnchantMultiplierPerLevel deserialize(ConfigurationSection section) {
        EnchantMultiplierPerLevel def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        return new EnchantMultiplierPerLevel(
                section.getDouble("max-level-1", def.maxLevel1),
                section.getDouble("max-level-2", def.maxLevel2),
                section.getDouble("max-level-3", def.maxLevel3),
                section.getDouble("max-level-4", def.maxLevel4),
                section.getDouble("max-level-5", def.maxLevel5),
                section.getDouble("cursed", def.cursed)
        );
    }

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("max-level-1", maxLevel1);
        section.set("max-level-2", maxLevel2);
        section.set("max-level-3", maxLevel3);
        section.set("max-level-4", maxLevel4);
        section.set("max-level-5", maxLevel5);
        section.set("cursed", cursed);
    }
}