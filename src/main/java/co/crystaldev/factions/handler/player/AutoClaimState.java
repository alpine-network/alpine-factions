package co.crystaldev.factions.handler.player;

import co.crystaldev.factions.api.faction.Faction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class AutoClaimState {

    private int mode;
    private Faction faction;

    public boolean isAutoClaim() {
        return this.mode > 0;
    }

    public boolean isAutoUnclaim() {
        return this.mode < 0;
    }

    public void toggleAutoClaim(@Nullable Faction faction) {
        this.mode = this.isAutoClaim() ? 0 : 1;
        this.faction = this.isAutoClaim() ? faction : null;
    }

    public void toggleAutoUnclaim() {
        this.mode = this.isAutoUnclaim() ? 0 : -1;
        this.faction = null;
    }
}
