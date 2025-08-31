package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @since 0.4.0
 */
public final class FactionTerritoryFinalizedEvent extends FactionEntityEvent<Player> {

    private final @NotNull World world;

    private final @NotNull Set<ClaimedChunk> claims;

    public FactionTerritoryFinalizedEvent(@NotNull Faction faction, @NotNull Player entity, @NotNull World world,
                                          @NotNull Set<ClaimedChunk> claims, boolean async) {
        super(faction, entity, async);
        this.world = world;
        this.claims = claims;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public @NotNull Set<ClaimedChunk> getClaims() {
        return this.claims;
    }

    public boolean contains(@NotNull Location location) {
        if (!this.world.equals(location.getWorld())) {
            return false;
        }

        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;

        for (ClaimedChunk claim : this.claims) {
            if (claim.getX() == cx && claim.getZ() == cz) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(@NotNull Chunk chunk) {
        if (!this.world.equals(chunk.getWorld())) {
            return false;
        }

        for (ClaimedChunk claim : this.claims) {
            if (claim.getX() == chunk.getX() && claim.getZ() == chunk.getZ()) {
                return true;
            }
        }

        return false;
    }
}
