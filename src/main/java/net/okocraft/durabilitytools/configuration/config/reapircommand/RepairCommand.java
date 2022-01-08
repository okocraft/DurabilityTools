package net.okocraft.durabilitytools.configuration.config.reapircommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import net.okocraft.durabilitytools.configuration.config.reapircommand.basecost.BaseCost;
import net.okocraft.durabilitytools.configuration.config.reapircommand.enchantmultiplierperlevel.EnchantMultiplierPerLevel;
import org.bukkit.configuration.ConfigurationSection;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data
class RepairCommand implements Serializable {

    private double maxCost;
    private @NonNull BaseCost baseCost;
    private @NonNull EnchantMultiplierPerLevel enchantMultiplierPerLevel;

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

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("max-cost", maxCost);
        baseCost.storeContents(section.createSection("base-cost"));
        enchantMultiplierPerLevel.storeContents(section.createSection("enchant-multiplier-per-level"));
    }
}
