package co.crystaldev.factions.api.faction.permission;

import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.member.Rank;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/20/2023
 */
@NoArgsConstructor
public final class PermissionHolder {
    private final HashSet<Rank> permittedRanks = new HashSet<>();
    private final HashSet<RelationType> permittedRelations = new HashSet<>();

    public PermissionHolder(@NotNull Permission permission) {
        this.permittedRanks.addAll(permission.getDefaultRankPermits());
        this.permittedRelations.addAll(permission.getDefaultRelationPermits());
    }

    public boolean isPermitted(@NotNull Rank rank) {
        return this.permittedRanks.contains(rank);
    }

    public boolean isPermitted(@NotNull RelationType relation) {
        return this.permittedRelations.contains(relation);
    }

    public void set(@NotNull Rank rank, boolean permitted) {
        if (permitted) {
            this.permittedRanks.add(rank);
        }
        else {
            this.permittedRanks.remove(rank);
        }
    }

    public void set(@NotNull RelationType relation, boolean permitted) {
        if (permitted) {
            this.permittedRelations.add(relation);
        }
        else {
            this.permittedRelations.remove(relation);
        }
    }
}
