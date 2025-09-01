/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.player;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public enum TerritorialTitleMode {
    @SerializedName("title")      TITLE("Title"),
    @SerializedName("action_bar") ACTION_BAR("Action Bar"),
    @SerializedName("chat")       CHAT("Chat");

    private final @NotNull String name;

    TerritorialTitleMode(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }
}
