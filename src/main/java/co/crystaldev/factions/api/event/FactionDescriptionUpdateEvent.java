package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.util.ComponentHelper;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Getter @Setter
public final class FactionDescriptionUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private @Nullable Component description;

    private boolean cancelled;

    public FactionDescriptionUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @Nullable Component description) {
        super(faction, entity);
        this.description = description;
    }

    public @Nullable String getPlainDescription() {
        return this.description == null ? null : ComponentHelper.plain(this.description);
    }
}
