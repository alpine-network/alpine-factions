package co.crystaldev.factions.api.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Getter
@AllArgsConstructor(staticName = "of") @NoArgsConstructor
public final class Warp {

    private @NotNull String name;

    private @NotNull Location location;

    private @Nullable String password;

    private final long createdAt = System.currentTimeMillis();

    private long updatedAt;

    public void updateWarp(Location location, String password) {
        this.location = location;
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
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
        return this.password != null;
    }
}
