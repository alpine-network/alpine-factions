package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter(onMethod = @__(@NotNull))
public abstract class FactionEntityEvent<T> extends FactionEvent {

    private final T entity;

    public FactionEntityEvent(@NotNull Faction faction, @NotNull T entity) {
        super(faction);
        this.entity = entity;
    }
}
