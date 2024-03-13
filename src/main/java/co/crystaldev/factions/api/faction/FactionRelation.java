package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/13/2023
 */
@RequiredArgsConstructor @Getter
public enum FactionRelation implements Relational {
    @SerializedName("self")
    SELF(0, "self", true) {
        @Override
        public boolean isValid() {
            return false;
        }
    },

    @SerializedName("enemy")
    ENEMY(0, "enemy", false),

    @SerializedName("neutral")
    NEUTRAL(1, "neutral", false),

    @SerializedName("truce")
    TRUCE(2, "truce", true),

    @SerializedName("ally")
    ALLY(3, "ally", true);

    private final int weight;

    private final String id;

    private final boolean friendly;
}
