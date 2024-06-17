package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Getter @Setter
public final class FactionHomeUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private Location location;

    private boolean cancelled;

    public FactionHomeUpdateEvent(@NotNull Faction faction, @NotNull Player entity, @Nullable Location location) {
        super(faction, entity);
        this.location = location;
    }

    public boolean wasUnset() {
        return this.location == null;
    }
}
