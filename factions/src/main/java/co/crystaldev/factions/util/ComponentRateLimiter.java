/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr
 * @since 07/30/2024
 */
@ApiStatus.Internal
public final class ComponentRateLimiter {

    private static final ComponentRateLimiter INSTANCE = new ComponentRateLimiter();

    private static final long DURATION = TimeUnit.SECONDS.toMillis(2L);

    // Player ID/Component hash -> expiration
    private final Map<Long, Long> rateLimiters = new HashMap<>();

    public boolean isLimited(@NotNull Player player, @NotNull Component component) {
        long hash = Objects.hash(player.getUniqueId(), component);
        Long expiration = this.rateLimiters.get(hash);
        if (expiration == null || System.currentTimeMillis() > expiration) {
            this.rateLimiters.put(hash, System.currentTimeMillis() + DURATION);
            return false;
        }

        return System.currentTimeMillis() < expiration;
    }

    public void clean() {
        this.rateLimiters.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getKey());
    }

    public static @NotNull ComponentRateLimiter getInstance() {
        return INSTANCE;
    }
}
