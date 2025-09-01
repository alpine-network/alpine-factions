/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.event.framework;

import co.crystaldev.factions.api.faction.Faction;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public abstract class FactionEntityEvent<T> extends FactionEvent {

    private final @NotNull T entity;

    public FactionEntityEvent(@NotNull Faction faction, @NotNull T entity) {
        super(faction);
        this.entity = entity;
    }

    public FactionEntityEvent(@NotNull Faction faction, @NotNull T entity, boolean async) {
        super(faction, async);
        this.entity = entity;
    }

    public @NotNull T getEntity() {
        return this.entity;
    }
}
