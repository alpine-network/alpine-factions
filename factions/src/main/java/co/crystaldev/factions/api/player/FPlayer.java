/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.player;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.config.FactionConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * @since 0.1.0
 */
public final class FPlayer {

    private final @NotNull UUID id;

    private double powerLevel;

    private long powerBoost;

    private int playtime;

    private boolean friendlyFire;

    private int autoMapHeight = 8;

    private @Nullable TerritorialTitleMode territorialTitleMode = TerritorialTitleMode.TITLE;

    public FPlayer(@NotNull UUID id) {
        this.id = id;
    }

    public @NotNull UUID getId() {
        return this.id;
    }

    public double getPowerLevel() {
        return this.powerLevel;
    }

    public void setPowerLevel(double powerLevel) {
        this.powerLevel = powerLevel;
    }

    public long getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(long powerBoost) {
        this.powerBoost = powerBoost;
    }

    public long getEffectivePower() {
        return (long) (this.powerLevel + this.powerBoost);
    }

    public long getMaxPower() {
        return AlpineFactions.getInstance().getConfiguration(FactionConfig.class).maxPlayerPower + this.powerBoost;
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }

    public int getPlaytime() {
        return this.playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public int getAutoMapHeight() {
        return this.autoMapHeight;
    }

    public void setAutoMapHeight(int autoMapHeight) {
        this.autoMapHeight = autoMapHeight;
    }

    public @NotNull TerritorialTitleMode getTerritorialTitleMode() {
        return Optional.ofNullable(this.territorialTitleMode).orElse(TerritorialTitleMode.TITLE);
    }

    public void setTerritorialTitleMode(@Nullable TerritorialTitleMode territorialTitleMode) {
        this.territorialTitleMode = territorialTitleMode;
    }
}
