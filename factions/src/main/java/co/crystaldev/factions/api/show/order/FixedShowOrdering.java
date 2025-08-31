/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
final class FixedShowOrdering implements ShowOrder {

    private final int index;

    public FixedShowOrdering(int index) {
        this.index = index;
    }

    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        return Math.max(0, Math.min(length - 1, this.index));
    }
}
