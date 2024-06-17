package co.crystaldev.factions.api.faction;

import lombok.Data;

/**
 * @since 0.1.0
 */
@Data
public final class RelatedFaction {
    private final Faction faction;
    private final FactionRelation relation;
}
