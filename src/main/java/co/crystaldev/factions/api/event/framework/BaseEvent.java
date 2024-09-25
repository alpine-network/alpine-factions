package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.AlpineFactions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Getter @NoArgsConstructor
public abstract class BaseEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull AlpineFactions plugin = AlpineFactions.getInstance();

    public BaseEvent(boolean async) {
        super(async);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
