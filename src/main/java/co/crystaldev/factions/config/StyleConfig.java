package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.api.faction.FactionRelation;
import de.exlll.configlib.Comment;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @since 0.1.0
 */
public final class StyleConfig extends AlpineConfig {

    @Override
    public String getFileName() {
        return "fstyles.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            ""
    })
    public HashMap<FactionRelation, String> relationalStyles = new LinkedHashMap<>();
    {
        this.relationalStyles.put(FactionRelation.SELF, "green");
        this.relationalStyles.put(FactionRelation.NEUTRAL, "aqua");
        this.relationalStyles.put(FactionRelation.ENEMY, "red");
        this.relationalStyles.put(FactionRelation.ALLY, "dark_purple");
        this.relationalStyles.put(FactionRelation.TRUCE, "light_purple");
    }

    @Comment("")
    public HashMap<String, String> factionNameStyles = new LinkedHashMap<>();
    {
        this.factionNameStyles.put("Wilderness", "dark_green");
        this.factionNameStyles.put("SafeZone", "gold");
        this.factionNameStyles.put("WarZone", "dark_red");
    }

    @Comment({
            "",
            "Styles listed here will override the global styles above.",
            "Different from the other styles in this config, these do",
            "not get directly processed into a component. This is more",
            "for interacting with specific plugins via PAPI."
    })
    public HashMap<FactionRelation, String> relationalStylePlaceholderOverrides = new LinkedHashMap<>();
    {
        this.relationalStylePlaceholderOverrides.put(FactionRelation.SELF, "&a");
    }
}
