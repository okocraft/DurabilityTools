package net.okocraft.durabilitytools.configuration.languages.language.command.repaircommand;

import net.okocraft.durabilitytools.configuration.languages.Message;
import net.okocraft.durabilitytools.configuration.languages.PlaceholderMessage;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RepairCommand(@NotNull Message economyIsNotEnabled, @NotNull Message cannotRepairAir,
                            @NotNull Message cannotRepairIt, @NotNull Message itemIsNotDamaged,
                            @NotNull Message notEnoughMoney,
                            @NotNull PlaceholderMessage<Double> notifyCost) {

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
}
