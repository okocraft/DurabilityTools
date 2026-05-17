package net.okocraft.durabilitytools.configuration.config.reapircommand.basecost;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record BaseCost(double wood, double stone, double leather, double gold, double chain, double iron, double bow,
                       double diamond, double netherite) {

    public static final BaseCost DEFAULT_CONTENTS = new BaseCost();

    private BaseCost() {
        this(
            200,
            400,
            600,
            700,
            900,
            1200,
            1600,
            1600,
            2000
        );
    }

    public @UnmodifiableView Map<String, Double> costs() {
        Map<String, Double> costs = new HashMap<>();
        costs.put("wood", wood);
        costs.put("stone", stone);
        costs.put("leather", leather);
        costs.put("gold", gold);
        costs.put("chain", chain);
        costs.put("iron", iron);
        costs.put("bow", bow);
        costs.put("diamond", diamond);
        costs.put("netherite", netherite);
        return Collections.unmodifiableMap(costs);
    }

    public static BaseCost deserialize(ConfigurationSection section) {
        BaseCost def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        return new BaseCost(
            section.getDouble("wood", def.wood),
            section.getDouble("stone", def.stone),
            section.getDouble("leather", def.leather),
            section.getDouble("gold", def.gold),
            section.getDouble("chain", def.chain),
            section.getDouble("iron", def.iron),
            section.getDouble("bow", def.bow),
            section.getDouble("diamond", def.diamond),
            section.getDouble("netherite", def.netherite)
        );
    }
}
