package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.Warp;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Getter
public final class FactionWarpUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final Warp oldWarp;

    private final Warp newWarp;

    private final boolean unset;

    @Setter
    private boolean cancelled;

    public FactionWarpUpdateEvent(@NotNull Faction faction, @NotNull Player entity,
                                  @Nullable Warp warp, @Nullable Warp newWarp,
                                  boolean unset) {
        super(faction, entity);
        this.oldWarp = warp;
        this.newWarp = newWarp;
        this.unset = unset;
    }
}
