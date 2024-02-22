package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter
public final class FactionRelationUpdateEvent extends FactionEvent {

    private final @NotNull Faction targetFaction;

    @Setter
    private @NotNull FactionRelation relation;

    public FactionRelationUpdateEvent(@NotNull Faction faction, @NotNull Faction targetFaction, @NotNull FactionRelation relation) {
        super(faction);
        this.targetFaction = targetFaction;
        this.relation = relation;
    }
}
