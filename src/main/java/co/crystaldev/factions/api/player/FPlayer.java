package co.crystaldev.factions.api.player;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.config.FactionConfig;
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
 * @since 0.1.0
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
    private boolean friendlyFire;

    @Setter
    private TerritorialTitleMode territorialTitleMode = TerritorialTitleMode.TITLE;

    public long getEffectivePower() {
        return (long) (this.powerLevel + this.powerBoost);
    }

    public long getMaxPower() {
        return AlpineFactions.getInstance().getConfiguration(FactionConfig.class).maxPlayerPower + this.powerBoost;
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }

    public @NotNull TerritorialTitleMode getTerritorialTitleMode() {
        return Optional.ofNullable(this.territorialTitleMode).orElse(TerritorialTitleMode.TITLE);
    }
}
