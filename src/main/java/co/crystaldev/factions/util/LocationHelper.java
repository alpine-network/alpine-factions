package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class LocationHelper {
    @NotNull
    public static BlockFace getFacing(@NotNull Location location) {
        double yaw = location.getYaw() % 360;
        if (yaw < 0) {
            yaw += 360.0;
        }

        if (0 <= yaw && yaw < 45 || 315 <= yaw && yaw < 360.0) return BlockFace.SOUTH;
        else if (45 <= yaw && yaw < 135) return BlockFace.WEST;
        else if (135 <= yaw && yaw < 225) return BlockFace.NORTH;
        else return BlockFace.EAST;
    }

    @NotNull
    public static BlockFace getFullFacing(@NotNull Location location) {
        double yaw = (location.getYaw() % 360 + 360) % 360;

        // fun
        if (yaw >= 0 && yaw < 22.5 || yaw >= 337.5 && yaw < 360) return BlockFace.SOUTH;
        else if (yaw >= 22.5 && yaw < 67.5) return BlockFace.SOUTH_WEST;
        else if (yaw >= 67.5 && yaw < 112.5) return BlockFace.WEST;
        else if (yaw >= 112.5 && yaw < 157.5) return BlockFace.NORTH_WEST;
        else if (yaw >= 157.5 && yaw < 202.5) return BlockFace.NORTH;
        else if (yaw >= 202.5 && yaw < 247.5) return BlockFace.NORTH_EAST;
        else if (yaw >= 247.5 && yaw < 292.5) return BlockFace.EAST;
        else if (yaw >= 292.5 && yaw < 337.5) return BlockFace.SOUTH_EAST;

        return BlockFace.NORTH;
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
