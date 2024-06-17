package co.crystaldev.factions.command.argument;

import lombok.experimental.UtilityClass;

/**
 * @since 0.1.0
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
     * @see WorldClaimTypeArgumentResolver
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
     * Represents a {@link co.crystaldev.factions.api.faction.permission.Permission Permission} argument.
     * @see FactionPermissionArgumentResolver
     */
    public static final String FACTION_PERMISSION = "factions:faction_permission_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.api.Relational Relational} argument.
     * @see RelationalArgumentResolver
     */
    public static final String RELATIONAL = "factions:relational_argument_resolver";

    /**
     * Represents an {@link org.bukkit.OfflinePlayer OfflinePlayer} argument.
     * @see OfflinePlayerArgumentResolver
     */
    public static final String OFFLINE_PLAYER = "factions:offline_player_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.api.faction.FactionRelation FactionRelation} argument.
     * @see FactionRelationArgumentResolver
     */
    public static final String FACTION_RELATION = "factions:relation_argument_resolver";

    /**
     * Represents a {@link co.crystaldev.factions.api.faction.member.Rank Rank} argument.
     * @see FactionRankArgumentResolver
     */
    public static final String FACTION_RANK = "factions:rank_argument_resolver";
}
