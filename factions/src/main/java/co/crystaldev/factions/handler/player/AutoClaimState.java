/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.handler.player;

import co.crystaldev.factions.api.faction.Faction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE) @Getter
public final class AutoClaimState {

    private boolean enabled;

    private Faction faction;

    public void toggle(@Nullable Faction faction) {
        this.enabled = !this.enabled;
        this.faction = faction;
    }
}
