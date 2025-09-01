/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.map;

import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 0.4.0
 */
public final class FactionMapFormatter {

    private final List<ClaimFormatter> transformers = new LinkedList<>();

    public void register(@NotNull ClaimFormatter transformer) {
        this.transformers.add(transformer);
    }

    public @Nullable Component formatClaim(@NotNull Claim claim, @NotNull Faction faction, @NotNull FactionRelation relation,
                                           char character, @NotNull Component symbol) {
        if (!this.transformers.isEmpty()) {
            for (ClaimFormatter transformer : this.transformers) {
                Component transformed = transformer.transform(claim, faction, relation, character, symbol);
                if (transformed != null) {
                    return transformed;
                }
            }

        }
        return null;
    }
}
