package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.util.ChunkCoordinate;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/22/2024
 */
@Getter
public final class FactionTerritoryChangeEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final @NotNull World world;

    private final @NotNull Type type;

    private final @NotNull Set<ChunkCoordinate> chunks;

    private final @NotNull Map<Faction, List<ChunkCoordinate>> conqueredChunks;

    @Setter
    private boolean cancelled;

    public FactionTerritoryChangeEvent(@NotNull Faction faction, @NotNull Player entity, @NotNull World world,
                                       @NotNull Type type, @NotNull Set<ChunkCoordinate> chunks,
                                       @NotNull Map<Faction, List<ChunkCoordinate>> conqueredChunks) {
        super(faction, entity);
        this.world = world;
        this.type = type;
        this.chunks = chunks;
        this.conqueredChunks = conqueredChunks;
    }

    public boolean contains(@NotNull Location location) {
        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;

        if (!this.world.equals(location.getWorld())) {
            return false;
        }

        for (ChunkCoordinate chunk : this.chunks) {
            if (chunk.getX() == cx && chunk.getZ() == cz) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(@NotNull Chunk chunk) {
        if (!this.world.equals(chunk.getWorld())) {
            return false;
        }

        for (ChunkCoordinate coordinate : this.chunks) {
            if (coordinate.getX() == chunk.getX() && coordinate.getZ() == chunk.getZ()) {
                return true;
            }
        }

        return false;
    }

    public enum Type {
        CLAIM,
        UNCLAIM
    }
}
