package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class PlayerEngine extends AlpineEngine {
    PlayerEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
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
