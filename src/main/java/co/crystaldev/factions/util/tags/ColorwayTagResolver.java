package co.crystaldev.factions.util.tags;

import co.crystaldev.factions.config.StyleConfig;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
public final class ColorwayTagResolver implements TagResolver {

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
            List<StyleBuilderApplicable> styles = StyleConfig.parseStyle(s);
            return Tag.styling(b -> styles.forEach(b::apply));
        });
    }

    @Override
    public boolean has(@NotNull String name) {
        return StyleConfig.getInstance().styles.containsKey(name);
    }
}
