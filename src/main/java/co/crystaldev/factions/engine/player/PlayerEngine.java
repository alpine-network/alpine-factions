package co.crystaldev.factions.engine.player;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerHandler.getInstance().loggedIn(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        PlayerHandler.getInstance().loggedOut(event.getPlayer());
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();

        // player did not change chunks
        if (from.equals(to)) {
            return;
        }

        PlayerState state = PlayerHandler.getInstance().getPlayer(event.getPlayer());
        state.onMoveChunk(from, to);
    }
}
