package co.crystaldev.factions.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class FactionsLoader {
    static final FactionsProvider INSTANCE;

    static {
        ServicesManager manager = Bukkit.getServicesManager();
        if (manager.isProvidedFor(FactionsProvider.class)) {
            INSTANCE = manager.load(FactionsProvider.class);
        }
        else {
            throw new IllegalArgumentException("No FactionsProvider implementation found");
        }
    }
}
