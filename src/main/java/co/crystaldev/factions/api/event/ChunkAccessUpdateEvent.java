package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter @Setter
public final class ChunkAccessUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Collection<ClaimedChunk> claims;

    private Faction accessedFaction;

    private OfflinePlayer accessedPlayer;

    private boolean accessed;

    private boolean cancelled;

    public ChunkAccessUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @NotNull Collection<ClaimedChunk> claims,
                                  @NotNull Object subject) {
        super(faction, entity);
        this.claims = claims;

        if (subject instanceof Faction) {
            this.accessedFaction = (Faction) subject;
        }
        else if (subject instanceof OfflinePlayer) {
            this.accessedPlayer = (OfflinePlayer) subject;
        }
        else {
            throw new IllegalArgumentException("subject must be instance of Faction or OfflinePlayer");
        }
    }

    public boolean isFaction() {
        return this.accessedFaction != null;
    }

    public boolean isPlayer() {
        return this.accessedPlayer != null;
    }
}
