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
import co.crystaldev.factions.util.ComponentHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class FactionDescriptionUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private @Nullable Component description;

    private boolean cancelled;

    public FactionDescriptionUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity, @Nullable Component description) {
        super(faction, entity);
        this.description = description;
    }

    public @Nullable String getPlainDescription() {
        return this.description == null ? null : ComponentHelper.plain(this.description);
    }

    public @Nullable Component getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable Component description) {
        this.description = description;
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
