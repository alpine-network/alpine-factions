/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.show.component;

import co.crystaldev.factions.api.show.ShowContext;
import co.crystaldev.factions.api.show.order.ShowOrder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 0.1.0
 */
public interface ShowComponent {

    @NotNull ShowOrder getOrdering();

    @Nullable Component buildComponent(@NotNull ShowContext ctx);

    default boolean isVisible(@NotNull ShowContext ctx) {
        return true;
    }

    static @NotNull ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @Nullable Component> componentFunction,
            @Nullable Predicate<ShowContext> visibilityPredicate
    ) {
        return new FunctionalShowComponent(order, componentFunction, visibilityPredicate);
    }

    static @NotNull ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @Nullable Component> componentPredicate
    ) {
        return new FunctionalShowComponent(order, componentPredicate, null);
    }
}
