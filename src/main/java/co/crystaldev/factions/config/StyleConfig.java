package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import de.exlll.configlib.Comment;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> styles = new HashMap<>();
    {

        this.styles.put("info", "dark_aqua bold");
        this.styles.put("highlight", "aqua");

        this.styles.put("error", "dark_red bold");
        this.styles.put("error_highlight", "red bold");

        this.styles.put("success", "dark_green bold");
        this.styles.put("success_highlight", "green");

        this.styles.put("emphasis", "gray");
        this.styles.put("separator", "dark_gray bold");
        this.styles.put("text", "white");
        this.styles.put("error_text", "white");
    }
}
