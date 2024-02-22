package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter
public final class FactionNameUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    @Setter
    private @NotNull String name;

    @Setter
    private boolean cancelled;

    public FactionNameUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @NotNull String name) {
        super(faction, entity);
        this.name = name;
    }
}
