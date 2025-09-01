/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util.claims;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 0.1.0
 */
@Getter
public enum ClaimType {
    SQUARE("s", "sq"),
    CIRCLE("c"),
    LINE("l");

    private final List<String> aliases;

    ClaimType(@NotNull String... aliases) {
        this.aliases = ImmutableList.<String>builder()
                .add(name().toLowerCase())
                .add(aliases)
                .build();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static @Nullable ClaimType get(@NotNull String type) {
        for (ClaimType value : values()) {
            if (value.aliases.contains(type)) {
                return value;
            }
        }
        return null;
    }
}
