package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.AlpineFactions;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
     * Create a component representing a null element.
     *
     * @return The 'null' component.
     */
    @NotNull
    public static Component nil() {
        return Component.text("< null >");
    }

    /**
     * Deserialize a component from a MiniMessage string.
     *
     * @param serializedComponent The serialized component.
     * @return The component.
     */
    @NotNull
    public static Component mini(@NotNull String serializedComponent) {
        return AlpineFactions.getInstance().getMiniMessage().deserialize(serializedComponent);
    }

    /**
     * Serialize a component into a MiniMessage string.
     *
     * @param toSerialize The component to serialize.
     * @return The serialized component.
     */
    @NotNull
    public static String mini(@NotNull Component toSerialize) {
        return AlpineFactions.getInstance().getMiniMessage().serialize(toSerialize);
    }

    /**
     * Deserialize a component from a legacy component string.
     *
     * @param serializedComponent The serialized component.
     * @return The component.
     */
    @NotNull
    public static Component legacy(@NotNull String serializedComponent) {
        char ch = serializedComponent.contains("§") ? '§' : '&';
        return LegacyComponentSerializer.legacy(ch).deserialize(serializedComponent);
    }

    /**
     * Serialize a component into a legacy component string.
     *
     * @param toSerialize The component to serialize.
     * @return The serialized component.
     */
    public static String legacy(@NotNull Component toSerialize) {
        return LegacyComponentSerializer.legacySection().serialize(toSerialize);
    }

    /**
     * Get the plain text held in a component.
     *
     * @param component The component.
     * @return The text.
     */
    @NotNull
    public static String plain(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param hover   The text to display while hovering.
     * @param command The command to execute when the component is clicked.
     * @return The component.
     */
    @NotNull
    public static Component events(@NotNull Component component, @NotNull Component hover, @NotNull String command) {
        return component.hoverEvent(HoverEvent.showText(hover)).clickEvent(ClickEvent.runCommand(command));
    }

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param both The text to display while hovering and command to execute when clicked.
     * @return The component.
     */
    @NotNull
    public static Component events(@NotNull Component component, @NotNull Component both) {
        return events(component, both, plain(both));
    }

    /**
     * Add events to the given component.
     *
     * @param component The component to add hover and click events to.
     * @param both The text to display while hovering and command to execute when clicked.
     * @return The component.
     */
    @NotNull
    public static Component events(@NotNull Component component, @NotNull String both) {
        return events(component, Component.text(both), both);
    }

    /**
     * Get the length of the given component.
     *
     * @param component The component.
     * @return The length.
     */
    public static int length(@Nullable Component component) {
        if (component == null) {
            return 0;
        }

        return plain(component).length();
    }

    /**
     * Wraps text based on its length.
     *
     * @param text      The text to wrap.
     * @param maxLength The maximum length of the text.
     * @return The wrapped text.
     */
    @NotNull
    public static Component wrap(@NotNull String text, int maxLength) {
        return Components.joinNewLines(Stream.of(WordUtils.wrap(text, maxLength, "\n", true))
                .map(Component::text)
                .collect(Collectors.toList()));
    }
}
