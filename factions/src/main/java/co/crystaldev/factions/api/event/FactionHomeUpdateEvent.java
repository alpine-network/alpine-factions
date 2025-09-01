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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class FactionHomeUpdateEvent extends FactionEntityEvent<Player> implements Cancellable {

    private @Nullable Location location;

    private boolean cancelled;

    public FactionHomeUpdateEvent(@NotNull Faction faction, @NotNull Player entity, @Nullable Location location) {
        super(faction, entity);
        this.location = location;
    }

    public @Nullable Location getLocation() {
        return this.location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    public boolean wasUnset() {
        return this.location == null;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
