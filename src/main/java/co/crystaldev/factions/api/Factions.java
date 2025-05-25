package co.crystaldev.factions.api;

import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.map.FactionMapFormatter;
import co.crystaldev.factions.api.registry.FlagRegistry;
import co.crystaldev.factions.api.registry.PermissionRegistry;
import co.crystaldev.factions.api.show.ShowFormatter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.5.0
 */
public interface Factions {

    static @NotNull FactionAccessor registry() {
        return FactionsLoader.INSTANCE.registry();
    }

    static @NotNull PlayerAccessor players() {
        return FactionsLoader.INSTANCE.players();
    }

    static @NotNull ClaimAccessor claims() {
        return FactionsLoader.INSTANCE.claims();
    }

    static @NotNull FlagRegistry flags() {
        return FactionsLoader.INSTANCE.flags();
    }

    static @NotNull PermissionRegistry permissions() {
        return FactionsLoader.INSTANCE.permissions();
    }

    static @NotNull ShowFormatter showFormatter() {
        return FactionsLoader.INSTANCE.showFormatter();
    }

    static @NotNull FactionMapFormatter mapFormatter() {
        return FactionsLoader.INSTANCE.mapFormatter();
    }
}
