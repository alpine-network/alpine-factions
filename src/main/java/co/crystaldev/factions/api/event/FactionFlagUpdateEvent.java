package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter @Setter
public final class FactionFlagUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull FactionFlag<?> flag;

    private @NotNull Object value;

    private boolean cancelled;

    public FactionFlagUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @NotNull FactionFlag<?> flag, @NotNull Object value) {
        super(faction, entity);
        this.flag = flag;
        this.value = value;
    }
}
