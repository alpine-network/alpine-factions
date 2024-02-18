package co.crystaldev.factions.api.faction.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/17/2024
 */
public interface FlagAdapter<T> {
    @NotNull
    default String serialize(@NotNull T value) {
        return value.toString();
    }

    @Nullable
    T deserialize(@NotNull String argument);
}
