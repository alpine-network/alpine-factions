package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.permission.Permission;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public final class Claim {

    @Getter
    private String factionId;

    private final HashSet<String> accessedFactionIds;

    private final HashSet<UUID> accessedPlayerIds;

    private Claim(@Nullable String faction, @NotNull HashSet<String> accessedFactionIds, @NotNull HashSet<UUID> accessedPlayerIds) {
        this.factionId = faction;
        this.accessedFactionIds = accessedFactionIds;
        this.accessedPlayerIds = accessedPlayerIds;
    }

    public Claim(@NotNull String faction) {
        this(faction, new HashSet<>(), new HashSet<>());
    }

    private Claim() {
        // should only get here via Gson
        this(null, new HashSet<>(), new HashSet<>());
    }

    public void setFaction(@NotNull String faction) {
        if (this.factionId.equals(faction)) {
            return;
        }

        this.factionId = faction;
    }

    @NotNull
    public Faction getFaction() {
        return Accessors.factions().getByIdOrDefault(this.factionId);
    }

    public void setFaction(@NotNull Faction faction) {
        this.setFaction(faction.getId());
    }

    public boolean isFaction(@NotNull Faction faction) {
        return this.factionId.equals(faction.getId());
    }

    public boolean modifiesAccess() {
        return this.accessedFactionIds != null && !this.accessedFactionIds.isEmpty()
                || this.accessedPlayerIds != null && !this.accessedPlayerIds.isEmpty();
    }

    public boolean isAccessed(@NotNull String factionId) {
        return this.accessedFactionIds != null && this.accessedFactionIds.contains(factionId);
    }

    public boolean isAccessed(@NotNull Faction faction) {
        return this.isAccessed(faction.getId());
    }

    public boolean isAccessed(@NotNull UUID player) {
        return this.accessedPlayerIds != null && this.accessedPlayerIds.contains(player);
    }

    public boolean isAccessed(@NotNull OfflinePlayer player) {
        return this.isAccessed(player.getUniqueId());
    }

    public boolean isPermitted(@NotNull OfflinePlayer player, @NotNull Permission permission) {
        FactionAccessor factions = Accessors.factions();
        Faction faction = factions.getById(this.factionId);
        Faction playerFaction = factions.findOrDefault(player);

        return this.isAccessed(player) || this.isAccessed(playerFaction) || faction != null && faction.isPermitted(player, permission);
    }

    public void setAccess(@NotNull String factionId, boolean accessed) {
        if (accessed) {
            this.accessedFactionIds.add(factionId);
        }
        else {
            this.accessedFactionIds.remove(factionId);
        }
    }

    public void setAccess(@NotNull UUID player, boolean accessed) {
        if (accessed) {
            this.accessedPlayerIds.add(player);
        }
        else {
            this.accessedPlayerIds.remove(player);
        }
    }

    public void clearAccess() {
        this.accessedFactionIds.clear();
        this.accessedPlayerIds.clear();
    }

    public static final class Adapter extends TypeAdapter<Claim> {
        @Override
        public void write(JsonWriter jsonWriter, Claim claim) throws IOException {
            if (!claim.modifiesAccess()) {
                jsonWriter.value(claim.factionId);
                return;
            }

            Reference.GSON.toJson(claim, Claim.class, jsonWriter);
        }

        @Override
        public Claim read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.STRING) {
                return new Claim(jsonReader.nextString());
            }
            return Reference.GSON.fromJson(jsonReader, Claim.class);
        }
    }
}
