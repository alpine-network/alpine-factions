/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class Warp {

    private @NotNull String name;

    private @NotNull Location location;

    private @Nullable String password;

    private final long createdAt = System.currentTimeMillis();

    private long updatedAt = System.currentTimeMillis();

    private Warp(@NotNull String name, @NotNull Location location, @Nullable String password) {
        this.name = name;
        this.location = location;
        this.password = password;
    }

    private Warp() {
        // should only get here via Gson
    }

    public void updateWarp(Location location, String password) {
        this.location = location;
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull Location getLocation() {
        return this.location;
    }

    public @Nullable String getPassword() {
        return this.password;
    }

    public boolean hasPassword() {
        return this.password != null;
    }

    public boolean isPasswordCorrect(String input) {
        if (this.password == null) {
            return true;
        }

        return this.password.equals(input);
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public static Warp of(@NotNull String name, @NotNull Location location, @Nullable String password) {
        return new Warp(name, location, password);
    }
}
