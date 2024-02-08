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

    public static double distance(double x1, double z1, double x2, double z2) {
        x1 = Math.abs(x2 - x1);
        z1 = Math.abs(z2 - z1);
        return Math.sqrt((x1 * x1) + (z1 * z1));
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        x1 = Math.abs(x2 - x1);
        y1 = Math.abs(y2 - y1);
        z1 = Math.abs(z2 - z1);
        return Math.sqrt((x1 * x1) + (y1 * y1) + (z1 * z1));
    }
}
