package co.crystaldev.factions.handler.player;

import co.crystaldev.factions.api.faction.Faction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE) @Getter
public final class AutoClaimState {

    private boolean enabled;

    private Faction faction;

    public void toggle(@Nullable Faction faction) {
        this.enabled = !this.enabled;
        this.faction = faction;
    }
}
