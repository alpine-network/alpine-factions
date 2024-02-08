package co.crystaldev.factions.util;

import co.crystaldev.factions.StyleConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 11/14/2023
 */
@UtilityClass
public final class ComponentHelper {

    /**
     * Wraps text based on its length.
     *
     * @param text      The text to wrap.
     * @param maxLength The maximum length of the text.
     * @return The wrapped text.
     */
    @NotNull
    public Component wrap(@NotNull String text, int maxLength) {
        return ComponentHelper.joinNewLines(Stream.of(WordUtils.wrap(text, maxLength, "\n", true))
                .map(Component::text)
                .collect(Collectors.toList()));
    }

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component join(@NotNull Component... components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with no joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component join(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinSpaces(@NotNull Component... components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }

    /**
     * Joins a variable number of components together
     * with a single space as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinSpaces(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.separator(Component.space()), components);
    }


    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinNewLines(@NotNull Component... components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Joins a variable number of components together
     * with a single new line as a joiner.
     *
     * @param components The components to join
     * @return The joined component
     */
    @NotNull
    public static Component joinNewLines(@NotNull Iterable<Component> components) {
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Apply the given style to the given component.
     *
     * @param style     The style.
     * @param component The component.
     * @return The stylized component.
     * @see StyleConfig
     */
    @NotNull
    public static Component stylize(@Nullable String style, @NotNull Component component, boolean force) {
        if (style == null) {
            return component;
        }

        if (!force) {
            return stylize(style, component);
        }

        for (StyleBuilderApplicable type : StyleConfig.parseStyle(style)) {
            if (type instanceof TextColor) {
                component = component.color((TextColor) type);
            }
            else {
                component = component.decorate((TextDecoration) type);
            }
        }
        return component;
    }

    /**
     * Apply the given style to the given component.
     *
     * @param style     The style.
     * @param component The component.
     * @return The stylized component.
     * @see StyleConfig
     */
    @NotNull
    public static Component stylize(@Nullable String style, @NotNull Component component) {
        if (style == null) {
            return component;
        }

        TextComponent.Builder builder = Component.text();
        for (StyleBuilderApplicable type : StyleConfig.parseStyle(style)) {
            if (type instanceof TextColor) {
                builder.color((TextColor) type);
            }
            else {
                builder.decorate((TextDecoration) type);
            }
        }
        builder.append(component);
        return builder.asComponent();
    }
}
