package co.crystaldev.factions.api.faction;

import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Getter
public final class Warp {

    private final @NotNull Location location;

    private final @Nullable String password;

    private final long createdAt;

    public Warp(@NotNull Location location, @Nullable String password, long createdAt) {
        this.location = location;
        this.password = password;
        this.createdAt = createdAt;
    }

    public boolean isPasswordCorrect(String input) {
        if (this.password == null) {
            return true;
        }

        return this.password.equals(input);
    }

    public boolean isPasswordCorrect(Optional<String> input) {
        return this.isPasswordCorrect(input.orElse(null));
    }

    public boolean hasPassword() {
        return password != null;
    }
}
