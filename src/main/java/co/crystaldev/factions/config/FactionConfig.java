package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import de.exlll.configlib.Comment;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 */
public final class FactionConfig extends AlpineConfig {

    @Getter
    private static FactionConfig instance;
    { instance = this; }

    @Override
    public String getFileName() {
        return "factions_config.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            "",
            "The minimum length of a faction name."
    })
    public int minNameLength = 3;

    @Comment("The maximum length of a faction name.")
    public int maxNameLength = 10;
}
