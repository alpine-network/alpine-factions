package co.crystaldev.factions.api.map;

import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClaimFormatter {
    @Nullable Component transform(@NotNull Claim claim, @NotNull Faction faction, @NotNull FactionRelation relation,
                                  char character, @NotNull Component symbol);
}
