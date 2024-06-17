package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class CreateFactionEvent extends FactionEntityEvent<Player> implements Cancellable {

    @Getter @Setter
    private boolean cancelled;

    public CreateFactionEvent(@NotNull Faction faction, @NotNull Player entity) {
        super(faction, entity);
    }
}
