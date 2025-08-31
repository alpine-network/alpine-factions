package co.crystaldev.factions.util;

import co.crystaldev.factions.AlpineFactions;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class ComponentHelper {

    /**
     * Create a component representing a null element.
     *
     * @return The 'null' component.
     */
    public static @NotNull Component nil() {
        return Component.text("< null >");
    }

    /**
     * Deserialize a component from a MiniMessage string.
     *
     * @param serializedComponent The serialized component.
     * @return The component.
     */
    public static @NotNull Component mini(@NotNull String serializedComponent) {
        return AlpineFactions.getInstance().getMiniMessage().deserialize(serializedComponent);
    }

    /**
     * Serialize a component into a MiniMessage string.
     *
     * @param toSerialize The component to serialize.
     * @return The serialized component.
     */
    public static @NotNull String mini(@NotNull Component toSerialize) {
        return AlpineFactions.getInstance().getMiniMessage().serialize(toSerialize);
    }

    /**
     * Deserialize a component from a legacy component string.
     *
     * @param serializedComponent The serialized component.
     * @return The component.
     */
    public static @NotNull Component legacy(@NotNull String serializedComponent) {
        char ch = serializedComponent.contains("§") ? '§' : '&';
        return LegacyComponentSerializer.legacy(ch).deserialize(serializedComponent);
    }

    /**
     * Get the plain text held in a component.
     *
     * @param component The component.
     * @return The text.
     */
    public static @NotNull String plain(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
