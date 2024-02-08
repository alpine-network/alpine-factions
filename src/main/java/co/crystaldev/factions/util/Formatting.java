package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.config.MessageConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
@UtilityClass
public final class Formatting {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private static final int TITLE_LENGTH = 54;

    @NotNull
    public static String formatPlaceholders(@Nullable String string, @NotNull Object... placeholders) {
        if (string == null)
            return "";
        if (placeholders.length == 0)
            return string;

        if (placeholders.length == 1) {
            // Replace all placeholders with given value
            string = string.replaceAll("%\\w+%", placeholders[0].toString());
        }
        else {
            for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
                String placeholder = (String) placeholders[i];
                Object rawReplacer = placeholders[i + 1];
                String formattedReplacer;

                if (rawReplacer instanceof Float || rawReplacer instanceof Double) formattedReplacer = DECIMAL_FORMAT.format(rawReplacer);
                else if (rawReplacer instanceof Boolean) formattedReplacer = (Boolean) rawReplacer ? "True" : "False";
                else if (rawReplacer instanceof Component) formattedReplacer = Reference.STRICT_MINIMESSAGE.serialize(((Component) rawReplacer).append(Components.reset()));
                else formattedReplacer = rawReplacer.toString();

                string = string.replaceAll("%" + placeholder + "%", formattedReplacer);
            }
        }

        return string;
    }

    @NotNull
    public static Component title(@NotNull Component component) {
        MessageConfig config = MessageConfig.getInstance();

        Component center = config.titleFormat.build("content", component);

        if (config.titleUsesPadding) {
            int paddingLength = Math.max(4, (TITLE_LENGTH - ComponentHelper.length(center)) / 2);
            String paddingString = StringHelper.repeat(config.paddingCharacter, paddingLength);
            Component padding = ComponentHelper.stylize(config.paddingStyle, Component.text(paddingString));
            return ComponentHelper.join(padding, center, padding);
        }
        else {
            return center;
        }
    }

    @NotNull
    public static Component title(@NotNull String component) {
        return title(Component.text(component));
    }
}
