package net.okocraft.durabilitytools.configuration.config.reapircommand.enchantmultiplierperlevel;

import org.bukkit.configuration.ConfigurationSection;

public record EnchantMultiplierPerLevel(double maxLevel1, double maxLevel2, double maxLevel3, double maxLevel4,
                                        double maxLevel5, double cursed) {

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
}
