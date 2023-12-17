package co.crystaldev.factions.api.faction;

import lombok.Data;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/13/2023
 */
@Data
public final class Relation {
    private final UUID faction;
    private final RelationType type;
}
