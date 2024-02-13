package co.crystaldev.factions.util.claims;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.ChunkCoordinate;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.LocationHelper;
import co.crystaldev.factions.util.Messaging;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/11/2024
 */
@UtilityClass @ApiStatus.Internal
public final class ClaimHelper {
    public static void attemptClaim(@NotNull Player player, @NotNull String action,
                                    @NotNull Faction actingFaction, @Nullable Faction claimingFaction,
                                    @NotNull Set<ChunkCoordinate> chunks, @NotNull Chunk origin) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionStore factionStore = FactionStore.getInstance();

        boolean overriding = PlayerHandler.getInstance().isOverriding(player);
        Chunk playerChunk = player.getLocation().getChunk();
        int cx = playerChunk.getX();
        int cz = playerChunk.getZ();

        // ensure there are chunks to claim
        if (chunks.isEmpty()) {
            return;
        }

        // ensure wilderness is always null
        if (claimingFaction != null && claimingFaction.isWilderness()) {
            claimingFaction = null;
        }

        // ensure the claiming faction has enough power to claim this land
        if (!overriding && claimingFaction != null && claimingFaction.getRemainingPower() < chunks.size()) {
            messageConfig.insufficientPower.send(player,
                    "faction", FactionHelper.formatRelational(player, claimingFaction),
                    "faction_name", claimingFaction.getName());
            return;
        }

        // ensure that the claim limit hasn't been reached
        if (!overriding && chunks.size() > factionConfig.maxClaimFillVolume) {
            messageConfig.fillLimit.send(player, "limit", factionConfig.maxClaimFillVolume);
            return;
        }

        // ensure that the player has permission to claim the land
        if (!overriding && claimingFaction != null && !claimingFaction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
            FactionHelper.missingPermission(player, claimingFaction, "modify territory");
            return;
        }

        // iterate and validate chunks
        Faction playerFaction = factionStore.findFactionOrDefault(player);
        ClaimStore store = ClaimStore.getInstance();
        int conqueredChunkCount = 0;
        Map<Faction, List<ChunkCoordinate>> conqueredFactions = new HashMap<>();
        Iterator<ChunkCoordinate> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            ChunkCoordinate next = iterator.next();

            if (LocationHelper.distance(next.getX(), next.getZ(), cx, cz) > factionConfig.maxClaimDistance) {
                messageConfig.claimTooFar.send(player);
                return;
            }

            Faction faction = store.getFaction(origin.getWorld().getName(), next.getX(), next.getZ());
            if (faction != null) {
                // this chunk is claimed

                if (faction.equals(claimingFaction)) {
                    // our faction already owns this chunk

                    iterator.remove();
                }
                else if (!overriding && !faction.isConquerable() && !faction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
                    // owning faction is strong enough to keep this claim

                    messageConfig.conquerFail.send(player,
                            "faction", FactionHelper.formatRelational(player, faction),
                            "faction_name", faction.getName());
                    return;
                }
                else if (!faction.equals(playerFaction)) {
                    // conquer the chunk
                    conqueredFactions.computeIfAbsent(faction, f -> new ArrayList<>()).add(next);
                    conqueredChunkCount++;
                }
            }
        }

        // ensure there are still chunks to claim
        if (chunks.isEmpty()) {
            // claimingFaction is not null in this context
            // the player's faction has to match this faction
            messageConfig.landOwned.send(player,
                    "faction", FactionHelper.formatRelational(player, claimingFaction),
                    "faction_name", claimingFaction.getName());
            return;
        }

        // ensure that the claims can be conquered
        if (conqueredChunkCount > 0 && canConquer(conqueredFactions, origin.getWorld().getName(), store)) {
            messageConfig.conquerFromEdge.send(player);
            return;
        }

        // claim/unclaim the land
        for (ChunkCoordinate chunk : chunks) {
            if (claimingFaction == null) {
                store.removeClaim(origin.getWorld().getName(), chunk.getX(), chunk.getZ());
            }
            else {
                store.putClaim(origin.getWorld().getName(), chunk.getX(), chunk.getZ(), claimingFaction);
            }
        }
        store.saveClaims();

        // notify the claiming faction
        Faction wilderness = factionStore.getWilderness();
        int claimedChunks = chunks.size() - conqueredChunkCount;
        if (claimedChunks > 0) {
            Faction oldFaction = claimingFaction == null ? actingFaction : wilderness;
            Faction newFaction = claimingFaction == null ? wilderness : actingFaction;

            Component claimType = (claimingFaction == null ? messageConfig.unclaimed : messageConfig.claimed).build();
            Messaging.broadcast(actingFaction, player, pl -> {
                ConfigText message = claimedChunks == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;
                return buildClaimMessage(pl, player, message, origin, action, claimType, claimedChunks, oldFaction,
                        newFaction, playerFaction);
            });
        }

        // notify the conquered/pillaged factions
        if (conqueredChunkCount > 0) {
            Component claimType = (claimingFaction == null ? messageConfig.pillaged : messageConfig.conquered).build();
            Faction newFaction = claimingFaction == null ? wilderness : claimingFaction;

            conqueredFactions.forEach((faction, conquered) -> {
                ConfigText message = conquered.size() == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;

                Chunk chunk = origin.getWorld().getChunkAt(conquered.get(0).getX(), conquered.get(0).getZ());

                // notify the conquered faction
                Messaging.broadcast(faction, pl -> {
                    return buildClaimMessage(pl, player, message, chunk, action, claimType, conquered.size(),
                            faction, newFaction, playerFaction);
                });

                // notify the new faction
                Messaging.broadcast(actingFaction, player, pl -> {
                    return buildClaimMessage(pl, player, message, chunk, action, claimType, conquered.size(), faction,
                            newFaction, playerFaction);
                });
            });
        }
    }

    public static boolean shouldCancelClaim(@NotNull Player player, @Nullable Faction replacedFaction,
                                            @Nullable Faction claimingFaction, boolean claiming) {
        MessageConfig config = MessageConfig.getInstance();

        if (claiming && claimingFaction == null) {
            Faction wilderness = FactionStore.getInstance().getWilderness();
            config.landOwned.send(player,
                    "faction", FactionHelper.formatRelational(player, wilderness),
                    "faction_name", wilderness.getName()
            );
            return true;
        }

        if (replacedFaction != null && !replacedFaction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
            FactionHelper.missingPermission(player, replacedFaction, "modify territory");
            return true;
        }

        return false;
    }

    @NotNull
    public static Set<ChunkCoordinate> square(@NotNull Chunk origin, int radius) {
        radius--;

        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new ChunkCoordinate(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    @NotNull
    public static Set<ChunkCoordinate> line(@NotNull Chunk origin, int length, @NotNull BlockFace facing) {
        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int i = 0; i < length; i++) {
            chunks.add(new ChunkCoordinate(chunkX, chunkZ));
            chunkX += facing.getModX();
            chunkZ += facing.getModZ();
        }

        return chunks;
    }

    @NotNull
    public static Set<ChunkCoordinate> circle(@NotNull Chunk origin, int radius) {
        radius--;

        int chunkX = origin.getX();
        int chunkZ = origin.getZ();
        int radiusSquared = radius * radius;

        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radiusSquared)
                    continue;

                chunks.add(new ChunkCoordinate(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    @Nullable
    public static Set<ChunkCoordinate> fill(@NotNull Chunk origin) {
        int max = FactionConfig.getInstance().maxClaimFillVolume;
        World world = origin.getWorld();
        ClaimStore store = ClaimStore.getInstance();
        Set<ChunkCoordinate> chunks = new HashSet<>();

        // discover chunks to fill
        chunks.add(new ChunkCoordinate(origin.getX(), origin.getZ()));
        recurse(chunks, world.getName(), store.getFaction(origin), store, max);

        // limit was reached, disregard
        if (chunks.size() >= max) {
            return null;
        }

        // convert to chunks
        return chunks;
    }

    private static void recurse(@NotNull Set<ChunkCoordinate> chunks, @NotNull String world,
                                @Nullable Faction faction, @NotNull ClaimStore store, int max) {
        Set<ChunkCoordinate> nearby = new HashSet<>();
        for (ChunkCoordinate chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();

            // check surrounding chunks
            check(nearby, chunks, world, x + 1, z, faction, store);
            check(nearby, chunks, world, x - 1, z, faction, store);
            check(nearby, chunks, world, x, z + 1, faction, store);
            check(nearby, chunks, world, x, z - 1, faction, store);
        }

        // no nearby chunks were found, fill complete
        if (nearby.isEmpty()) {
            return;
        }

        // add the new chunks
        chunks.addAll(nearby);

        // keep searching
        if (nearby.size() < max) {
            recurse(chunks, world, faction, store, max);
        }
    }

    private static void check(@NotNull Set<ChunkCoordinate> nearby, @NotNull Set<ChunkCoordinate> filled,
                              @NotNull String world, int x, int z, @Nullable Faction faction, @NotNull ClaimStore store) {
        ChunkCoordinate coord = new ChunkCoordinate(x, z);
        if (filled.contains(coord)) {
            return;
        }

        // ensure claim is the same as the origin claim
        Faction claimOwner = store.getFaction(world, x, z);
        if (!FactionHelper.equals(faction, claimOwner)) {
            return;
        }

        // able to claim
        nearby.add(coord);
    }

    private static boolean canConquer(@NotNull Map<Faction, List<ChunkCoordinate>> conqueredFactions,
                                      @NotNull String world, @NotNull ClaimStore store) {

        boolean canConquer = true;

        for (Map.Entry<Faction, List<ChunkCoordinate>> entry : conqueredFactions.entrySet()) {
            Faction faction = entry.getKey();
            boolean foundEdge = false;
            for (ChunkCoordinate coordinate : entry.getValue()) {
                boolean isEdge = !FactionHelper.equals(faction, store.getFaction(world, coordinate.getX() - 1, coordinate.getZ()))
                        || !FactionHelper.equals(faction, store.getFaction(world, coordinate.getX() + 1, coordinate.getZ()))
                        || !FactionHelper.equals(faction, store.getFaction(world, coordinate.getX(), coordinate.getZ() - 1))
                        || !FactionHelper.equals(faction, store.getFaction(world, coordinate.getX(), coordinate.getZ() + 1));
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

    @NotNull
    private static Component buildClaimMessage(@NotNull Player recipient, @NotNull Player actor, @NotNull ConfigText message,
                                               @NotNull Chunk origin, @NotNull String action, @NotNull Component claimType,
                                               int amount, @NotNull Faction oldFaction, @NotNull Faction newFaction,
                                               @NotNull Faction playerFaction) {
        return message.build(
                "world", origin.getWorld().getName(),
                "amount", amount,
                "chunk_x", origin.getX(),
                "chunk_z", origin.getZ(),
                "type", action,

                "claim_type", claimType,

                "actor", FactionHelper.formatRelational(recipient, playerFaction, actor),
                "actor_name", actor.getName(),

                "old_faction", FactionHelper.formatRelational(recipient, oldFaction),
                "old_faction_name", oldFaction.getName(),
                "new_faction", FactionHelper.formatRelational(recipient, newFaction),
                "new_faction_name", newFaction.getName()
        );
    }
}
