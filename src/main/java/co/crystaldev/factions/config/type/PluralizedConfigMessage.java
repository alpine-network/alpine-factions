package co.crystaldev.factions.config.type;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/20/2024
 */
@Getter @AllArgsConstructor @NoArgsConstructor
@Configuration
public final class PluralizedConfigMessage {

    private ConfigText single;

    private ConfigText plural;

    @NotNull
    public ConfigText get(int count) {
        return count == 1 ? this.single : this.plural;
    }

    @NotNull
    public static PluralizedConfigMessage of(@NotNull ConfigText single, @NotNull ConfigText plural) {
        return new PluralizedConfigMessage(single, plural);
    }
}
