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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
        return isPermitted(player, Factions.get().factions().findOrDefault(player), permission);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                      @NotNull Permission permission, boolean allowWilderness) {
        // Always disallow invalid locations
        if (chunk.getWorld() == null) {
            return false;
        }

        // Bypass checking wilderness claims if requested
        ClaimAccessor claims = Factions.get().claims();
        boolean isWilderness = !claims.isClaimed(chunk) || claims.getFactionOrDefault(chunk).isWilderness();
        if (allowWilderness && isWilderness) {
            return true;
        }

        Claim claim = claims.getClaim(chunk);
        return claim == null || claim.isPermitted((OfflinePlayer) player, permission);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                      @NotNull Permission permission) {
        return isPermitted(player, chunk, permission, false);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Location location,
                                      @NotNull Permission permission, boolean allowWilderness) {
        return isPermitted(player, location.getChunk(), permission, allowWilderness);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Location location,
                                      @NotNull Permission permission) {
        return isPermitted(player, location.getChunk(), permission, false);
    }

    public static boolean isPermitted(@NotNull ServerOperator player, @NotNull Block block,
                                      @NotNull Permission permission, boolean allowWilderness) {
        return isPermitted(player, block.getChunk(), permission, allowWilderness);
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
        return checkPermissionAndNotify(player, Factions.get().factions().findOrDefault(player), permission, message);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Chunk chunk,
                                                   @NotNull Permission permission, @NotNull String message,
                                                   boolean allowWilderness) {
        boolean permitted = isPermitted(player, chunk, permission, allowWilderness);
        if (!permitted) {
            ClaimAccessor claims = Factions.get().claims();
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
                                                   boolean allowWilderness) {
        return checkPermissionAndNotify(player, location.getChunk(), permission, message, allowWilderness);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Location location,
                                                   @NotNull Permission permission, @NotNull String message) {
        return checkPermissionAndNotify(player, location.getChunk(), permission, message, false);
    }

    public static boolean checkPermissionAndNotify(@NotNull ServerOperator player, @NotNull Block block,
                                                   @NotNull Permission permission, @NotNull String message,
                                                   boolean allowWilderness) {
        return checkPermissionAndNotify(player, block.getChunk(), permission, message, allowWilderness);
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
