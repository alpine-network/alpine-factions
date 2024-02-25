package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.api.faction.FactionRelation;
import de.exlll.configlib.Comment;
import lombok.Getter;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 */
public final class StyleConfig extends AlpineConfig {

    @Getter
    private static StyleConfig instance;
    { instance = this; }

    @Override
    public String getFileName() {
        return "style_config.yml";
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

    public HashMap<String, String> factionNameStyles = new LinkedHashMap<>();
    {
        this.factionNameStyles.put("Wilderness", "dark_green");
        this.factionNameStyles.put("SafeZone", "gold");
        this.factionNameStyles.put("WarZone", "dark_red");
    }
}
