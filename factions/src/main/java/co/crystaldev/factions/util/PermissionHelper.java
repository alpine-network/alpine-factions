package co.crystaldev.factions.util;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.config.MessageConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.3.0
 */
@UtilityClass
public final class PermissionHelper {

    // region Check

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Faction faction,
                                      @NotNull Permission permission) {
        return faction.isPermitted(player, permission);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Permission permission) {
        return isPermitted(player, Factions.registry().findOrDefault(player), permission);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                      @NotNull Permission permission, boolean bypassWilderness) {
        // Always disallow invalid locations
        World world = chunk.getWorld();
        if (world == null) {
            return false;
        }

        // Do not allow players to interact outside the border
        if (!LocationHelper.isChunkWithinBorder(world.getWorldBorder(), chunk.getX(), chunk.getZ())) {
            return false;
        }

        // Bypass checking wilderness claims if requested
        ClaimAccessor claims = Factions.claims();
        boolean isWilderness = !claims.isClaimed(chunk) || claims.getFactionOrDefault(chunk).isWilderness();
        if (bypassWilderness && isWilderness) {
            return true;
        }

        // Check the permission
        // Fallback to wilderness faction if chunk is not claimed
        Claim claim = claims.getClaim(chunk);
        Faction wilderness = Factions.registry().getWilderness();
        return claim == null ? wilderness.isPermitted(player, permission)
                : claim.isPermitted((OfflinePlayer) player, permission);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                      @NotNull Permission permission) {
        return isPermitted(player, chunk, permission, false);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Location location,
                                      @NotNull Permission permission, boolean bypassWilderness) {
        return isPermitted(player, location.getChunk(), permission, bypassWilderness);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Location location,
                                      @NotNull Permission permission) {
        return isPermitted(player, location.getChunk(), permission, false);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Block block,
                                      @NotNull Permission permission, boolean bypassWilderness) {
        return isPermitted(player, block.getChunk(), permission, bypassWilderness);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Block block,
                                      @NotNull Permission permission) {
        return isPermitted(player, block.getChunk(), permission, false);
    }

    // endregion Check

    // region Check + Notify

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Faction faction,
                                                   @NotNull Permission permission, @NotNull String message) {
        boolean permitted = isPermitted(player, faction, permission);
        if (!permitted) {
            notify(player, faction, message);
        }
        return permitted;
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Permission permission,
                                                   @NotNull String message) {
        return checkPermissionAndNotify(player, Factions.registry().findOrDefault(player), permission, message);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                                   @NotNull Permission permission, @NotNull String message,
                                                   boolean bypassWilderness) {
        boolean permitted = isPermitted(player, chunk, permission, bypassWilderness);
        if (!permitted) {
            ClaimAccessor claims = Factions.claims();
            notify(player, claims.getFactionOrDefault(chunk), message);
        }
        return permitted;
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                                   @NotNull Permission permission, @NotNull String message) {
        return checkPermissionAndNotify(player, chunk, permission, message, false);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Location location,
                                                   @NotNull Permission permission, @NotNull String message,
                                                   boolean bypassWilderness) {
        return checkPermissionAndNotify(player, location.getChunk(), permission, message, bypassWilderness);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Location location,
                                                   @NotNull Permission permission, @NotNull String message) {
        return checkPermissionAndNotify(player, location.getChunk(), permission, message, false);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Block block,
                                                   @NotNull Permission permission, @NotNull String message,
                                                   boolean bypassWilderness) {
        return checkPermissionAndNotify(player, block.getChunk(), permission, message, bypassWilderness);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Block block,
                                                   @NotNull Permission permission, @NotNull String message) {
        return checkPermissionAndNotify(player, block.getChunk(), permission, message, false);
    }

    // endregion Check + Notify

    public static void notify(@NotNull ServerOperator player, @NotNull Faction faction, @NotNull String message) {

        if (!(player instanceof Player) || !faction.getFlagValueOrDefault(FactionFlags.VERBOSE)) {
            return;
        }

        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        config.missingFactionPerm.rateLimitedSend((Player) player,
                "action", message,
                "faction", RelationHelper.formatFactionName(player, faction),
                "faction_name", faction.getName()
        );
    }
}
