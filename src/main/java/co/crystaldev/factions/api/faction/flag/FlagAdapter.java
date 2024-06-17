package co.crystaldev.factions.api.faction.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public interface FlagAdapter<T> {
    @NotNull
    default String serialize(@NotNull T value) {
        return value.toString();
    }

    @Nullable
    T deserialize(@NotNull String argument);
}
