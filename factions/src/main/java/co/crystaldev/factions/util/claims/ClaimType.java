package co.crystaldev.factions.util.claims;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 0.1.0
 */
@Getter
public enum ClaimType {
    SQUARE("s", "sq"),
    CIRCLE("c"),
    LINE("l");

    private final List<String> aliases;

    ClaimType(@NotNull String... aliases) {
        this.aliases = ImmutableList.<String>builder()
                .add(name().toLowerCase())
                .add(aliases)
                .build();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static @Nullable ClaimType get(@NotNull String type) {
        for (ClaimType value : values()) {
            if (value.aliases.contains(type)) {
                return value;
            }
        }
        return null;
    }
}
