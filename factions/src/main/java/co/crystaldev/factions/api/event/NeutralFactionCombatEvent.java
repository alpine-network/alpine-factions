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
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
public final class NeutralFactionCombatEvent extends FactionEntityEvent<Player> implements Cancellable {

    private final @NotNull Faction attackingFaction;

    private final @NotNull Player attacker;

    private boolean cancelled = true;

    public NeutralFactionCombatEvent(@NotNull Faction faction, @NotNull Player entity,
                                     @NotNull Faction attackingFaction, @NotNull Player attacker) {
        super(faction, entity);
        this.attackingFaction = attackingFaction;
        this.attacker = attacker;
    }

    public @NotNull Faction getAttackingFaction() {
        return this.attackingFaction;
    }

    public @NotNull Player getAttacker() {
        return this.attacker;
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
