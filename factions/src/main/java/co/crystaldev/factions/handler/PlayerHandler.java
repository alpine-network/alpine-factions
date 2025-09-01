/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.handler;

import co.crystaldev.factions.handler.player.PlayerState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @since 0.1.0
 */
public final class PlayerHandler {

    @Getter
    private static PlayerHandler instance;
    { instance = this; }

    private final Map<UUID, PlayerState> playerStateMap = new ConcurrentHashMap<>();

    public void loggedIn(@NotNull Player player) {
        this.getPlayer(player).onLogin();
    }

    public void loggedOut(@NotNull Player player) {
        this.getPlayer(player).onLogout();
        this.playerStateMap.remove(player.getUniqueId());
    }

    public @NotNull PlayerState getPlayer(@NotNull Player player) {
        return this.playerStateMap.computeIfAbsent(player.getUniqueId(), id -> new PlayerState(player));
    }

    public void forEach(@NotNull Consumer<PlayerState> playerConsumer) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerState state = this.getPlayer(player);
            playerConsumer.accept(state);
        });
    }

    public boolean isOverriding(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        return this.getPlayer((Player) sender).isOverriding();
    }
}
