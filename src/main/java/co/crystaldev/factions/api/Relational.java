package co.crystaldev.factions.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
public interface Relational {
    @NotNull
    String getId();

    default boolean isValid() {
        return true;
    }
}
