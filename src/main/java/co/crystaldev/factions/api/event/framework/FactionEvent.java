package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.api.faction.Faction;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public abstract class FactionEvent extends BaseEvent {

    private final @NotNull Faction faction;

    public FactionEvent(@NotNull Faction faction) {
        this.faction = faction;
    }

    public FactionEvent(@NotNull Faction faction, boolean async) {
        super(async);
        this.faction = faction;
    }

    public @NotNull Faction getFaction() {
        return this.faction;
    }
}
