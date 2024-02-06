package co.crystaldev.factions.api.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@AllArgsConstructor @Getter
public enum Rank {
    LEADER("***", "leader"),
    COLEADER("**", "coleader"),
    MOD("*", "moderator"),
    MEMBER("+", "member"),
    RECRUIT("-", "recruit");

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

    @NotNull
    public static Rank getDefault() {
        return RECRUIT;
    }
}
