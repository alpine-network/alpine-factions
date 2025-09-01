/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.util.ChunkCoordinate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 0.1.0
 */
public final class FactionTerritoryUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final @NotNull World world;

    private final @NotNull Type type;

    private final @NotNull Set<ChunkCoordinate> chunks;

    private final @NotNull Map<Faction, List<ChunkCoordinate>> conqueredChunks;

    private boolean cancelled;

    public FactionTerritoryUpdateEvent(@NotNull Faction faction, @NotNull Player entity, @NotNull World world,
                                       @NotNull Type type, @NotNull Set<ChunkCoordinate> chunks,
                                       @NotNull Map<Faction, List<ChunkCoordinate>> conqueredChunks,
                                       boolean async) {
        super(faction, entity, async);
        this.world = world;
        this.type = type;
        this.chunks = chunks;
        this.conqueredChunks = conqueredChunks;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public @NotNull Type getType() {
        return this.type;
    }

    public @NotNull Set<ChunkCoordinate> getChunks() {
        return this.chunks;
    }

    public @NotNull Map<Faction, List<ChunkCoordinate>> getConqueredChunks() {
        return this.conqueredChunks;
    }

    public boolean contains(@NotNull Location location) {
        if (!this.world.equals(location.getWorld())) {
            return false;
        }

        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;

        for (ChunkCoordinate chunk : this.chunks) {
            if (chunk.getX() == cx && chunk.getZ() == cz) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(@NotNull Chunk chunk) {
        if (!this.world.equals(chunk.getWorld())) {
            return false;
        }

        for (ChunkCoordinate coordinate : this.chunks) {
            if (coordinate.getX() == chunk.getX() && coordinate.getZ() == chunk.getZ()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Type {
        CLAIM,
        UNCLAIM
    }
}
