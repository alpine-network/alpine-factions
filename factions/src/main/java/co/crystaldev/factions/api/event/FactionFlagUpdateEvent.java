/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class FactionFlagUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull FactionFlag<?> flag;

    private @NotNull Object value;

    private boolean cancelled;

    public FactionFlagUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @NotNull FactionFlag<?> flag, @NotNull Object value) {
        super(faction, entity);
        this.flag = flag;
        this.value = value;
    }

    public @NotNull FactionFlag<?> getFlag() {
        return this.flag;
    }

    public @NotNull Object getValue() {
        return this.value;
    }

    public void setValue(@NotNull Object value) {
        this.value = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
