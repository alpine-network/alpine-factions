package co.crystaldev.factions.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
@AllArgsConstructor @Getter
public final class ChunkCoordinate {
    private final int x;
    private final int z;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkCoordinate)) {
            return false;
        }
        ChunkCoordinate other = (ChunkCoordinate) obj;
        return other.x == this.x && other.z == this.z;
    }
}
