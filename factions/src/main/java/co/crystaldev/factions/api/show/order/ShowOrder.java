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
public interface ShowOrder {

    int computeIndex(@NotNull Iterable<ShowComponent> components, int length);

    static @NotNull ShowOrder inserting() {
        return new BasicShowOrdering();
    }

    static @NotNull ShowOrder before(@NotNull ShowComponent component) {
        return new RelativeShowOrdering(component, true);
    }

    static @NotNull ShowOrder after(@NotNull ShowComponent component) {
        return new RelativeShowOrdering(component, false);
    }

    static @NotNull ShowOrder fixed(int index) {
        return new FixedShowOrdering(index);
    }
}
