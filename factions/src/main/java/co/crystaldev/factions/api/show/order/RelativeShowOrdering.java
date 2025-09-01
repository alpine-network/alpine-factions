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
final class RelativeShowOrdering implements ShowOrder {

    private final ShowComponent relative;

    private final boolean before;

    public RelativeShowOrdering(@NotNull ShowComponent relative, boolean before) {
        this.relative = relative;
        this.before = before;
    }

    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        int index = 1;
        for (ShowComponent component : components) {
            if (component.equals(this.relative)) {
                return Math.max(0, Math.min(length, this.before ? index - 1 : index + 1));
            }

            index++;
        }

        // fallback to end
        return -1;
    }
}
