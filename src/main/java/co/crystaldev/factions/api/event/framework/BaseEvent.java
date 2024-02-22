package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/21/2024
 */
@Getter(onMethod = @__(@NotNull))
public abstract class BaseEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final AlpineFactions plugin = AlpineFactions.getInstance();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
