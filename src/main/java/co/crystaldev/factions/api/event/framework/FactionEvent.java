package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/21/2024
 */
@Getter(onMethod = @__(@NotNull))
public abstract class FactionEvent extends BaseEvent {

    private final Faction faction;

    public FactionEvent(@NotNull Faction faction) {
        this.faction = faction;
    }

    public FactionEvent(@NotNull Faction faction, boolean async) {
        super(async);
        this.faction = faction;
    }
}
