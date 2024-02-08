package co.crystaldev.factions.command.claiming;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.LocationHelper;
import co.crystaldev.factions.util.Messaging;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
@UtilityClass
final class Claiming {

    public static void attemptClaim(@NotNull Player player, @NotNull String action,
                                    @Nullable Faction replacedFaction, @Nullable Faction claimingFaction,
                                    @NotNull Set<Chunk> chunks, @NotNull Chunk origin) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();

        Chunk playerChunk = player.getLocation().getChunk();
        int cx = playerChunk.getX();
        int cz = playerChunk.getZ();

        // ensure there are chunks to claim
        if (chunks.isEmpty()) {
            return;
        }

        // ensure the claiming faction has enough power to claim this land
        if (claimingFaction != null && claimingFaction.getRemainingPower() < chunks.size()) {
            messageConfig.notEnoughPower.send(player,
                    "faction", FactionHelper.formatRelational(player, claimingFaction),
                    "faction_name", claimingFaction.getName());
            return;
        }

        // ensure that the claim limit hasn't been reached
        if (chunks.size() > factionConfig.maxClaimFillVolume) {
            messageConfig.fillLimit.send(player, "limit", factionConfig.maxClaimFillVolume);
            return;
        }

        // iterate and validate chunks
        int conqueredChunkCount = 0;
        Map<Faction, List<Chunk>> conqueredFactions = new HashMap<>();
        ClaimStore store = ClaimStore.getInstance();
        Iterator<Chunk> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk next = iterator.next();

            if (LocationHelper.distance(next.getX(), next.getZ(), cx, cz) > factionConfig.maxClaimDistance) {
                messageConfig.claimTooFar.send(player);
                return;
            }

            Faction faction = store.getFaction(next);
            if (faction != null) {
                // this chunk is claimed

                if (faction.equals(claimingFaction)) {
                    // our faction already owns this chunk

                    iterator.remove();
                }
                else if (!faction.isConquerable() && !faction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
                    // owning faction is strong enough to keep this claim

                    messageConfig.conquerFail.send(player,
                            "faction", FactionHelper.formatRelational(player, faction),
                            "faction_name", faction.getName());
                    return;
                }
                else {
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

        // claim/unclaim the land
        for (Chunk chunk : chunks) {
            if (claimingFaction == null) {
                store.removeClaim(chunk);
            }
            else {
                store.putClaim(chunk, claimingFaction);
            }
        }

        // notify the claiming faction
        Faction wilderness = FactionStore.getInstance().getWilderness();
        int claimedChunks = chunks.size() - conqueredChunkCount;
        if (claimedChunks > 0) {
            Faction faction = claimingFaction == null ? replacedFaction : claimingFaction;
            Faction oldFaction = replacedFaction == null ? wilderness : replacedFaction;
            Faction newFaction = claimingFaction == null ? wilderness : claimingFaction;

            Messaging.broadcast(faction, pl -> {
                ConfigText message = claimedChunks == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;
                return buildClaimMessage(pl, player, message, origin, action, messageConfig.claimed.build(),
                        claimedChunks, oldFaction, newFaction);
            });
        }

        // notify the conquered/pillaged factions
        if (conqueredChunkCount > 0) {
            Component claimType = (claimingFaction == null ? messageConfig.pillaged : messageConfig.conquered).build();
            Faction newFaction = claimingFaction == null ? wilderness : claimingFaction;

            conqueredFactions.forEach((faction, conquered) -> {
                ConfigText message = conquered.size() == 1 ? messageConfig.landClaimSingle : messageConfig.landClaim;

                // notify the conquered faction
                Messaging.broadcast(faction, pl -> {
                    return buildClaimMessage(pl, player, message, conquered.get(0), action,
                            claimType, conquered.size(), faction, newFaction);
                });

                // notify the new faction
                Messaging.broadcast(claimingFaction == null ? replacedFaction : claimingFaction, pl -> {
                    return buildClaimMessage(pl, player, message, conquered.get(0), action,
                            claimType, conquered.size(), faction, newFaction);
                });
            });
        }
    }

    @NotNull
    private static Component buildClaimMessage(@NotNull Player recipient, @NotNull Player actor, @NotNull ConfigText message,
                                               @NotNull Chunk origin, @NotNull String action, @NotNull Component claimType,
                                               int amount, @NotNull Faction oldFaction, @NotNull Faction newFaction) {
        return message.build(
                "world", origin.getWorld().getName(),
                "amount", amount,
                "chunk_x", origin.getX(),
                "chunk_z", origin.getZ(),
                "type", action,

                "claim_type", claimType,

                "player", FactionHelper.formatRelational(recipient, newFaction, actor),
                "player_name", actor.getName(),

                "old_faction", FactionHelper.formatRelational(recipient, oldFaction),
                "old_faction_name", oldFaction.getName(),
                "new_faction", FactionHelper.formatRelational(recipient, newFaction),
                "new_faction_name", newFaction.getName()
        );
    }

    @NotNull
    public static Set<Chunk> square(@NotNull Chunk origin, int radius) {
        radius--;

        World world = origin.getWorld();
        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<Chunk> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(world.getChunkAt(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    @NotNull
    public static Set<Chunk> line(@NotNull Chunk origin, int length, @NotNull BlockFace facing) {
        World world = origin.getWorld();
        int chunkX = origin.getX();
        int chunkZ = origin.getZ();

        Set<Chunk> chunks = new HashSet<>();
        for (int i = 0; i < length; i++) {
            chunks.add(world.getChunkAt(chunkX, chunkZ));
            chunkX += facing.getModX();
            chunkZ += facing.getModZ();
        }

        return chunks;
    }

    @NotNull
    public static Set<Chunk> circle(@NotNull Chunk origin, int radius) {
        radius--;

        World world = origin.getWorld();
        int chunkX = origin.getX();
        int chunkZ = origin.getZ();
        int radiusSquared = radius * radius;

        Set<Chunk> chunks = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radiusSquared)
                    continue;

                chunks.add(world.getChunkAt(chunkX + x, chunkZ + z));
            }
        }

        return chunks;
    }

    @Nullable
    public static Set<Chunk> fill(@NotNull Chunk origin) {
        int max = FactionConfig.getInstance().maxClaimFillVolume;
        ClaimStore store = ClaimStore.getInstance();
        Set<Chunk> chunks = new HashSet<>();

        // discover chunks to fill
        chunks.add(origin);
        recurse(chunks, store.getFaction(origin), store, max);

        // limit was reached, disregard
        if (chunks.size() >= max) {
            return null;
        }

        return chunks;
    }

    private static void recurse(@NotNull Set<Chunk> chunks, @Nullable Faction faction, @NotNull ClaimStore store, int max) {
        Set<Chunk> nearby = new HashSet<>();
        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();
            World world = chunk.getWorld();

            // check surrounding chunks
            check(nearby, chunks, world.getChunkAt(x + 1, z), faction, store);
            check(nearby, chunks, world.getChunkAt(x - 1, z), faction, store);
            check(nearby, chunks, world.getChunkAt(x, z + 1), faction, store);
            check(nearby, chunks, world.getChunkAt(x, z - 1), faction, store);
        }

        // no nearby chunks were found, fill complete
        if (nearby.isEmpty()) {
            return;
        }

        // keep searching
        if (nearby.size() < max) {
            recurse(chunks, faction, store, max);
        }
    }

    private static void check(@NotNull Set<Chunk> nearby, @NotNull Set<Chunk> filled, @NotNull Chunk chunk,
                              @Nullable Faction faction, @NotNull ClaimStore store) {
        if (filled.contains(chunk)) {
            return;
        }

        // ensure claim is the same as the origin claim
        Faction claimOwner = store.getFaction(chunk);
        if (!Objects.equals(faction, claimOwner)) {
            return;
        }

        // able to claim
        nearby.add(chunk);
    }
}
