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

/**
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClaimFormatter {
    @Nullable Component transform(@NotNull Claim claim, @NotNull Faction faction, @NotNull FactionRelation relation,
                                  char character, @NotNull Component symbol);
}
