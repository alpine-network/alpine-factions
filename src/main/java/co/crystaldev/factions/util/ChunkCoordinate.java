package co.crystaldev.factions.util;

import lombok.Data;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
@Data(staticConstructor = "of")
public final class ChunkCoordinate {
    private final int x;
    private final int z;
}
