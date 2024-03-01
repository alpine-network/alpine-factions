package co.crystaldev.factions.api.faction.member;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@AllArgsConstructor @Getter
public enum Rank implements Relational {
    @SerializedName("leader")    LEADER("***", "leader"),
    @SerializedName("coleader")  COLEADER("**", "coleader"),
    @SerializedName("moderator") MOD("*", "moderator"),
    @SerializedName("member")    MEMBER("+", "member"),
    @SerializedName("recruit")   RECRUIT("-", "recruit");

    private final String prefix;

    private final String id;

    @NotNull
    public Rank getNextRank() {
        return values()[Math.max(0, this.ordinal() - 1)];
    }

    @NotNull
    public Rank getPreviousRank() {
        return values()[Math.min(4, this.ordinal() + 1)];
    }

    public boolean isSuperior(@NotNull Rank other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isSuperiorOrMatching(@NotNull Rank other) {
        return this.ordinal() >= other.ordinal();
    }

    @NotNull
    public static Rank getDefault() {
        return RECRUIT;
    }
}
