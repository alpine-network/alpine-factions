package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @since 0.1.0
 */
public final class ChunkAccessUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Collection<ClaimedChunk> claims;

    private @Nullable Faction accessedFaction;

    private @Nullable OfflinePlayer accessedPlayer;

    private boolean accessed;

    private boolean cancelled;

    public ChunkAccessUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @NotNull Collection<ClaimedChunk> claims,
                                  @NotNull Object subject) {
        super(faction, entity);
        this.claims = claims;

        if (subject instanceof Faction) {
            this.accessedFaction = (Faction) subject;
        } else if (subject instanceof OfflinePlayer) {
            this.accessedPlayer = (OfflinePlayer) subject;
        } else {
            throw new IllegalArgumentException("subject must be instance of Faction or OfflinePlayer");
        }
    }

    public @NotNull Collection<ClaimedChunk> getClaims() {
        return this.claims;
    }

    public @Nullable Faction getAccessedFaction() {
        return this.accessedFaction;
    }

    public @Nullable OfflinePlayer getAccessedPlayer() {
        return this.accessedPlayer;
    }

    public boolean isFaction() {
        return this.accessedFaction != null;
    }

    public boolean isPlayer() {
        return this.accessedPlayer != null;
    }

    public boolean isAccessed() {
        return this.accessed;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
