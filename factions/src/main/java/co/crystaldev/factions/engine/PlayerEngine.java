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
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class PlayerEngine extends AlpineEngine {
    PlayerEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerHandler.getInstance().loggedIn(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        PlayerHandler.getInstance().loggedOut(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerMove(PlayerMoveEvent event) {
        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();

        // player did not change chunks
        if (from.equals(to)) {
            return;
        }

        PlayerState state = PlayerHandler.getInstance().getPlayer(event.getPlayer());
        state.onMoveChunk(from, to);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();

        // player did not change chunks
        if (from.equals(to)) {
            return;
        }

        PlayerState state = PlayerHandler.getInstance().getPlayer(event.getPlayer());
        state.onMoveChunk(from, to);
    }
}
