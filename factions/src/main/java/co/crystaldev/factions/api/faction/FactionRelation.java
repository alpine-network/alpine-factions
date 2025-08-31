package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public enum FactionRelation implements Relational {
    @SerializedName("self")
    SELF("self", 0, true) {
        @Override
        public boolean isValid() {
            return false;
        }
    },

    @SerializedName("enemy")
    ENEMY("enemy", 0, false),

    @SerializedName("neutral")
    NEUTRAL("neutral", 1, false),

    @SerializedName("truce")
    TRUCE("truce", 2, true),

    @SerializedName("ally")
    ALLY("ally", 3, true);

    private final @NotNull String id;

    private final int weight;

    private final boolean friendly;

    FactionRelation(@NotNull String id, int weight, boolean friendly) {
        this.weight = weight;
        this.id = id;
        this.friendly = friendly;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isFriendly() {
        return this.friendly;
    }
}
