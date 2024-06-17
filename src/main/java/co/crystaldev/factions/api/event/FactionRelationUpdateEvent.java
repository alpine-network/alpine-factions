package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Getter @Setter
public final class FactionRelationUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Faction targetFaction;

    private @NotNull FactionRelation relation;

    private boolean cancelled;

    public FactionRelationUpdateEvent(@NotNull Faction faction, @NotNull Faction targetFaction,
                                      @NotNull CommandSender entity, @NotNull FactionRelation relation) {
        super(faction, entity);
        this.targetFaction = targetFaction;
        this.relation = relation;
    }
}
