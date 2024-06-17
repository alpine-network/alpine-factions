package co.crystaldev.factions.api;

import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import org.jetbrains.annotations.NotNull;

/**
 *
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
