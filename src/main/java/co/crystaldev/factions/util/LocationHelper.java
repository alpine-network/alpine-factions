package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
@UtilityClass
public final class LocationHelper {
    @NotNull
    public static BlockFace getFacing(@NotNull Location loc) {
        double rot = loc.getYaw() % 360;
        if (rot < 0) {
            rot += 360.0;
        }

        if (0 <= rot && rot < 45 || 315 <= rot && rot < 360.0) {
            return BlockFace.SOUTH;
        }
        else if (45 <= rot && rot < 135) {
            return BlockFace.WEST;
        }
        else if (135 <= rot && rot < 225) {
            return BlockFace.NORTH;
        }
        else {
            return BlockFace.EAST;
        }
    }
}
