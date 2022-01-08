package net.okocraft.durabilitytools.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public interface Serializable {

    public default ConfigurationSection serialize() {
        YamlConfiguration serialized = new YamlConfiguration();
        storeContents(serialized);
        return serialized;
    }

    void storeContents(ConfigurationSection section);
}
