package co.crystaldev.factions.config.type;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Getter @AllArgsConstructor @NoArgsConstructor
@Configuration
public final class PluralizedConfigMessage {

    private ConfigText single;

    private ConfigText plural;

    public @NotNull ConfigText get(int count) {
        return count == 1 ? this.single : this.plural;
    }

    public static @NotNull PluralizedConfigMessage of(@NotNull ConfigText single, @NotNull ConfigText plural) {
        return new PluralizedConfigMessage(single, plural);
    }
}
