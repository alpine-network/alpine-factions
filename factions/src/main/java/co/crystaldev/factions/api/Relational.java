package co.crystaldev.factions.api;

import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public interface Relational {

    @NotNull String getId();

    default boolean isValid() {
        return true;
    }
}
