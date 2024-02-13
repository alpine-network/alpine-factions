package co.crystaldev.factions.handler;

import co.crystaldev.factions.handler.player.PlayerState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
public final class PlayerHandler {

    @Getter
    private static PlayerHandler instance;
    { instance = this; }

    private final Map<UUID, PlayerState> playerStateMap = new HashMap<>();

    public void loggedIn(@NotNull Player player) {
        this.getPlayer(player).onLogin();
    }

    public void loggedOut(@NotNull Player player) {
        this.playerStateMap.remove(player.getUniqueId());
    }

    @NotNull
    public PlayerState getPlayer(@NotNull Player player) {
        return this.playerStateMap.computeIfAbsent(player.getUniqueId(), id -> new PlayerState(player));
    }

    public boolean isOverriding(@NotNull Player player) {
        return this.getPlayer(player).isOverriding();
    }
}
