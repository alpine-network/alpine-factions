package co.crystaldev.factions.api.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/19/2023
 */
@RequiredArgsConstructor @Getter
public final class FPlayer {

    private final UUID id;

    @Setter
    private double powerLevel;

    @Setter
    private long powerBoost;

    @Setter
    private int playtime;

    @Setter
    private TerritorialTitleMode territorialTitleMode = TerritorialTitleMode.TITLE;

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }

    @NotNull
    public TerritorialTitleMode getTerritorialTitleMode() {
        return Optional.ofNullable(this.territorialTitleMode).orElse(TerritorialTitleMode.TITLE);
    }
}
