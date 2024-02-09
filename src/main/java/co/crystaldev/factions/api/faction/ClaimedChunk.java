package co.crystaldev.factions.api.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@AllArgsConstructor @Getter
public final class ClaimedChunk {
    private final Claim claim;

    private final String world;
    private final int chunkX;
    private final int chunkZ;

    @NotNull
    public Chunk getChunk() {
        return Bukkit.getWorld(this.world).getChunkAt(this.chunkX, this.chunkZ);
    }
}
