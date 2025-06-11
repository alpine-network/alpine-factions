package co.crystaldev.factions.util.claims;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ChunkCoordinate;
import co.crystaldev.factions.util.LocationHelper;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class Claiming {

    public static Collection<ClaimedChunk> mode(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction,
                                                @NotNull ClaimType type, int radius) {
        Chunk origin = actor.getLocation().getChunk();

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, claimingFaction, claimingFaction != null)) {
            return Collections.emptySet();
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
                int maxRadius = PlayerHandler.getInstance().isOverriding(actor) ? -1 : 30;
                chunks = ClaimHelper.square(origin, radius, maxRadius);
            }
        }

        // attempt the claim
        return ClaimHelper.attemptClaim(actor, type.toString(), actingFaction, claimingFaction, chunks, origin, false);
    }

    public static Collection<ClaimedChunk> fill(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        Chunk origin = actor.getLocation().getChunk();
        Faction replacedFaction = Factions.claims().getFaction(origin);

        // do not unclaim fill if faction is wilderness
        boolean claiming = claimingFaction != null;
        if (!claiming && replacedFaction == null) {
            config.fillLimit.send(actor, "limit", AlpineFactions.getInstance().getConfiguration(FactionConfig.class).maxClaimFillVolume);
            return Collections.emptySet();
        }

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, claimingFaction, claiming)) {
            return Collections.emptySet();
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks = ClaimHelper.fill(origin);
        if (chunks == null) {
            config.fillLimit.send(actor, "limit", AlpineFactions.getInstance().getConfiguration(FactionConfig.class).maxClaimFillVolume);
            return Collections.emptySet();
        }

        // attempt to claim
        return ClaimHelper.attemptClaim(actor, "fill", actingFaction, claimingFaction, chunks, origin, true);
    }

    public static Collection<ClaimedChunk> one(@NotNull Player actor, @NotNull Faction actingFaction, @Nullable Faction claimingFaction) {
        Chunk origin = actor.getLocation().getChunk();

        // is the player able to claim this land?
        if (ClaimHelper.shouldCancelClaim(actor, claimingFaction, claimingFaction != null)) {
            return Collections.emptySet();
        }

        // attempt to claim
        Set<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(ChunkCoordinate.of(origin.getX(), origin.getZ())));
        return ClaimHelper.attemptClaim(actor, "square", actingFaction, claimingFaction, chunks, origin, false);
    }
}
