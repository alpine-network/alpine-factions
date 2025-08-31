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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.FactionConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.1.0
 */
public final class PowerEngine extends AlpineEngine {
    PowerEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        if (!event.isTime(1, TimeUnit.MINUTES)) {
            return;
        }

        FactionConfig config = this.plugin.getConfiguration(FactionConfig.class);
        double powerGain = config.powerGainPerHour / 60.0;

        PlayerAccessor players = Factions.players();
        players.forEach(player -> {
            // modify the player's power
            player.setPowerLevel(Math.min(player.getPowerLevel() + powerGain, config.maxPlayerPower));

            // modify the player's playtime
            player.setPlaytime(player.getPlaytime() + 1);

            // update the user!
            players.save(player);
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        FactionConfig config = this.plugin.getConfiguration(FactionConfig.class);
        PlayerAccessor players = Factions.players();
        FPlayer player = players.get(event.getEntity());

        player.setPowerLevel(Math.max(0.0, player.getPowerLevel() + config.powerLossPerDeath));
        players.save(player);
    }
}
