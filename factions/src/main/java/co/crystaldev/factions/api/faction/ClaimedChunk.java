/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class ClaimedChunk {
    private final @NotNull Claim claim;
    private final @NotNull String world;
    private final int x;
    private final int z;

    public ClaimedChunk(@NotNull Claim claim, @NotNull String world, int x, int z) {
        this.claim = claim;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public @NotNull Claim getClaim() {
        return this.claim;
    }

    public @NotNull String getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public @NotNull Chunk getChunk() {
        return Bukkit.getWorld(this.world).getChunkAt(this.x, this.z);
    }
}
