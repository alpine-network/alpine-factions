package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/13/2023
 */
public enum FactionRelation implements Relational {
    @SerializedName("self")
    SELF,

    @SerializedName("neutral")
    NEUTRAL,

    @SerializedName("enemy")
    ENEMY,

    @SerializedName("truce")
    TRUCE,

    @SerializedName("ally")
    ALLY
}
