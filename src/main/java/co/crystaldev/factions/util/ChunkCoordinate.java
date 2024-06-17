package co.crystaldev.factions.util;

import lombok.Data;

/**
 * @since 0.1.0
 */
@Data(staticConstructor = "of")
public final class ChunkCoordinate {
    private final int x;
    private final int z;
}
