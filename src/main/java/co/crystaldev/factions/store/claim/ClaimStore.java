package co.crystaldev.factions.store.claim;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 0.1.0
 */
public final class ClaimStore extends AlpineStore<String, ClaimRegion> implements ClaimAccessor {

    private final Map<String, ClaimRegion> claimStorage = new ConcurrentHashMap<>();

    ClaimStore(@NotNull AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<String, ClaimRegion>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "regions"))
                .gson(Reference.GSON)
                .dataType(ClaimRegion.class)
                .build(plugin));

        Collection<ClaimRegion> regions = this.loadAllEntries(ex -> {
            Reference.LOGGER.info("Unable to read claim region", ex);
        });
        for (ClaimRegion region : regions) {
            this.claimStorage.put(region.getKey(), region);
        }
    }

    @Override
    public @NotNull List<ClaimedChunk> getClaims(@NotNull Faction faction, @Nullable String world) {
        if (faction.isWilderness()) {
            return Collections.emptyList();
        }

        List<ClaimedChunk> claims = new LinkedList<>();
        this.claimStorage.forEach((key, region) -> {
            if (world != null && !region.getWorldName().equals(world)) {
                return;
            }

            if (region.contains(faction)) {
                claims.addAll(region.getClaims(faction));
            }
        });

        return claims;
    }

    @Override
    public int countClaims(@NotNull Faction faction, @Nullable String world) {
        if (faction.isWilderness()) {
            return 0;
        }

        AtomicInteger claimCounter = new AtomicInteger();
        this.claimStorage.forEach((key, region) -> {
            if (world != null && !region.getWorldName().equals(world)) {
                return;
            }

            if (region.contains(faction)) {
                claimCounter.addAndGet(region.countClaims(faction));
            }
        });

        return claimCounter.get();
    }

    @Override
    public @Nullable Claim getClaim(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return null;
        }

        return this.get(key).getClaim(chunkX, chunkZ);
    }

    @Override
    public boolean isClaimed(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return false;
        }

        return this.get(key).isClaimed(chunkX, chunkZ);
    }

    @Override
    public void save(@NotNull String worldName, int chunkX, int chunkZ) {
        String key = getKey(worldName, chunkX, chunkZ);
        if (!this.has(key)) {
            return;
        }

        ClaimRegion region = this.get(key);
        region.markDirty();
        this.put(key, region);
    }

    @Override
    public @Nullable Claim put(@NotNull String worldName, int chunkX, int chunkZ, @NotNull Faction faction) {
        String key = getKey(worldName, chunkX, chunkZ);
        ClaimRegion region = this.getOrCreate(key, () -> new ClaimRegion(key, worldName));
        this.claimStorage.put(key, region);

        Claim claim = region.putClaim(chunkX, chunkZ, faction);
        this.put(key, region);
        return claim;
    }

    @Override
    public @Nullable Claim remove(@NotNull String worldName, int chunkX, int chunkZ) {
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
