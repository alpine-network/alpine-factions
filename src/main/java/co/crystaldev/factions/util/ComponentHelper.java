package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 11/14/2023
 */
@UtilityClass
public final class ComponentHelper {

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
}
