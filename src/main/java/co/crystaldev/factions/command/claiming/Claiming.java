package co.crystaldev.factions.command.claiming;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.store.claim.ClaimStore;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
@UtilityClass
final class Claiming {

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
