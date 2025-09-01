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
public abstract class FactionEvent extends BaseEvent {

    private final @NotNull Faction faction;

    public FactionEvent(@NotNull Faction faction) {
        this.faction = faction;
    }

    public FactionEvent(@NotNull Faction faction, boolean async) {
        super(async);
        this.faction = faction;
    }

    public @NotNull Faction getFaction() {
        return this.faction;
    }
}
