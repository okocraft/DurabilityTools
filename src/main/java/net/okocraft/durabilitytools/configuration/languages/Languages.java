package net.okocraft.durabilitytools.configuration.languages;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.okocraft.durabilitytools.DurabilityTools;
import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Languages {

    private static final String DEFAULT_LANGUAGE = "en_us";
    
    private final DurabilityTools plugin;
    
    private final Map<String, Language> languages = new HashMap<>();

    public Languages(DurabilityTools plugin) {
        this.plugin = plugin;
        loadLanguage(DEFAULT_LANGUAGE);
    }

    private Language loadLanguage(String languageCode) {
        ConfigurationSection languageSection = languageConfig(languageCode);
        if (languageSection.getKeys(true).isEmpty()) {
            return null;
        }
        Language language = Language.deserialize(languageSection);
        languages.put(languageCode, language);
        return language;
    }

    private YamlConfiguration languageConfig(String languageCode) {
        return plugin.getConfig("languages/" + languageCode + ".yml");
    }

    public Language language(CommandSender viewer) {
        String languageCode = DEFAULT_LANGUAGE;
        if (viewer instanceof Player) {
            languageCode = ((Player) viewer).getLocale();
        }
        return language(languageCode);
    }
    
    private Language language(String languageCode) throws IllegalStateException {
        Language lang = languages.get(languageCode);
        if (lang != null) {
            return lang;
        }

        lang = loadLanguage(languageCode);
        if (lang != null) {
            return lang;
        }
        return Optional.ofNullable(languages.get(DEFAULT_LANGUAGE)).orElseThrow(
                () -> new IllegalStateException("Default language " + DEFAULT_LANGUAGE + " is not included in jar.")
        );
    }
}