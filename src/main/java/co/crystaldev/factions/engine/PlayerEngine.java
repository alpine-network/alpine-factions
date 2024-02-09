package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.event.ServerTickEvent;
import co.crystaldev.factions.handler.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/18/2023
 */
public final class PlayerEngine extends AlpineEngine {
    PlayerEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        PlayerHandler.getInstance().tick();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        PlayerHandler.getInstance().loggedOut(event.getPlayer());
    }
}
