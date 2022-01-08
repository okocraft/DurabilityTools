package net.okocraft.durabilitytools.configuration.config.reapircommand.basecost;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.configuration.Serializable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.UnmodifiableView;

@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode(exclude = "costs")
public @Data class BaseCost implements Serializable {

    private double wood;
    private double stone;
    private double leather;
    private double gold;
    private double chain;
    private double iron;
    private double bow;
    private double diamond;
    private double netherite;

    private final Map<String, Double> costs = new HashMap<>();

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
        putBaseCosts();
    }

    private void putBaseCosts() {
        costs.put("wood", wood);
        costs.put("stone", stone);
        costs.put("leather", leather);
        costs.put("gold", gold);
        costs.put("chain", chain);
        costs.put("iron", iron);
        costs.put("bow", bow);
        costs.put("diamond", diamond);
        costs.put("netherite", netherite);
    }

    public double get(String typeName) {
        return costs.getOrDefault(typeName, 1000D);
    }

    public @UnmodifiableView Map<String, Double> costs() {
        return Collections.unmodifiableMap(costs);
    }

    public static BaseCost deserialize(ConfigurationSection section) {
        BaseCost def = DEFAULT_CONTENTS;
        if (section == null) {
            return def;
        }

        BaseCost baseCost = new BaseCost(
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
        baseCost.putBaseCosts();
        return baseCost;
    }

    @Override
    public void storeContents(ConfigurationSection section) {
        section.set("wood", wood);
        section.set("stone", stone);
        section.set("leather", leather);
        section.set("gold", gold);
        section.set("chain", chain);
        section.set("iron", iron);
        section.set("bow", bow);
        section.set("diamond", diamond);
        section.set("netherite", netherite);
    }
}