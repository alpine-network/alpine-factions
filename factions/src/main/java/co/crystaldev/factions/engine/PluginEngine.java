/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.util.ComponentRateLimiter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.1.0
 */
public final class PluginEngine extends AlpineEngine {
    PluginEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        AlpineFactions plugin = AlpineFactions.getInstance();
        plugin.flags().unregister(event.getPlugin());
        plugin.permissions().unregister(event.getPlugin());
    }

    @EventHandler
    private void onServerTick(ServerTickEvent event) {
        if (event.isTime(1, TimeUnit.MINUTES)) {
            ComponentRateLimiter.getInstance().clean();
        }
    }
}
