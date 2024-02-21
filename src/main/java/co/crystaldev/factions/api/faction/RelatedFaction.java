package co.crystaldev.factions.api.faction;

import lombok.Data;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/20/2024
 */
@Data
public final class RelatedFaction {
    private final Faction faction;
    private final FactionRelation relation;
}
