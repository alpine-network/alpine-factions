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
    SELF(0),

    @SerializedName("neutral")
    NEUTRAL(0),

    @SerializedName("enemy")
    ENEMY(0),

    @SerializedName("truce")
    TRUCE(1),

    @SerializedName("ally")
    ALLY(2);

    private final int weight;
}
