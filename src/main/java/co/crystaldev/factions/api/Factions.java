package co.crystaldev.factions.api;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.show.ShowFormatter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public interface Factions {

    static @NotNull Factions get() {
        // TODO: do not expose in api
        return AlpineFactions.getInstance();
    }

    @NotNull ClaimAccessor claims();

    @NotNull PlayerAccessor players();

    @NotNull FactionAccessor factions();

    @NotNull FlagRegistry flagRegistry();

    @NotNull PermissionRegistry permissionRegistry();

    @NotNull ShowFormatter showFormatter();
}
