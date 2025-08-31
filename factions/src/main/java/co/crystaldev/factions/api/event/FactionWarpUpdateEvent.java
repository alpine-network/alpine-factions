package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class FactionWarpUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final @Nullable Warp oldWarp;

    private final @Nullable Warp newWarp;

    private boolean cancelled;

    public FactionWarpUpdateEvent(@NotNull Faction faction, @NotNull Player entity,
                                  @Nullable Warp warp, @Nullable Warp newWarp) {
        super(faction, entity);
        this.oldWarp = warp;
        this.newWarp = newWarp;
    }

    public @Nullable Warp getOldWarp() {
        return this.oldWarp;
    }

    public @Nullable Warp getNewWarp() {
        return this.newWarp;
    }

    public boolean wasUnset() {
        return this.newWarp == null;
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
