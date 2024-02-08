package co.crystaldev.factions;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.api.faction.RelationType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.exlll.configlib.Comment;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 * <br>
 * TODO: introduce Activatable priority in AlpineCore
 * This class needs to be loaded before the other configuration classes
 * due to our Adventure MiniMessage tag resolvers.
 */
public final class StyleConfig extends AlpineConfig {

    public static final Map<StyleBuilderApplicable, List<String>> STYLE_TO_ALIAS_MAP = ImmutableMap.<StyleBuilderApplicable, List<String>>builder()
            .put(TextDecoration.BOLD, ImmutableList.of("bold", "&l", "l"))
            .put(TextDecoration.ITALIC, ImmutableList.of("italic", "&o", "o"))
            .put(TextDecoration.OBFUSCATED, ImmutableList.of("obfuscated", "&k", "k", "magic"))
            .put(TextDecoration.UNDERLINED, ImmutableList.of("underlined", "&n", "n", "underline", "uline"))
            .put(TextDecoration.STRIKETHROUGH, ImmutableList.of("strikethrough", "&m", "m", "strike"))
            .put(NamedTextColor.BLACK, ImmutableList.of("black", "&0", "0"))
            .put(NamedTextColor.DARK_BLUE, ImmutableList.of("dark_blue", "&1", "1"))
            .put(NamedTextColor.DARK_GREEN, ImmutableList.of("dark_green", "&2", "2"))
            .put(NamedTextColor.DARK_AQUA, ImmutableList.of("dark_aqua", "&3", "3"))
            .put(NamedTextColor.DARK_RED, ImmutableList.of("dark_red", "&4", "4"))
            .put(NamedTextColor.DARK_PURPLE, ImmutableList.of("dark_purple", "&5", "5", "dark_magenta"))
            .put(NamedTextColor.GOLD, ImmutableList.of("gold", "&6", "6"))
            .put(NamedTextColor.GRAY, ImmutableList.of("gray", "grey", "&7", "7"))
            .put(NamedTextColor.DARK_GRAY, ImmutableList.of("dark_gray", "dark_grey", "&8", "8"))
            .put(NamedTextColor.BLUE, ImmutableList.of("blue", "&9", "9"))
            .put(NamedTextColor.GREEN, ImmutableList.of("green", "&a", "a"))
            .put(NamedTextColor.AQUA, ImmutableList.of("aqua", "&b", "b"))
            .put(NamedTextColor.RED, ImmutableList.of("red", "&c", "c"))
            .put(NamedTextColor.LIGHT_PURPLE, ImmutableList.of("light_purple", "&d", "d", "magenta", "purple"))
            .put(NamedTextColor.YELLOW, ImmutableList.of("yellow", "&e", "e"))
            .put(NamedTextColor.WHITE, ImmutableList.of("white", "&f", "f"))
            .build();

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
    public HashMap<String, String> styles = new HashMap<>();
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

    public HashMap<RelationType, String> relationalStyles = new HashMap<>();
    {
        this.relationalStyles.put(RelationType.SELF, "green");
        this.relationalStyles.put(RelationType.NEUTRAL, "aqua");
        this.relationalStyles.put(RelationType.ENEMY, "red");
        this.relationalStyles.put(RelationType.ALLY, "dark_purple");
        this.relationalStyles.put(RelationType.TRUCE, "light_purple");
    }

    public HashMap<String, String> factionNameStyles = new HashMap<>();
    {
        this.factionNameStyles.put("Wilderness", "dark_green");
        this.factionNameStyles.put("SafeZone", "gold");
        this.factionNameStyles.put("WarZone", "dark_red");
    }

    @NotNull
    public static List<StyleBuilderApplicable> parseStyle(@NotNull String style) {
        List<StyleBuilderApplicable> styles = new ArrayList<>();
        for (String component : style.split(" +")) {
            StyleBuilderApplicable parsedComponent = null;

            // match any known style or color
            for (Map.Entry<StyleBuilderApplicable, List<String>> entry : STYLE_TO_ALIAS_MAP.entrySet()) {
                List<String> aliases = entry.getValue();
                if (aliases.contains(component.toLowerCase())) {
                    parsedComponent = entry.getKey();
                    break;
                }
            }

            // unknown style, try parsing as a color (might be hex)
            if (parsedComponent == null) {
                if (!component.startsWith(TextColor.HEX_PREFIX)) {
                    component = TextColor.HEX_PREFIX + component;
                }
                parsedComponent = TextColor.fromHexString(component);
            }

            if (parsedComponent != null) {
                styles.add(parsedComponent);
            }
        }

        return styles;
    }
}
