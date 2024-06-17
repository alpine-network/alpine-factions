package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.BaseEvent;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Getter
public final class PlayerChangedFactionEvent extends BaseEvent implements Cancellable {

    private final @NotNull Faction newFaction;

    private final @NotNull Faction oldFaction;

    private final @NotNull OfflinePlayer offlinePlayer;

    @Setter
    private boolean cancelled;

    public PlayerChangedFactionEvent(@NotNull Faction newFaction, @NotNull Faction oldFaction, @NotNull OfflinePlayer player) {
        this.newFaction = newFaction;
        this.oldFaction = oldFaction;
        this.offlinePlayer = player;
    }

    @Nullable
    public Player getPlayer() {
        return this.offlinePlayer.getPlayer();
    }
}
