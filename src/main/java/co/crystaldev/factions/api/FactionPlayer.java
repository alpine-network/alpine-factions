package co.crystaldev.factions.api;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/19/2023
 */
@Data
public final class FactionPlayer {
    private final UUID id;
    private long power;

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }
}
