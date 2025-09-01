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
import co.crystaldev.factions.api.faction.FactionRelation;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class FactionRelationUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Faction targetFaction;

    private @NotNull FactionRelation relation;

    private boolean cancelled;

    public FactionRelationUpdateEvent(@NotNull Faction faction, @NotNull Faction targetFaction,
                                      @NotNull CommandSender entity, @NotNull FactionRelation relation) {
        super(faction, entity);
        this.targetFaction = targetFaction;
        this.relation = relation;
    }

    public @NotNull Faction getTargetFaction() {
        return this.targetFaction;
    }

    public @NotNull FactionRelation getRelation() {
        return this.relation;
    }

    public void setRelation(@NotNull FactionRelation relation) {
        this.relation = relation;
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
