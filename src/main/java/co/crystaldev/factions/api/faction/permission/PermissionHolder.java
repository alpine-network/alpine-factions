package co.crystaldev.factions.api.faction.permission;

import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.1.0
 */
public final class PermissionHolder {
    private final HashSet<Rank> permittedRanks = new HashSet<>();
    private final HashSet<FactionRelation> permittedRelations = new HashSet<>();

    public PermissionHolder(@NotNull Permission permission) {
        this.permittedRanks.addAll(permission.getDefaultRankPermits());
        this.permittedRelations.addAll(permission.getDefaultRelationPermits());
    }

    private PermissionHolder() {
        // should only get here via Gson
        this(null);
    }

    public boolean isPermitted(@NotNull Relational relation) {
        return (relation instanceof Rank ? this.permittedRanks : this.permittedRelations).contains(relation);
    }

    public void set(@NotNull Relational relation, boolean permitted) {
        Set<Relational> permits = (Set) (relation instanceof Rank ? this.permittedRanks : this.permittedRelations);
        if (permitted) {
            permits.add(relation);
        }
        else {
            permits.remove(relation);
        }
    }

    public void set(boolean permitted, @NotNull Relational... relations) {
        this.permittedRanks.clear();
        this.permittedRelations.clear();

        if (!permitted) {
            return;
        }

        for (Relational relational : relations) {
            if (relational instanceof Rank) {
                this.permittedRanks.add((Rank) relational);
            }
            else {
                this.permittedRelations.add((FactionRelation) relational);
            }
        }
    }
}
