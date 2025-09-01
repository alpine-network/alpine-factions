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
import co.crystaldev.factions.api.faction.member.Member;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class FactionMemberTitleUpdateEvent extends FactionEntityEvent<Member> implements Cancellable {

    private @Nullable Component title;

    private boolean cancelled;

    public FactionMemberTitleUpdateEvent(@NotNull Faction faction, @NotNull Member entity, @Nullable Component title) {
        super(faction, entity);
        this.title = title;
    }

    public @Nullable Component getTitle() {
        return this.title;
    }

    public void setTitle(@Nullable Component title) {
        this.title = title;
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
