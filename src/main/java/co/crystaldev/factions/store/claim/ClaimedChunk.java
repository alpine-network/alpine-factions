package co.crystaldev.factions.store.claim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Chunk;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@AllArgsConstructor @Getter
public final class ClaimedChunk {
    private final Chunk chunk;
    private final Claim claim;
}
