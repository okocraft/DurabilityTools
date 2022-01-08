package net.okocraft.durabilitytools.configuration.languages.language.command.repaircommand;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.PlaceholderMessage;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.configuration.ConfigurationSection;

@Accessors(fluent = true)
@AllArgsConstructor
public @Data class RepairCommand implements Serializable {

    private @NonNull Message economyIsNotEnabled;
    private @NonNull Message cannotRepairAir;
    private @NonNull Message cannotRepairIt;
    private @NonNull Message itemIsNotDamaged;
    private @NonNull Message notEnoughMoney;
    private @NonNull PlaceholderMessage<Double> notifyCost;

    public static final RepairCommand DEFAULT_CONTENTS = new RepairCommand();

    private RepairCommand() {
        this(
                new Message(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&cVault is not enabled."
                ),
                new Message(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&cPlease hold item to repair."
                ),
                new Message(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&cSorry, we cannot repair your item."
                ),
                new Message(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&bItem is not damaged!"
                ),
                new Message(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&cNot enough money."
                ),
                new PlaceholderMessage<>(
                        Language.DEFAULT_PLUGIN_PREFIX,
                        "&7Cost is &b%cost% &7. If it's ok, type /dt repair confirm",
                        "%cost%"
                )
        );
        
    }

    public static RepairCommand deserialize(ConfigurationSection section) {
        RepairCommand def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        String pluginPrefix = Optional.ofNullable(section.getParent())
                .map(ConfigurationSection::getParent)
                .map(parent -> parent.getString("plugin-prefix"))
                .orElse(Language.DEFAULT_PLUGIN_PREFIX);

        return new RepairCommand(
            new Message(pluginPrefix, section.getString("economy-is-not-enabled", def.economyIsNotEnabled.value())),
            new Message(pluginPrefix, section.getString("cannot-repair-air", def.cannotRepairAir.value())),
            new Message(pluginPrefix, section.getString("cannot-repair-it", def.cannotRepairIt.value())),
            new Message(pluginPrefix, section.getString("item-is-not-damaged", def.itemIsNotDamaged.value())),
            new Message(pluginPrefix, section.getString("not-enough-money", def.notEnoughMoney.value())),
            new PlaceholderMessage<>(pluginPrefix, section.getString("notify-cost", def.notifyCost.value()), "%cost%")
        );
    }

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("economy-is-not-enabled", economyIsNotEnabled.value());
        section.set("cannot-repair-air", cannotRepairAir.value());
        section.set("cannot-repair-it", cannotRepairIt.value());
        section.set("item-is-not-damaged", itemIsNotDamaged.value());
        section.set("not-enough-money", notEnoughMoney.value());
        section.set("notify-cost", notifyCost.value());
    }
}
