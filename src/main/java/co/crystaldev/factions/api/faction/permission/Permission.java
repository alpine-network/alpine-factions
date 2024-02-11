package co.crystaldev.factions.api.faction.permission;

import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.faction.member.Rank;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/20/2023
 */
@Data
public final class Permission {
    private final String id;
    private final String name;
    private final String description;
    private final String permission;
    private final Set<Rank> defaultRankPermits;
    private final Set<RelationType> defaultRelationPermits;

    @NotNull
    public static Builder builder(@NotNull String id) {
        return new Builder(id);
    }

    @RequiredArgsConstructor
    public static final class Builder {
        private final String id;
        private String name;
        private String description;
        private String permission;
        private Set<Rank> defaultRankPermits;
        private Set<RelationType> defaultRelationPermits;

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder description(@NotNull String description) {
            this.description = description;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public Builder permit(@NotNull Rank... permittedRanks) {
            this.defaultRankPermits = ImmutableSet.copyOf(permittedRanks);
            return this;
        }

        @NotNull
        public Builder permit(@NotNull RelationType... permittedRelations) {
            this.defaultRelationPermits = ImmutableSet.copyOf(permittedRelations);
            return this;
        }

        @NotNull
        public Permission build() {
            Preconditions.checkNotNull(this.id, "id must not be null");
            Preconditions.checkNotNull(this.name, "name must not be null");
            Preconditions.checkNotNull(this.description, "description must not be null");
            return new Permission(this.id, this.name, this.description, this.permission,
                    this.defaultRankPermits == null ? Collections.emptySet() : this.defaultRankPermits,
                    this.defaultRelationPermits == null ? Collections.emptySet() : this.defaultRelationPermits);
        }
    }
}
