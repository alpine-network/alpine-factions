package co.crystaldev.factions.api;

import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public interface Factions {

    @NotNull
    static Factions get() {
        return Reference.FACTIONS;
    }

    @NotNull
    ClaimAccessor claims();

    @NotNull
    PlayerAccessor players();

    @NotNull
    FactionAccessor factions();
}
