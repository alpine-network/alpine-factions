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
    LEADER("***"),
    COLEADER("**"),
    MOD("*"),
    MEMBER("+"),
    RECRUIT("-");

    private final String prefix;

    @NotNull
    public static Rank getDefault() {
        return RECRUIT;
    }
}
