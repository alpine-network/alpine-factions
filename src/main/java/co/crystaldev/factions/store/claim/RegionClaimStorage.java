package co.crystaldev.factions.store.claim;

import co.crystaldev.factions.store.FactionStore;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public final class RegionClaimStorage {

    @Getter
    private final String key;

    @Getter @SerializedName("world_name")
    private final String worldName;

    @SerializedName("claims")
    private final HashMap<String, Claim> chunkToClaim = new HashMap<>();

    @SerializedName("count")
    private final HashMap<String, Integer> claimCount = new HashMap<>();

    private transient boolean dirty;

    public RegionClaimStorage(@NotNull String key, @NotNull String worldName) {
        this.key = key;
        this.worldName = worldName;
    }

    private RegionClaimStorage() {
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

    @Nullable
    public Claim getClaim(int chunkX, int chunkZ) {
        return this.chunkToClaim.get(getChunkKey(chunkX, chunkZ));
    }

    public boolean isClaimed(int chunkX, int chunkZ) {
        return this.chunkToClaim.containsKey(getChunkKey(chunkX, chunkZ));
    }

    @Nullable
    public Claim putClaim(int chunkX, int chunkZ, @NotNull String factionId) {
        if (factionId.equals(FactionStore.WILDERNESS_ID)) {
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

    @Nullable
    public Claim removeClaim(int chunkX, int chunkZ) {
        Claim claim = this.chunkToClaim.remove(getChunkKey(chunkX, chunkZ));
        if (claim != null) {
            this.modifyCount(claim.getFactionId(), -1);
        }
        return claim;
    }

    @NotNull
    public List<ClaimedChunk> getClaims(@NotNull String factionId) {
        World world = Bukkit.getWorld(this.worldName);
        List<ClaimedChunk> chunks = new ArrayList<>();

        this.chunkToClaim.forEach((key, claim) -> {
            String[] split = key.split(",");
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);

            Chunk chunk = world.getChunkAt(x, z);
            chunks.add(new ClaimedChunk(chunk, claim));
        });

        return chunks;
    }

    public boolean contains(@NotNull String factionId) {
        return this.claimCount.containsKey(factionId);
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
    }

    @NotNull
    private static String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
    }
}
