package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@Getter
public final class NeutralFactionCombatEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final Faction attackingFaction;

    private final Player attacker;

    @Setter
    private boolean cancelled = true;

    public NeutralFactionCombatEvent(@NotNull Faction faction, @NotNull Player entity,
                                     @NotNull Faction attackingFaction, @NotNull Player attacker) {
        super(faction, entity);
        this.attackingFaction = attackingFaction;
        this.attacker = attacker;
    }
}
