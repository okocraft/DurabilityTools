package net.okocraft.durabilitytools.configuration.config.reapircommand;

import net.okocraft.durabilitytools.configuration.config.reapircommand.basecost.BaseCost;
import net.okocraft.durabilitytools.configuration.config.reapircommand.enchantmultiplierperlevel.EnchantMultiplierPerLevel;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public record RepairCommand(double maxCost, @NotNull BaseCost baseCost,
                            @NotNull EnchantMultiplierPerLevel enchantMultiplierPerLevel) {

    public static final RepairCommand DEFAULT_CONTENTS = new RepairCommand();

    private RepairCommand() {
        this(
            100000,
            BaseCost.DEFAULT_CONTENTS,
            EnchantMultiplierPerLevel.DEFAULT_CONTENTS
        );
    }

    public static RepairCommand deserialize(ConfigurationSection section) {
        RepairCommand def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        return new RepairCommand(
            section.getDouble("max-cost", def.maxCost),
            BaseCost.deserialize(section.getConfigurationSection("base-cost")),
            EnchantMultiplierPerLevel.deserialize(section.getConfigurationSection("enchant-multiplier-per-level"))
        );
    }
}
