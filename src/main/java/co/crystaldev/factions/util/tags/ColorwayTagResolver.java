package co.crystaldev.factions.util.tags;

import co.crystaldev.factions.config.StyleConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
public final class ColorwayTagResolver implements TagResolver {

    private static final Map<StyleBuilderApplicable, List<String>> STYLE_TO_ALIAS_MAP = ImmutableMap.<StyleBuilderApplicable, List<String>>builder()
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

    private final Map<String, Tag> styleToTagMap = new HashMap<>();

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        StyleConfig config = StyleConfig.getInstance();
        Map<String, String> styleToColor = config.styles;
        String style = styleToColor.get(name);
        if (style == null) {
            return null;
        }

        return this.styleToTagMap.computeIfAbsent(style, s -> {
            List<StyleBuilderApplicable> styles = new ArrayList<>();
            for (String component : s.split(" +")) {
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

            return Tag.styling(b -> styles.forEach(b::apply));
        });
    }

    @Override
    public boolean has(@NotNull String name) {
        Map<String, String> styleToColor = StyleConfig.getInstance().styles;
        return styleToColor.containsKey(name);
    }
}
