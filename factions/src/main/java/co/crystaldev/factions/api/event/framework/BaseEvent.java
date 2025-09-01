/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.event.framework;

import co.crystaldev.alpinecore.framework.event.AlpineEvent;
import co.crystaldev.factions.AlpineFactions;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public abstract class BaseEvent extends AlpineEvent {

    private final @NotNull AlpineFactions plugin = AlpineFactions.getInstance();

    public BaseEvent() {
        // NO-OP
    }

    public BaseEvent(boolean async) {
        super(async);
    }

    public @NotNull AlpineFactions getPlugin() {
        return this.plugin;
    }
}
