package co.crystaldev.factions.api.map;

import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 0.4.0
 */
public final class FactionMapFormatter {

    private final List<ClaimFormatter> transformers = new LinkedList<>();

    public void register(@NotNull ClaimFormatter transformer) {
        this.transformers.add(transformer);
    }

    public @Nullable Component formatClaim(@NotNull Claim claim, @NotNull Faction faction, @NotNull String symbol) {
        if (!this.transformers.isEmpty()) {
            for (ClaimFormatter transformer : this.transformers) {
                Component transformed = transformer.transform(claim, faction, symbol);
                if (transformed != null) {
                    return transformed;
                }
            }

        }
        return null;
    }
}
