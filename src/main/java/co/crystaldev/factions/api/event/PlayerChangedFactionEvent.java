package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.BaseEvent;
import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class PlayerChangedFactionEvent extends BaseEvent implements Cancellable {

    private final @NotNull Faction newFaction;

    private final @NotNull Faction oldFaction;

    private final @NotNull OfflinePlayer offlinePlayer;

    private boolean cancelled;

    public PlayerChangedFactionEvent(@NotNull Faction newFaction, @NotNull Faction oldFaction, @NotNull OfflinePlayer player) {
        this.newFaction = newFaction;
        this.oldFaction = oldFaction;
        this.offlinePlayer = player;
    }

    public @Nullable Player getPlayer() {
        return this.offlinePlayer.getPlayer();
    }

    public @NotNull Faction getNewFaction() {
        return this.newFaction;
    }

    public @NotNull Faction getOldFaction() {
        return this.oldFaction;
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
