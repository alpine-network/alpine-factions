package co.crystaldev.factions.command.argument;

import lombok.experimental.UtilityClass;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/17/2024
 */
@UtilityClass
public final class Args {

    /**
     * Represents an alphanumeric {@link String} argument.
     * @see AlphanumericArgumentResolver
     */
    public static final String ALPHANUMERIC = "factions:alphanumeric_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.util.claims.ClaimType ClaimType} argument.
     * @see ClaimTypeArgumentResolver
     */
    public static final String CLAIM_TYPE = "factions:claim_type_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.util.claims.WorldClaimType WorldClaimType} argument.
     * @see WorldClaimArgumentResolver
     */
    public static final String WORLD_CLAIM_TYPE = "factions:world_claim_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.api.faction.Faction Faction} argument.
     * @see FactionFlagArgumentResolver
     */
    public static final String FACTION = "factions:faction_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.api.faction.flag.FactionFlag FactionFlag} argument.
     * @see FactionFlagArgumentResolver
     */
    public static final String FACTION_FLAG = "factions:faction_flag_argument_resolver";

    /**
     * Represents an {@link org.bukkit.OfflinePlayer OfflinePlayer} argument.
     * @see OfflinePlayerArgumentResolver
     */
    public static final String OFFLINE_PLAYER = "factions:offline_player_argument_resolver";
}
