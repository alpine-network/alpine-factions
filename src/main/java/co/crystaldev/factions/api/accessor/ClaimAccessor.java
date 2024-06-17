package co.crystaldev.factions.api.accessor;

import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.util.ChunkCoordinate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 0.1.0
 */
public interface ClaimAccessor {

    // region Claims

    @NotNull
    List<ClaimedChunk> getClaims(@NotNull Faction faction, @Nullable String world);

    @NotNull
    default List<ClaimedChunk> getClaims(@NotNull Faction faction, @Nullable World world) {
        return this.getClaims(faction, world == null ? null : world.getName());
    }

    @NotNull
    default List<ClaimedChunk> getClaims(@NotNull Faction faction) {
        return this.getClaims(faction, (String) null);
    }

    int countClaims(@NotNull Faction faction, @Nullable String world);

    default int countClaims(@NotNull Faction faction, @Nullable World world) {
        return this.countClaims(faction, world == null ? null : world.getName());
    }

    default int countClaims(@NotNull Faction faction) {
        return this.countClaims(faction, (String) null);
    }

    // endregion Claims

    // region Claim

    @Nullable
    Claim getClaim(@NotNull String worldName, int chunkX, int chunkZ);

    @Nullable
    default Claim getClaim(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        return this.getClaim(worldName, chunk.getX(), chunk.getZ());
    }

    @Nullable
    default Claim getClaim(@NotNull Location location) {
        return this.getClaim(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Nullable
    default Claim getClaim(@NotNull Block block) {
        return this.getClaim(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    @Nullable
    default Claim getClaim(@NotNull Chunk chunk) {
        return this.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    boolean isClaimed(@NotNull String worldName, int chunkX, int chunkZ);

    default boolean isClaimed(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        return this.isClaimed(worldName, chunk.getX(), chunk.getZ());
    }

    default boolean isClaimed(@NotNull Location location) {
        return this.isClaimed(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    default boolean isClaimed(@NotNull Block block) {
        return this.isClaimed(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    default boolean isClaimed(@NotNull Chunk chunk) {
        return this.isClaimed(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    default boolean isSameClaim(@NotNull Location a, @NotNull Location b) {
        return this.getFactionOrDefault(a).equals(this.getFactionOrDefault(b));
    }

    default boolean isSameClaim(@NotNull Block a, @NotNull Block b) {
        return this.getFactionOrDefault(a).equals(this.getFactionOrDefault(b));
    }

    default boolean isSameClaim(@NotNull Chunk a, @NotNull Chunk b) {
        return this.getFactionOrDefault(a).equals(this.getFactionOrDefault(b));
    }

    void save(@NotNull String worldName, int chunkX, int chunkZ);

    default void save(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        this.save(worldName, chunk.getX(), chunk.getZ());
    }

    default void save(@NotNull Location location) {
        this.save(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    default void save(@NotNull Block block) {
        this.save(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    default void save(@NotNull Chunk chunk) {
        this.save(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    // endregion Claim

    // region Faction

    @Nullable
    default Faction getFaction(@NotNull String worldName, int chunkX, int chunkZ) {
        Claim claim = this.getClaim(worldName, chunkX, chunkZ);
        if (claim != null) {
            FactionAccessor factions = Factions.get().factions();
            return factions.getById(claim.getFactionId());
        }
        return null;
    }

    @Nullable
    default Faction getFaction(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        return this.getFaction(worldName, chunk.getX(), chunk.getZ());
    }

    @Nullable
    default Faction getFaction(@NotNull Location location) {
        return this.getFaction(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Nullable
    default Faction getFaction(@NotNull Block block) {
        return this.getFaction(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    @Nullable
    default Faction getFaction(@NotNull Chunk chunk) {
        return this.getFaction(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @NotNull
    default Faction getFactionOrDefault(@NotNull String worldName, int chunkX, int chunkZ) {
        FactionAccessor factions = Factions.get().factions();
        Claim claim = this.getClaim(worldName, chunkX, chunkZ);
        Faction fac = null;
        if (claim != null) {
            fac = factions.getById(claim.getFactionId());
        }
        return fac == null ? factions.getWilderness() : fac;
    }

    @NotNull
    default Faction getFactionOrDefault(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        return this.getFactionOrDefault(worldName, chunk.getX(), chunk.getZ());
    }

    @NotNull
    default Faction getFactionOrDefault(@NotNull Location location) {
        return this.getFactionOrDefault(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @NotNull
    default Faction getFactionOrDefault(@NotNull Block block) {
        return this.getFactionOrDefault(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    @NotNull
    default Faction getFactionOrDefault(@NotNull Chunk chunk) {
        return this.getFactionOrDefault(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    // endregion Faction

    // region Claiming

    @Nullable
    Claim put(@NotNull String worldName, int chunkX, int chunkZ, @NotNull Faction faction);

    @Nullable
    default Claim put(@NotNull String worldName, @NotNull ChunkCoordinate chunk, @NotNull Faction faction) {
        return this.put(worldName, chunk.getX(), chunk.getZ(), faction);
    }

    @Nullable
    default Claim put(@NotNull Location location, @NotNull Faction faction) {
        return this.put(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4, faction);
    }

    @Nullable
    default Claim put(@NotNull Block block, @NotNull Faction faction) {
        return this.put(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4, faction);
    }

    @Nullable
    default Claim put(@NotNull Chunk chunk, @NotNull Faction faction) {
        return this.put(chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), faction);
    }

    @Nullable
    Claim remove(@NotNull String worldName, int chunkX, int chunkZ);

    @Nullable
    default Claim remove(@NotNull String worldName, @NotNull ChunkCoordinate chunk) {
        return this.remove(worldName, chunk.getX(), chunk.getZ());
    }

    @Nullable
    default Claim remove(@NotNull Location location) {
        return this.remove(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Nullable
    default Claim remove(@NotNull Block block) {
        return this.remove(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    @Nullable
    default Claim remove(@NotNull Chunk chunk) {
        return this.remove(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    // endregion Claiming
}
