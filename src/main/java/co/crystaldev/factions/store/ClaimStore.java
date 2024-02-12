package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimRegion;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public final class ClaimStore extends AlpineStore<String, ClaimRegion> {

    @Getter
    private static ClaimStore instance;
    { instance = this; }

    private final Map<String, ClaimRegion> claimStorage = new HashMap<>();

    ClaimStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<String, ClaimRegion>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "regions"))
                .gson(Reference.GSON)
                .dataType(ClaimRegion.class)
                .build());

        Collection<ClaimRegion> regions = this.loadAllEntries(ex -> {
            Reference.LOGGER.info("Unable to read claim region", ex);
        });
        for (ClaimRegion region : regions) {
            this.claimStorage.put(region.getKey(), region);
        }
    }

    @NotNull
    public List<ClaimedChunk> getClaims(@Nullable Faction faction, @Nullable World world) {
        if (faction == null || faction.getId().equals(FactionStore.WILDERNESS_ID)) {
            return Collections.emptyList();
        }

        List<ClaimedChunk> claims = new LinkedList<>();
        this.claimStorage.forEach((key, region) -> {
            if (world != null && !region.getWorldName().equals(world.getName())) {
                return;
            }

            if (region.contains(faction)) {
                claims.addAll(region.getClaims(faction));
            }
        });

        return claims;
    }

    @NotNull
    public List<ClaimedChunk> getClaims(@Nullable Faction faction) {
        return this.getClaims(faction, null);
    }

    public int countClaims(@Nullable Faction faction, @Nullable World world) {
        if (faction == null || faction.getId().equals(FactionStore.WILDERNESS_ID)) {
            return 0;
        }

        AtomicInteger claimCounter = new AtomicInteger();
        this.claimStorage.forEach((key, region) -> {
            if (world != null && !region.getWorldName().equals(world.getName())) {
                return;
            }

            if (region.contains(faction)) {
                claimCounter.addAndGet(region.countClaims(faction));
            }
        });

        return claimCounter.get();
    }

    public int countClaims(@Nullable Faction faction) {
        return this.countClaims(faction, null);
    }

    @Nullable
    public Faction getFaction(@NotNull String worldName, int chunkX, int chunkZ) {
        Claim claim = this.getClaim(worldName, chunkX, chunkZ);
        if (claim == null) {
            return null;
        }

        return FactionStore.getInstance().getFaction(claim.getFactionId());
    }

    @Nullable
    public Faction getFaction(@NotNull Chunk chunk) {
        return this.getFaction(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Nullable
    public Faction getFaction(@NotNull Entity entity) {
        return this.getFaction(entity.getLocation().getChunk());
    }

    @NotNull
    public Faction getFactionOrDefault(@NotNull String worldName, int chunkX, int chunkZ) {
        FactionStore store = FactionStore.getInstance();
        Claim claim = this.getClaim(worldName, chunkX, chunkZ);
        if (claim == null) {
            return store.getWilderness();
        }

        return Optional.ofNullable(store.getFaction(claim.getFactionId())).orElseGet(store::getWilderness);
    }

    @NotNull
    public Faction getFactionOrDefault(@NotNull Chunk chunk) {
        return this.getFactionOrDefault(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @NotNull
    public Faction getFactionOrDefault(@NotNull Entity entity) {
        return this.getFactionOrDefault(entity.getLocation().getChunk());
    }

    @Nullable
    public Claim getClaim(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return null;
        }

        return this.get(key).getClaim(chunkX, chunkZ);
    }

    @Nullable
    public Claim getClaim(@NotNull Chunk chunk) {
        return this.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public boolean isClaimed(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return false;
        }

        return this.get(key).isClaimed(chunkX, chunkZ);
    }

    public boolean isClaimed(@NotNull Chunk chunk) {
        return this.isClaimed(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public boolean isSameClaim(@NotNull Chunk a, @NotNull Chunk b) {
        return this.getFactionOrDefault(a).equals(this.getFactionOrDefault(b));
    }

    @Nullable
    public Claim putClaim(@NotNull String worldName, int chunkX, int chunkZ, @NotNull Faction faction) {
        String key = getKey(worldName, chunkX, chunkZ);
        ClaimRegion region = this.getOrCreate(key, () -> new ClaimRegion(key, worldName));
        this.claimStorage.put(key, region);

        Claim claim = region.putClaim(chunkX, chunkZ, faction);
        this.put(key, region);
        return claim;
    }

    @Nullable
    public Claim putClaim(@NotNull Chunk chunk, @NotNull Faction faction) {
        return this.putClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), faction);
    }

    @Nullable
    public Claim removeClaim(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return null;
        }

        ClaimRegion region = this.get(key);
        Claim removed = region.removeClaim(chunkX, chunkZ);
        if (region.isEmpty()) {
            this.claimStorage.remove(key);
            this.remove(key);
        }
        else {
            this.claimStorage.put(key, region);
        }

        this.put(key, region);
        return removed;
    }

    @Nullable
    public Claim removeClaim(@NotNull Chunk chunk) {
        return this.removeClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public void saveClaims() {
        boolean updated = false;
        for (ClaimRegion region : this.claimStorage.values()) {
            if (region.isDirty()) {
                this.put(region.getKey(), region);
                updated = true;
            }
        }

        if (updated) {
            this.flush();
        }
    }

    @NotNull
    private static String getKey(@NotNull String worldName, int x, int z) {
        return worldName + "_" + ((x >> 5) + "_" + (z >> 5)).hashCode();
    }
}
