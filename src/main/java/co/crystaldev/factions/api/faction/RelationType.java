package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/13/2023
 */
@AllArgsConstructor @Getter
public enum RelationType implements Relational {
    @SerializedName("self")    SELF(false),
    @SerializedName("neutral") NEUTRAL(true),
    @SerializedName("enemy")   ENEMY(true),
    @SerializedName("truce")   TRUCE(true),
    @SerializedName("ally")    ALLY(true);

    private final boolean applicable;

    @Override
    public boolean isRank() {
        return false;
    }
}
