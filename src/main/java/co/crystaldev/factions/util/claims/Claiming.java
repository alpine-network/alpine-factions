package co.crystaldev.factions.util.claims;

import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
@UtilityClass
public final class Claiming {

    public static void mode(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction,
                            @NotNull ClaimType type, int radius) {
        Chunk origin = actor.getLocation().getChunk();
        Faction replacedFaction = Accessors.claims().getFaction(origin);

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, replacedFaction, claimingFaction, claimingFaction != null)) {
            return;
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks;
        switch (type) {
            case LINE: {
                chunks = ClaimHelper.line(origin, radius, LocationHelper.getFacing(actor.getLocation()));
                break;
            }
            case CIRCLE: {
                chunks = ClaimHelper.circle(origin, radius);
                break;
            }
            default: {
                chunks = ClaimHelper.square(origin, radius);
            }
        }

        // attempt the claim
        ClaimHelper.attemptClaim(actor, type.toString(), actingFaction, claimingFaction, chunks, origin);
    }

    public static void fill(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction) {
        MessageConfig config = MessageConfig.getInstance();
        Chunk origin = actor.getLocation().getChunk();
        Faction replacedFaction = Accessors.claims().getFaction(origin);

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, replacedFaction, claimingFaction, claimingFaction != null)) {
            return;
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks = ClaimHelper.fill(origin);
        if (chunks == null) {
            config.fillLimit.send(actor, "limit", FactionConfig.getInstance().maxClaimFillVolume);
            return;
        }

        // attempt to claim
        ClaimHelper.attemptClaim(actor, "fill", actingFaction, claimingFaction, chunks, origin);
    }

    public static void one(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction) {
        Chunk origin = actor.getLocation().getChunk();
        Faction replacedFaction = Accessors.claims().getFaction(origin);

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, replacedFaction, claimingFaction, claimingFaction != null)) {
            return;
        }

        // attempt to claim
        Set<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(new ChunkCoordinate(origin.getX(), origin.getZ())));
        ClaimHelper.attemptClaim(actor, "square", actingFaction, claimingFaction, chunks, origin);
    }
}
