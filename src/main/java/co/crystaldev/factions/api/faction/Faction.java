package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.member.Member;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@Data
public final class Faction {

    /** The unique ID of this faction */
    private final UUID id = UUID.randomUUID();

    private String name;

    private String description;

    private final long createdAt = System.currentTimeMillis();

    private final ArrayList<Member> members = new ArrayList<>();

    private final ArrayList<Relation> relations = new ArrayList<>();

    private final Map<FactionFlag, Boolean> flags = new HashMap<>();
    {
        for (FactionFlag flag : FactionFlags.VALUES) {
            this.flags.put(flag, flag.isDefaultState());
        }
    }

    public void addMember(@NotNull Member member) {
        this.members.add(member);
    }

    public void removeMember(@NotNull Member member) {
        this.members.remove(member);
    }

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
