/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction.permission;

import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.handler.PlayerHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @since 0.1.0
 */
public final class Permission {
    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @Nullable String permission;
    private final @NotNull Set<Rank> defaultRankPermits;
    private final @NotNull Set<FactionRelation> defaultRelationPermits;

    public Permission(@NotNull String id, @NotNull String name,
                      @NotNull String description, @Nullable String permission,
                      @NotNull Set<Rank> defaultRankPermits,
                      @NotNull Set<FactionRelation> defaultRelationPermits) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.defaultRankPermits = defaultRankPermits;
        this.defaultRelationPermits = defaultRelationPermits;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getDescription() {
        return this.description;
    }

    public @Nullable String getPermission() {
        return this.permission;
    }

    public @NotNull Set<Rank> getDefaultRankPermits() {
        return this.defaultRankPermits;
    }

    public @NotNull Set<FactionRelation> getDefaultRelationPermits() {
        return this.defaultRelationPermits;
    }

    public boolean isPermitted(@NotNull Permissible permissible) {
        if (permissible instanceof ConsoleCommandSender) {
            return true;
        }

        if (permissible instanceof CommandSender && PlayerHandler.getInstance().isOverriding((CommandSender) permissible)) {
            return true;
        }

        return this.permission == null || permissible.hasPermission(this.permission);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Permission)) return false;
        Permission that = (Permission) object;
        return Objects.equals(this.getId(), that.getId())
                && Objects.equals(this.getName(), that.getName())
                && Objects.equals(this.getDescription(), that.getDescription())
                && Objects.equals(this.getPermission(), that.getPermission())
                && Objects.equals(this.getDefaultRankPermits(), that.getDefaultRankPermits())
                && Objects.equals(this.getDefaultRelationPermits(), that.getDefaultRelationPermits());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getDescription(), this.getPermission(), this.getDefaultRankPermits(), this.getDefaultRelationPermits());
    }

    @Override
    public String toString() {
        return "Permission(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", permission=" + this.getPermission() + ", defaultRankPermits=" + this.getDefaultRankPermits() + ", defaultRelationPermits=" + this.getDefaultRelationPermits() + ")";
    }

    public static @NotNull Builder builder(@NotNull String id) {
        return new Builder(id);
    }

    /**
     * @since 0.1.0
     */
    public static final class Builder {
        private final String id;
        private String name;
        private String description;
        private String permission;
        private Set<Rank> defaultRankPermits;
        private Set<FactionRelation> defaultRelationPermits;

        public Builder(String id) {
            this.id = id;
        }

        public @NotNull Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public @NotNull Builder description(@NotNull String description) {
            this.description = description;
            return this;
        }

        public @NotNull Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        public @NotNull Builder permit(@NotNull Rank... permittedRanks) {
            this.defaultRankPermits = ImmutableSet.copyOf(permittedRanks);
            return this;
        }

        public @NotNull Builder permit(@NotNull FactionRelation... permittedRelations) {
            this.defaultRelationPermits = ImmutableSet.copyOf(permittedRelations);
            return this;
        }

        public @NotNull Permission build() {
            Preconditions.checkNotNull(this.id, "id must not be null");
            Preconditions.checkNotNull(this.name, "name must not be null");
            Preconditions.checkNotNull(this.description, "description must not be null");
            return new Permission(this.id, this.name, this.description, this.permission,
                    this.defaultRankPermits == null ? Collections.emptySet() : this.defaultRankPermits,
                    this.defaultRelationPermits == null ? Collections.emptySet() : this.defaultRelationPermits);
        }
    }
}
