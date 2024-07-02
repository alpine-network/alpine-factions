package co.crystaldev.factions.store.claim;

import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 0.1.0
 */
public final class ClaimRegion {

    @Getter
    private final String key;

    @Getter @SerializedName("world_name")
    private final String worldName;

    @SerializedName("claims")
    private final ConcurrentHashMap<String, Claim> chunkToClaim = new ConcurrentHashMap<>();

    @SerializedName("count")
    private final ConcurrentHashMap<String, Integer> claimCount = new ConcurrentHashMap<>();

    private transient boolean dirty;

    public ClaimRegion(@NotNull String key, @NotNull String worldName) {
        this.key = key;
        this.worldName = worldName;
    }

    private ClaimRegion() {
        // should only get here via Gson
        this(null, null);
    }

    public boolean isDirty() {
        if (this.dirty) {
            this.dirty = false;
            return true;
        }
        return false;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public @Nullable Claim getClaim(int chunkX, int chunkZ) {
        return this.chunkToClaim.get(getChunkKey(chunkX, chunkZ));
    }

    public boolean isClaimed(int chunkX, int chunkZ) {
        return this.chunkToClaim.containsKey(getChunkKey(chunkX, chunkZ));
    }

    public @Nullable Claim putClaim(int chunkX, int chunkZ, @NotNull Faction faction) {
        String factionId = faction.getId();
        if (factionId.equals(Faction.WILDERNESS_ID)) {
            return null;
        }

        Claim claim = this.getClaim(chunkX, chunkZ);
        if (claim != null) {
            // claim was overclaimed

            // reduce the claim count for the previous owner
            this.modifyCount(claim.getFactionId(), -1);

            // replace the faction
            claim.setFaction(factionId);
            claim.clearAccess();
            return claim;
        }

        claim = new Claim(factionId);
        this.chunkToClaim.put(getChunkKey(chunkX, chunkZ), claim);
        this.modifyCount(factionId, 1);
        return claim;
    }

    public @Nullable Claim removeClaim(int chunkX, int chunkZ) {
        Claim claim = this.chunkToClaim.remove(getChunkKey(chunkX, chunkZ));
        if (claim != null) {
            this.modifyCount(claim.getFactionId(), -1);
        }
        return claim;
    }

    public @NotNull List<ClaimedChunk> getClaims(@NotNull Faction faction) {
        World world = Bukkit.getWorld(this.worldName);
        List<ClaimedChunk> chunks = new ArrayList<>();

        this.chunkToClaim.forEach((key, claim) -> {
            if (!claim.getFactionId().equals(faction.getId()))
                return;

            String[] split = key.split(",");
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);
            chunks.add(new ClaimedChunk(claim, world.getName(), x, z));
        });

        return chunks;
    }

    public int countClaims(@NotNull Faction faction) {
        return this.claimCount.getOrDefault(faction.getId(), 0);
    }

    public boolean contains(@NotNull Faction faction) {
        return this.claimCount.containsKey(faction.getId());
    }

    public boolean isEmpty() {
        return this.chunkToClaim.isEmpty();
    }

    private void modifyCount(@NotNull String factionId, int modifier) {
        int count = this.claimCount.getOrDefault(factionId, 0) + modifier;
        if (count <= 0) {
            this.claimCount.remove(factionId);
        }
        else {
            this.claimCount.put(factionId, count);
        }
        this.markDirty();
    }

    private static @NotNull String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
    }
}
