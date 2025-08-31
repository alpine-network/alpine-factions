package co.crystaldev.factions.api.event.framework;

import co.crystaldev.alpinecore.framework.event.AlpineEvent;
import co.crystaldev.factions.AlpineFactions;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public abstract class BaseEvent extends AlpineEvent {

    private final @NotNull AlpineFactions plugin = AlpineFactions.getInstance();

    public BaseEvent() {
        // NO-OP
    }

    public BaseEvent(boolean async) {
        super(async);
    }

    public @NotNull AlpineFactions getPlugin() {
        return this.plugin;
    }
}
