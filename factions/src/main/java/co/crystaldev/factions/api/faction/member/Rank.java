/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction.member;

import co.crystaldev.factions.api.Relational;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public enum Rank implements Relational {
    @SerializedName("leader")    LEADER("leader", "***"),
    @SerializedName("coleader")  COLEADER("coleader", "**"),
    @SerializedName("officer")   OFFICER("officer", "*"),
    @SerializedName("member")    MEMBER("member", "+"),
    @SerializedName("recruit")   RECRUIT("recruit", "-");

    private final @NotNull String id;

    private final @NotNull String prefix;

    Rank(@NotNull String id, @NotNull String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull String getPrefix() {
        return this.prefix;
    }

    public @NotNull Rank getNextRank() {
        return values()[Math.max(0, this.ordinal() - 1)];
    }

    public @NotNull Rank getPreviousRank() {
        return values()[Math.min(4, this.ordinal() + 1)];
    }

    public boolean isSuperior(@NotNull Rank other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isSuperiorOrMatching(@NotNull Rank other) {
        return this.ordinal() <= other.ordinal();
    }

    public static @NotNull Rank getDefault() {
        return RECRUIT;
    }
}
