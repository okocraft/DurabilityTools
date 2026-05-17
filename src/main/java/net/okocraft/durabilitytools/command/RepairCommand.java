package net.okocraft.durabilitytools.command;

import net.milkbowl.vault.economy.Economy;
import net.okocraft.durabilitytools.DurabilityTools;
import net.okocraft.durabilitytools.configuration.config.Config;
import net.okocraft.durabilitytools.configuration.config.reapircommand.enchantmultiplierperlevel.EnchantMultiplierPerLevel;
import net.okocraft.durabilitytools.configuration.languages.Languages;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Locale;
import java.util.Map;

public class RepairCommand extends BaseCommand {

    private final Economy economy;

    public RepairCommand(Commands registration) {
        super(
            registration,
            "repair",
            "durabilitytools.commands.repair",
            1,
            true,
            "/dt repair [confirm]"
        );

        DurabilityTools plugin = registration.plugin;

        Server server = plugin.getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new IllegalStateException("Vault is not loaded.");
        }

        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Vault is not loaded.");
        }
        this.economy = rsp.getProvider();
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {

        Config config = this.plugin.mainConfig();
        Languages languages = this.plugin.languagesConfig();

        Language lang = languages.language(sender);
        var messages = lang.command().repairCommand();

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            messages.cannotRepairAir().sendTo(sender);
            return false;
        }

        if (!(item.getItemMeta() instanceof Damageable damageableMeta)) {
            messages.cannotRepairIt().sendTo(sender);
            return false;
        }

        int currentDamage = damageableMeta.getDamage();
        int maxDurability = item.getType().getMaxDurability() - 1;
        if (currentDamage == 0 || maxDurability <= 0) {
            messages.itemIsNotDamaged().sendTo(sender);
            return false;
        }

        double wearRate = Math.round(((double) currentDamage / (double) maxDurability) * 1000D) / 10D;

        double cost = Math.round(wearRate * this.getCost(item)) / 100D;
        cost = Math.min(config.repairCommand().maxCost(), cost);

        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            messages.notifyCost().sendTo(sender, cost);
            return true;
        }

        if (this.economy.getBalance(player) < cost) {
            messages.notEnoughMoney().sendTo(sender);
            return false;
        }

        this.economy.withdrawPlayer(player, cost);

        damageableMeta.setDamage(0);
        item.setItemMeta(damageableMeta);
        player.getInventory().setItemInMainHand(item);

        return true;
    }

    public double getCost(ItemStack item) {
        double base = this.getBaseCost(item.getType());
        double multiplier = 1;
        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            multiplier += this.getEnchantCost(entry.getKey(), entry.getValue());
        }

        return Math.max(base * multiplier, 0);
    }

    private double getBaseCost(Material item) {
        Map<String, Double> costs = this.plugin.mainConfig().repairCommand().baseCost().costs();
        for (String key : costs.keySet()) {
            if (item.name().startsWith(key.toUpperCase(Locale.ROOT))) {
                return costs.get(key);
            }
        }
        return 0;
    }

    private double getEnchantCost(Enchantment enchant, int level) {
        EnchantMultiplierPerLevel config = this.plugin.mainConfig().repairCommand().enchantMultiplierPerLevel();
        if (enchant.isCursed()) {
            return config.cursed() * level;
        }

        return switch (enchant.getMaxLevel()) {
            case 1 -> config.maxLevel1() * level;
            case 2 -> config.maxLevel2() * level;
            case 3 -> config.maxLevel3() * level;
            case 4 -> config.maxLevel4() * level;
            case 5 -> config.maxLevel5() * level;
            default -> 0;
        };
    }
}
