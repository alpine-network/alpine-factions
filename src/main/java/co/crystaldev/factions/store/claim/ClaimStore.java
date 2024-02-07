package co.crystaldev.factions.store.claim;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.store.FactionStore;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public final class ClaimStore extends AlpineStore<String, RegionClaimStorage> {

    @Getter
    private static ClaimStore instance;
    { instance = this; }

    private final Map<String, RegionClaimStorage> claimStorage = new HashMap<>();

    ClaimStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<String, RegionClaimStorage>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "claims"))
                .gson(Reference.GSON)
                .dataType(RegionClaimStorage.class)
                .build());

        Collection<RegionClaimStorage> regions = this.loadAllEntries(ex -> {
            Reference.LOGGER.info("Unable to read claim region", ex);
        });
        for (RegionClaimStorage region : regions) {
            this.claimStorage.put(region.getKey(), region);
        }
        AlpineFactions.getInstance().log(String.format("Cached %dx claim regions", this.claimStorage.size()));
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

            if (region.contains(faction.getId())) {
                claims.addAll(region.getClaims(faction.getId()));
            }
        });

        return claims;
    }

    @NotNull
    public List<ClaimedChunk> getClaims(@Nullable Faction faction) {
        return this.getClaims(faction, null);
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
        RegionClaimStorage storage = this.get(getKey(chunk));
        if (storage == null) {
            return null;
        }

        return storage.getClaim(chunk.getX(), chunk.getZ());
    }

    public boolean isClaimed(@NotNull Chunk chunk) {
        RegionClaimStorage storage = this.get(getKey(chunk));
        if (storage == null) {
            return false;
        }

        return storage.isClaimed(chunk.getX(), chunk.getZ());
    }

    @Nullable
    public Claim putClaim(@NotNull Chunk chunk, @NotNull Faction faction) {
        String key = getKey(chunk);
        RegionClaimStorage storage = this.getOrCreate(key, () -> {
            RegionClaimStorage newStorage = new RegionClaimStorage(key, chunk.getWorld().getName());
            this.claimStorage.put(key, newStorage);
            return newStorage;
        });
        return storage.putClaim(chunk.getX(), chunk.getZ(), faction.getId());
    }

    @Nullable
    public Claim removeClaim(@NotNull Chunk chunk) {
        String key = getKey(chunk);
        RegionClaimStorage storage = this.get(key);
        if (storage == null) {
            return null;
        }

        Claim removed = storage.removeClaim(chunk.getX(), chunk.getZ());
        if (storage.isEmpty()) {
            this.claimStorage.remove(key);
            this.remove(key);
        }

        return removed;
    }

    public void updateChunk(@NotNull Chunk chunk) {
        RegionClaimStorage storage = this.get(getKey(chunk));
        if (storage != null) {
            storage.markDirty();
        }
    }

    @NotNull
    private static String getKey(@NotNull Chunk chunk) {
        int x = chunk.getX() >> 4;
        int z = chunk.getZ() >> 4;
        return chunk.getWorld().getName() + "_" + x + "_" + z;
    }
}
