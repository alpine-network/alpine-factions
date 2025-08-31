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
final class FunctionalShowComponent implements ShowComponent {

    private static final Predicate<ShowContext> DEFAULT_VISIBILITY_PREDICATE = ctx -> true;

    private final ShowOrder order;

    private final Function<ShowContext, @NotNull Component> componentPredicate;

    private final Predicate<ShowContext> visibilityPredicate;

    public FunctionalShowComponent(@NotNull ShowOrder order,
                                   @NotNull Function<ShowContext, @NotNull Component> componentFunction,
                                   @Nullable Predicate<ShowContext> visibilityPredicate) {
        this.order = order;
        this.componentPredicate = componentFunction;
        this.visibilityPredicate = visibilityPredicate == null ? DEFAULT_VISIBILITY_PREDICATE : visibilityPredicate;
    }

    @Override
    public @NotNull ShowOrder getOrdering() {
        return this.order;
    }

    @Override
    public @NotNull Component buildComponent(@NotNull ShowContext ctx) {
        return this.componentPredicate.apply(ctx);
    }

    @Override
    public boolean isVisible(@NotNull ShowContext ctx) {
        return this.visibilityPredicate.test(ctx);
    }
}
