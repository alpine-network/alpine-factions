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
    SELF(0, "self") {
        @Override
        public boolean isValid() {
            return false;
        }
    },

    @SerializedName("neutral")
    NEUTRAL(0, "neutral"),

    @SerializedName("enemy")
    ENEMY(0, "enemy"),

    @SerializedName("truce")
    TRUCE(1, "truce"),

    @SerializedName("ally")
    ALLY(2, "ally");

    private final int weight;

    private final String id;
}
