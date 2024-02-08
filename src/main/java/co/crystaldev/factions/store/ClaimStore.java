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
    public Faction getFaction(@NotNull Chunk chunk) {
        Claim claim = this.getClaim(chunk);
        if (claim == null) {
            return null;
        }

        return FactionStore.getInstance().getFaction(claim.getFactionId());
    }

    @Nullable
    public Claim getClaim(@NotNull Chunk chunk) {
        String key = getKey(chunk);
        if (!this.has(key)) {
            return null;
        }

        return this.get(key).getClaim(chunk.getX(), chunk.getZ());
    }

    public boolean isClaimed(@NotNull Chunk chunk) {
        String key = getKey(chunk);
        if (!this.has(key)) {
            return false;
        }

        return this.get(key).isClaimed(chunk.getX(), chunk.getZ());
    }

    @Nullable
    public Claim putClaim(@NotNull Chunk chunk, @NotNull Faction faction) {
        String key = getKey(chunk);
        ClaimRegion storage = this.getOrCreate(key, () -> {
            ClaimRegion newStorage = new ClaimRegion(key, chunk.getWorld().getName());
            this.claimStorage.put(key, newStorage);
            return newStorage;
        });
        return storage.putClaim(chunk.getX(), chunk.getZ(), faction);
    }

    @Nullable
    public Claim removeClaim(@NotNull Chunk chunk) {
        String key = getKey(chunk);
        if (!this.has(key)) {
            return null;
        }

        ClaimRegion storage = this.get(key);
        Claim removed = storage.removeClaim(chunk.getX(), chunk.getZ());
        if (storage.isEmpty()) {
            this.claimStorage.remove(key);
            this.remove(key);
        }

        return removed;
    }

    public void updateChunk(@NotNull Chunk chunk) {
        String key = getKey(chunk);
        if (this.has(key)) {
            this.get(key).markDirty();
        }
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
    private static String getKey(@NotNull Chunk chunk) {
        int x = chunk.getX() >> 4;
        int z = chunk.getZ() >> 4;
        return chunk.getWorld().getName() + "_" + (x + "_" + z).hashCode();
    }
}
