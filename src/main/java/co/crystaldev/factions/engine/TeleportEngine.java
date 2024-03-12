package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.handler.TeleportManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/12/2024
 */
public final class TeleportEngine extends AlpineEngine {
    TeleportEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        if (!event.isTime(1L, TimeUnit.SECONDS)) {
            return;
        }

        TeleportManager.getInstance().execute();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        TeleportManager.getInstance().onPlayerMove(event.getPlayer(), event.getTo().distance(event.getFrom()));
    }
}
