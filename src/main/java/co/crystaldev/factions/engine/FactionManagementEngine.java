package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.event.ServerTickEvent;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import org.bukkit.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
public final class FactionManagementEngine extends AlpineEngine {
    FactionManagementEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    private void onServerTick(ServerTickEvent event) {
        if (!event.isTime(30L, TimeUnit.SECONDS))
            return;

        FactionStore.getInstance().saveFactions();
        ClaimStore.getInstance().saveClaims();
    }
}
