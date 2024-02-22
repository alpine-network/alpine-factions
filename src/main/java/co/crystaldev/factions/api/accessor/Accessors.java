package co.crystaldev.factions.api.accessor;

import co.crystaldev.factions.AlpineFactions;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/21/2024
 */
@UtilityClass
public final class Accessors {
    @NotNull
    public static ClaimAccessor claims() {
        return AlpineFactions.getInstance().getClaimAccessor();
    }

    @NotNull
    public static FactionAccessor factions() {
        return AlpineFactions.getInstance().getFactionAccessor();
    }

    @NotNull
    public static PlayerAccessor players() {
        return AlpineFactions.getInstance().getPlayerAccessor();
    }
}
