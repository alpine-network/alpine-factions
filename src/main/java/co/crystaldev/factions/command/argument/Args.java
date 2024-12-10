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
     * Represents an warp {@link String} argument.
     * @see FactionWarpArgumentResolver
     */
    public static final String WARP = "factions:warp_argument_resolver";
}
