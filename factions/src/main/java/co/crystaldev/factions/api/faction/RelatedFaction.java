/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 0.1.0
 */
public final class RelatedFaction {
    private final @NotNull Faction faction;

    private final @NotNull FactionRelation relation;

    public RelatedFaction(@NotNull Faction faction, @NotNull FactionRelation relation) {
        this.faction = faction;
        this.relation = relation;
    }

    public @NotNull Faction getFaction() {
        return this.faction;
    }

    public @NotNull FactionRelation getRelation() {
        return this.relation;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RelatedFaction)) return false;
        RelatedFaction that = (RelatedFaction) object;
        return Objects.equals(this.getFaction(), that.getFaction())
                && this.getRelation() == that.getRelation();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFaction(), this.getRelation());
    }

    public String toString() {
        return "RelatedFaction(faction=" + this.getFaction() + ", relation=" + this.getRelation() + ")";
    }
}
