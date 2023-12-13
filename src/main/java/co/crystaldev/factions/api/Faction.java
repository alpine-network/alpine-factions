package co.crystaldev.factions.api;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@Data
public final class Faction {

    private final UUID id = UUID.randomUUID();

    private String name;

    private String description;

    private final long createdAt = System.currentTimeMillis();

    private final ArrayList<Member> members = new ArrayList<>();

    private final ArrayList<Relation> relations = new ArrayList<>();

    public boolean isNeutral(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.NEUTRAL);
    }

    public boolean isEnemy(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.ENEMY);
    }

    public boolean isTruce(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.TRUCE);
    }

    public boolean isAlly(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.ALLY);
    }

    private boolean isRelation(@NotNull Faction faction, @NotNull RelationType type) {
        for (Relation relation : this.relations) {
            if (relation.getFaction().equals(faction.getId()) && relation.getType() == type)
                return true;
        }
        return false;
    }
}
