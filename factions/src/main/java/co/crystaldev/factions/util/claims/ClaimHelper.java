package co.crystaldev.factions.util.claims;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.FactionTerritoryFinalizedEvent;
import co.crystaldev.factions.api.event.FactionTerritoryUpdateEvent;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.*;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @since 0.1.0
 */
@UtilityClass @ApiStatus.Internal
public final class ClaimHelper {

    public static Collection<ClaimedChunk> attemptClaim(@NotNull Player player, @NotNull String action,
                                    @NotNull Faction actingFaction, @Nullable Faction claimingFaction,
                                    @NotNull Set<ChunkCoordinate> chunks, @NotNull Chunk origin,
                                    boolean higherLimit) {
        MessageConfig messageConfig = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        FactionConfig factionConfig = AlpineFactions.getInstance().getConfiguration(FactionConfig.class);
        FactionAccessor factions = Factions.registry();

        boolean overriding = PlayerHandler.getInstance().isOverriding(player);
        Chunk playerChunk = player.getLocation().getChunk();
        int cx = playerChunk.getX();
        int cz = playerChunk.getZ();
        int maxClaimDistance = (higherLimit ? 2 : 1) * factionConfig.maxClaimDistance;
        World world = origin.getWorld();
        WorldBorder border = world.getWorldBorder();

        // ensure there are chunks to claim
        if (chunks.isEmpty()) {
            return Collections.emptySet();
        }

        // ensure wilderness is always null
        if (claimingFaction != null && claimingFaction.isWilderness()) {
            claimingFaction = null;
        }

        // ensure the claiming faction has enough power to claim this land
        if (!overriding && claimingFaction != null && claimingFaction.getRemainingClaims() < chunks.size()) {
            messageConfig.insufficientPower.send(player,
                    "faction", RelationHelper.formatFactionName(player, claimingFaction),
                    "faction_name", claimingFaction.getName());
            return Collections.emptySet();
        }

        // ensure that the claim limit hasn't been reached
        if (!overriding && chunks.size() > factionConfig.maxClaimFillVolume) {
            messageConfig.fillLimit.send(player, "limit", factionConfig.maxClaimFillVolume);
            return Collections.emptySet();
        }

        // iterate and validate chunks
        Faction playerFaction = factions.findOrDefault(player);
        ClaimAccessor claims = Factions.claims();
        int conqueredChunkCount = 0;
        Map<Faction, List<ChunkCoordinate>> conqueredFactions = new HashMap<>();
        Iterator<ChunkCoordinate> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            ChunkCoordinate next = iterator.next();

            if (!overriding && LocationHelper.distance(next.getX(), next.getZ(), cx, cz) > maxClaimDistance) {
                messageConfig.claimTooFar.send(player);
                return Collections.emptySet();
            }

            if (!LocationHelper.isChunkWithinBorder(border, next.getX(), next.getZ())) {
                iterator.remove();
                continue;
            }

            Faction faction = claims.getFaction(world.getName(), next.getX(), next.getZ());
            if (faction != null) {
                // this chunk is claimed

                if (faction.equals(claimingFaction)) {
                    // our faction already owns this chunk

                    iterator.remove();
                }
                else if (!overriding && !faction.isConquerable() && !faction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
                    // owning faction is strong enough to keep this claim

                    messageConfig.conquerFail.send(player,
                            "faction", RelationHelper.formatFactionName(player, faction),
                            "faction_name", faction.getName());
                    return Collections.emptySet();
                }
                else if (!faction.equals(playerFaction)) {
                    // conquer the chunk
                    conqueredFactions.computeIfAbsent(faction, f -> new ArrayList<>()).add(next);
                    conqueredChunkCount++;
                }
            }
            else if (claimingFaction == null) {
                // cannot unclaim a chunk that is not currently claimed
                iterator.remove();
            }
        }

        // ensure there are still chunks to claim
        if (chunks.isEmpty()) {
            if (claimingFaction == null) {
                claimingFaction = factions.getWilderness();
            }

            messageConfig.landOwned.send(player,
                    "faction", RelationHelper.formatFactionName(player, claimingFaction),
                    "faction_name", claimingFaction.getName());
            return Collections.emptySet();
        }

        // ensure that the claims can be conquered
        if (conqueredChunkCount > 0 && !canConquer(conqueredFactions, world.getName(), claims)) {
            messageConfig.conquerFromEdge.send(player);
            return Collections.emptySet();
        }

        // call event
        FactionTerritoryUpdateEvent.Type type = claimingFaction == null
                ? FactionTerritoryUpdateEvent.Type.UNCLAIM : FactionTerritoryUpdateEvent.Type.CLAIM;
        FactionTerritoryUpdateEvent event = AlpineFactions.callEvent(new FactionTerritoryUpdateEvent(
                actingFaction, player, world, type, chunks, conqueredFactions, !Bukkit.isPrimaryThread()));
        if (event.isCancelled() || chunks.isEmpty()) {
            return Collections.emptySet();
        }

        // claim/unclaim the land
        Set<ClaimedChunk> claimedChunks = new HashSet<>();
        for (ChunkCoordinate chunk : chunks) {
            if (claimingFaction == null) {
                claims.remove(world.getName(), chunk.getX(), chunk.getZ());
            }
            else {
                Claim newClaim = claims.put(world.getName(), chunk.getX(), chunk.getZ(), claimingFaction);
                if (newClaim != null) {
                    claimedChunks.add(new ClaimedChunk(newClaim, world.getName(), chunk.getX(), chunk.getZ()));
                }
            }
        }

        // apply attributes to claimed chunks
        if (!claimedChunks.isEmpty()) {
            AlpineFactions.callEvent(new FactionTerritoryFinalizedEvent(
                    actingFaction, player, world, claimedChunks, !Bukkit.isPrimaryThread()));
        }

        // notify the claiming faction
        Faction wilderness = factions.getWilderness();
        int chunkCount = chunks.size() - conqueredChunkCount;
        if (chunkCount > 0) {
            Faction oldFaction = claimingFaction == null ? actingFaction : wilderness;
            Faction newFaction = claimingFaction == null ? wilderness : actingFaction;

            Component claimAction = (claimingFaction == null ? messageConfig.unclaimed : messageConfig.claimed).build();
            FactionHelper.broadcast(actingFaction, player, observer -> {
                ConfigText message = chunkCount == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;
                return buildClaimMessage(observer, player, message, origin, action, claimAction,
                        chunkCount, oldFaction, newFaction);
            });
        }

        // notify the conquered/pillaged factions
        if (conqueredChunkCount > 0) {
            Component claimAction = (claimingFaction == null ? messageConfig.pillaged : messageConfig.conquered).build();
            Faction newFaction = claimingFaction == null ? wilderness : claimingFaction;

            conqueredFactions.forEach((faction, conquered) -> {
                ConfigText message = conquered.size() == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;

                Chunk chunk = world.getChunkAt(conquered.get(0).getX(), conquered.get(0).getZ());

                // notify the conquered faction
                FactionHelper.broadcast(faction, observer -> {
                    return buildClaimMessage(observer, player, message, chunk, action, claimAction,
                            conquered.size(), faction, newFaction);
                });

                // notify the new faction
                FactionHelper.broadcast(actingFaction, player, observer -> {
                    return buildClaimMessage(observer, player, message, chunk, action, claimAction,
                            conquered.size(), faction, newFaction);
                });
            });
        }

        return Collections.unmodifiableSet(claimedChunks);
    }

    public static boolean shouldCancelClaim(@NotNull Player player, @Nullable Faction claimingFaction, boolean claiming) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);

        if (claiming) {
            if (claimingFaction == null) {
                Faction wilderness = Factions.registry().getWilderness();
                config.landOwned.send(player,
                        "faction", RelationHelper.formatFactionName(player, wilderness),
                        "faction_name", wilderness.getName()
                );
                return true;
            }
            else {
                return !PermissionHelper.checkPermissionAndNotify(player, claimingFaction,
                        Permissions.MODIFY_TERRITORY, "modify territory");
            }
        }

        return false;
    }

    public static @NotNull Set<ChunkCoordinate> square(@NotNull Chunk origin, int radius, int maxRadius) {
        radius = Math.min(maxRadius < 0 ? 256 : maxRadius, Math.abs(radius) - 1);

        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(ChunkCoordinate.of(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    public static @NotNull Set<ChunkCoordinate> line(@NotNull Chunk origin, int length, @NotNull BlockFace facing) {
        length = Math.min(30, Math.abs(length));

        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int i = 0; i < length; i++) {
            chunks.add(ChunkCoordinate.of(chunkX, chunkZ));
            chunkX += facing.getModX();
            chunkZ += facing.getModZ();
        }

        return chunks;
    }

    public static @NotNull Set<ChunkCoordinate> circle(@NotNull Chunk origin, int radius) {
        radius = Math.min(30, Math.abs(radius) - 1);

        int chunkX = origin.getX();
        int chunkZ = origin.getZ();
        int radiusSquared = radius * radius;

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radiusSquared)
                    continue;

                chunks.add(ChunkCoordinate.of(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    public static @Nullable Set<ChunkCoordinate> fill(@NotNull Chunk origin) {
        int max = AlpineFactions.getInstance().getConfiguration(FactionConfig.class).maxClaimFillVolume;
        World world = origin.getWorld();
        ClaimAccessor claims = Factions.claims();
        Set<ChunkCoordinate> chunks = new HashSet<>();

        // discover chunks to fill
        chunks.add(ChunkCoordinate.of(origin.getX(), origin.getZ()));
        recurse(chunks, world.getName(), claims.getFaction(origin), claims, max);

        // limit was reached, disregard
        if (chunks.size() >= max) {
            return null;
        }

        // convert to chunks
        return chunks;
    }

    private static void recurse(@NotNull Set<ChunkCoordinate> chunks, @NotNull String world,
                                @Nullable Faction faction, @NotNull ClaimAccessor claims, int max) {
        Set<ChunkCoordinate> nearby = new HashSet<>();
        for (ChunkCoordinate chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();

            // check surrounding chunks
            checkAndAddClaim(nearby, chunks, world, x + 1, z, faction, claims);
            checkAndAddClaim(nearby, chunks, world, x - 1, z, faction, claims);
            checkAndAddClaim(nearby, chunks, world, x, z + 1, faction, claims);
            checkAndAddClaim(nearby, chunks, world, x, z - 1, faction, claims);
        }

        // no nearby chunks were found, fill complete
        if (nearby.isEmpty()) {
            return;
        }

        // add the new chunks
        chunks.addAll(nearby);

        // keep searching
        if (nearby.size() < max) {
            recurse(chunks, world, faction, claims, max);
        }
    }

    private static void checkAndAddClaim(@NotNull Set<ChunkCoordinate> nearby, @NotNull Set<ChunkCoordinate> filled,
                                         @NotNull String world, int x, int z, @Nullable Faction faction, @NotNull ClaimAccessor claims) {
        ChunkCoordinate coord = ChunkCoordinate.of(x, z);
        if (filled.contains(coord)) {
            return;
        }

        // ensure claim is the same as the origin claim
        Faction claimOwner = claims.getFaction(world, x, z);
        if (!Objects.equals(faction, claimOwner)) {
            return;
        }

        // able to claim
        nearby.add(coord);
    }

    private static boolean canConquer(@NotNull Map<Faction, List<ChunkCoordinate>> conqueredFactions,
                                      @NotNull String world, @NotNull ClaimAccessor claims) {

        boolean canConquer = true;

        for (Map.Entry<Faction, List<ChunkCoordinate>> entry : conqueredFactions.entrySet()) {
            Faction faction = entry.getKey();
            boolean foundEdge = false;
            for (ChunkCoordinate coordinate : entry.getValue()) {
                boolean isEdge = !Objects.equals(faction, claims.getFaction(world, coordinate.getX() - 1, coordinate.getZ()))
                        || !Objects.equals(faction, claims.getFaction(world, coordinate.getX() + 1, coordinate.getZ()))
                        || !Objects.equals(faction, claims.getFaction(world, coordinate.getX(), coordinate.getZ() - 1))
                        || !Objects.equals(faction, claims.getFaction(world, coordinate.getX(), coordinate.getZ() + 1));
                if (isEdge) {
                    foundEdge = true;
                }
            }

            if (!foundEdge) {
                canConquer = false;
            }
        }

        return canConquer;
    }

    private static @NotNull Component buildClaimMessage(@NotNull Player recipient, @NotNull Player actor, @NotNull ConfigText message,
                                               @NotNull Chunk origin, @NotNull String action, @NotNull Component claimType,
                                               int amount, @NotNull Faction oldFaction, @NotNull Faction newFaction) {
        return message.build(
                "world", origin.getWorld().getName(),
                "amount", amount,
                "chunk_x", origin.getX(),
                "chunk_z", origin.getZ(),
                "type", action,

                "claim_type", claimType,

                "actor", RelationHelper.formatPlayerName(recipient, actor),
                "actor_name", actor.getName(),

                "old_faction", RelationHelper.formatFactionName(recipient, oldFaction),
                "old_faction_name", oldFaction.getName(),
                "new_faction", RelationHelper.formatFactionName(recipient, newFaction),
                "new_faction_name", newFaction.getName()
        );
    }
}
