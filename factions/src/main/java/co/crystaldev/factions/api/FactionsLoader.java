/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class FactionsLoader {
    static final FactionsProvider INSTANCE;

    static {
        ServicesManager manager = Bukkit.getServicesManager();
        if (manager.isProvidedFor(FactionsProvider.class)) {
            INSTANCE = manager.load(FactionsProvider.class);
        }
        else {
            throw new IllegalArgumentException("No FactionsProvider implementation found");
        }
    }
}
