package co.crystaldev.factions.command.claiming;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public enum ClaimType {
    SQUARE("s", "sq"),
    CIRCLE("c"),
    LINE("l");

    @Getter
    private final List<String> aliases;

    ClaimType(@NotNull String... aliases) {
        this.aliases = ImmutableList.<String>builder()
                .add(name().toLowerCase())
                .add(aliases)
                .build();
    }

    @Nullable
    public static ClaimType get(@NotNull String type) {
        for (ClaimType value : values()) {
            if (value.aliases.contains(type)) {
                return value;
            }
        }
        return null;
    }
}
