package co.crystaldev.factions.api.event.framework;

import co.crystaldev.alpinecore.framework.event.AlpineEvent;
import co.crystaldev.factions.AlpineFactions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Getter @NoArgsConstructor
public abstract class BaseEvent extends AlpineEvent {

    private final @NotNull AlpineFactions plugin = AlpineFactions.getInstance();

    public BaseEvent(boolean async) {
        super(async);
    }
}
