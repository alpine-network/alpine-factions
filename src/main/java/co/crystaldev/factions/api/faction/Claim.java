package co.crystaldev.factions.api.faction;

import co.crystaldev.alpinecore.Reference;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.player.FPlayer;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
public final class Claim {

    @Getter
    @SerializedName("faction")
    private String factionId;

    @SerializedName("factions")
    private final HashSet<String> accessedFactionIds;

    @SerializedName("players")
    private final HashSet<UUID> accessedPlayerIds;

    @SerializedName("attributes")
    private final HashSet<String> attributes;

    /** This value does not persist after a reload */
    @Getter
    private transient final long claimedAt = System.currentTimeMillis();

    public Claim(@NotNull String faction) {
        this.factionId = faction;
        this.accessedFactionIds = new HashSet<>();
        this.accessedPlayerIds = new HashSet<>();
        this.attributes = new HashSet<>();
    }

    private Claim() {
        // should only get here via Gson
        this(null);
    }

    public void setFaction(@NotNull String factionId) {
        if (this.factionId.equals(factionId)) {
            return;
        }

        this.factionId = factionId;
    }

    public @NotNull Faction getFaction() {
        return Factions.get().factions().getByIdOrDefault(this.factionId);
    }

    public void setFaction(@NotNull Faction faction) {
        this.setFaction(faction.getId());
    }

    public boolean isFaction(@NotNull Faction faction) {
        return this.factionId.equals(faction.getId());
    }

    // region Attribute

    public boolean hasAttribute(@NotNull String attribute) {
        return this.attributes.contains(attribute);
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public void putAttribute(@NotNull String attribute) {
        this.attributes.add(attribute);
    }

    public void removeAttribute(@NotNull String attribute) {
        this.attributes.remove(attribute);
    }

    // endregion Attribute

    // region Access

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
        FactionAccessor factions = Factions.get().factions();
        Faction faction = factions.getById(this.factionId);
        Faction playerFaction = factions.findOrDefault(player);

        return this.isAccessed(player) || this.isAccessed(playerFaction) || faction != null && faction.isPermitted(player, permission);
    }

    public void setAccess(@NotNull Faction faction, boolean accessed) {
        if (accessed) {
            this.accessedFactionIds.add(faction.getId());
        }
        else {
            this.accessedFactionIds.remove(faction.getId());
        }
    }

    public void setAccess(@NotNull OfflinePlayer player, boolean accessed) {
        if (accessed) {
            this.accessedPlayerIds.add(player.getUniqueId());
        }
        else {
            this.accessedPlayerIds.remove(player.getUniqueId());
        }
    }

    public @NotNull Set<FPlayer> getPlayers() {
        PlayerAccessor players = Factions.get().players();
        return this.accessedPlayerIds.stream().map(players::getById).collect(Collectors.toSet());
    }

    public @NotNull Set<Faction> getFactions() {
        FactionAccessor factions = Factions.get().factions();
        return this.accessedFactionIds.stream().map(factions::getById).collect(Collectors.toSet());
    }

    public void clearAccess() {
        this.accessedFactionIds.clear();
        this.accessedPlayerIds.clear();
    }

    // endregion Access

    public static final class Adapter extends TypeAdapter<Claim> {
        @Override
        public void write(JsonWriter jsonWriter, Claim claim) throws IOException {
            if (!claim.modifiesAccess() && !claim.hasAttributes()) {
                jsonWriter.value(claim.factionId);
                return;
            }

            // AlpineCore gson instance
            Reference.GSON.toJson(claim, Claim.class, jsonWriter);
        }

        @Override
        public Claim read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.STRING) {
                return new Claim(jsonReader.nextString());
            }

            // AlpineCore gson instance
            return Reference.GSON.fromJson(jsonReader, Claim.class);
        }
    }
}
