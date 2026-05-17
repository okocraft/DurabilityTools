package net.okocraft.durabilitytools;

import net.okocraft.durabilitytools.command.Commands;
import net.okocraft.durabilitytools.configuration.config.Config;
import net.okocraft.durabilitytools.configuration.languages.Languages;
import net.okocraft.durabilitytools.system.DropBeforeListener;
import net.okocraft.durabilitytools.system.ReduceDamageListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DurabilityTools extends JavaPlugin implements Listener {

    private @NotNull Config mainConfig = Config.deserialize(this.getConfig("config.yml"));
    private @NotNull Languages languagesConfig = new Languages(this);

    @Nullable
    private Commands commands;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new DropBeforeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ReduceDamageListener(this), this);

        this.commands = new Commands(this);
        try {
            new PlaceholderAPIHook(this).register();
        } catch (Error e) {
            this.getLogger().warning("Cannot load placeholderapi. ignored.");
        }
    }

    public void reload() {
        this.mainConfig = Config.deserialize(this.getConfig("config.yml"));
        this.languagesConfig = new Languages(this);
    }

    public void saveDefaultConfig(String configName) throws IllegalArgumentException {
        File configFile = new File(this.getDataFolder(), configName);
        if (!configFile.exists()) {
            this.saveResource(configName, false);
        }
    }

    public YamlConfiguration getConfig(String configName) {
        if (configName == null) {
            return new YamlConfiguration();
        }
        try {
            this.saveDefaultConfig(configName);
        } catch (IllegalArgumentException e) {
            return new YamlConfiguration();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), configName));
        InputStream inputStream = this.getResource(configName);
        if (inputStream != null) {
            config.setDefaults(
                YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
        }
        return config;
    }

    public @NotNull Config mainConfig() {
        return this.mainConfig;
    }

    public @NotNull Languages languagesConfig() {
        return this.languagesConfig;
    }

    public @Nullable Commands commands() {
        return this.commands;
    }
}
