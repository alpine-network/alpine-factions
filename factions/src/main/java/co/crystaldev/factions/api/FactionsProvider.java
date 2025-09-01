/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api;

import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.map.FactionMapFormatter;
import co.crystaldev.factions.api.registry.FlagRegistry;
import co.crystaldev.factions.api.registry.PermissionRegistry;
import co.crystaldev.factions.api.show.ShowFormatter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public interface FactionsProvider {

    @NotNull FactionAccessor registry();

    @NotNull PlayerAccessor players();

    @NotNull ClaimAccessor claims();

    @NotNull FlagRegistry flags();

    @NotNull PermissionRegistry permissions();

    @NotNull ShowFormatter showFormatter();

    @NotNull FactionMapFormatter mapFormatter();
}
