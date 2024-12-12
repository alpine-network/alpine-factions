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
public final class FactionWarpUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private String warpName;

    private String password;

    private Location location;

    private boolean cancelled;

    public FactionWarpUpdateEvent(@NotNull Faction faction, @NotNull Player entity, @NotNull String warpName, @Nullable String password,@Nullable Location location) {
        super(faction, entity);
        this.warpName = warpName;
        this.location = location;
        this.password = password;
    }

    public boolean wasUnset() {
        return this.location == null;
    }
}
