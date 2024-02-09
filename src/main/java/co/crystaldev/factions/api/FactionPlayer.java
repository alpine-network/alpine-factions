package co.crystaldev.factions.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequiredArgsConstructor @Getter
public final class FactionPlayer {

    private final UUID id;

    @Setter
    private long power;

    @Setter
    private long powerBoost;

    private boolean territorialTitles = true;

    public void toggleTerritorialTitles() {
        this.territorialTitles = !this.territorialTitles;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }
}
