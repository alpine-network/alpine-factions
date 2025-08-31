/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.handler.player;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.AsciiFactionMap;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.RelationHelper;
import co.crystaldev.factions.util.claims.Claiming;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@RequiredArgsConstructor @Getter
public final class PlayerState {

    private final Player player;

    private final AutoClaimState autoClaimState = new AutoClaimState();

    @Setter
    private boolean autoFactionMap;

    @Setter
    private boolean overriding;

    public void onLogin() {
        Faction faction = Factions.registry().findOrDefault(this.player);
        Component motd = faction.getMotd();

        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        if (!faction.isWilderness()) {
            // Notify the faction the player has joined
            AlpineFactions.schedule(() -> FactionHelper.broadcast(faction, this.player, observer -> {
                return config.login.build(
                        "player", RelationHelper.formatLiteralPlayerName(observer, this.player));
            }), 20L);
        }


        if (motd != null) {
            Component title = config.motdTitle.build(
                    "faction", RelationHelper.formatLiteralFactionName(this.player, faction),
                    "faction_name", faction.getName());

            AlpineFactions.schedule(() -> {
                Component builtMotd = Components.joinNewLines(Formatting.title(title), motd);
                Messaging.send(this.player, builtMotd);
            }, 100L);
        }
    }

    public void onLogout() {
        Faction faction = Factions.registry().findOrDefault(this.player);

        if (!faction.isWilderness()) {
            MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
            // Notify the faction the player has left
            FactionHelper.broadcast(faction, this.player, observer -> {
                return config.logout.build(
                        "player", RelationHelper.formatPlayerName(observer, this.player));
            });
        }
    }

    public void onMoveChunk(@NotNull Chunk oldChunk, @NotNull Chunk newChunk) {
        FPlayer state = Factions.players().get(this.player);

        // player has entered into a new faction claim
        ClaimAccessor claims = Factions.claims();
        if (!claims.isSameClaim(oldChunk, newChunk)) {
            this.displayTerritorialTitle(state, newChunk);
        }

        // now we should attempt to claim/unclaim
        if (this.autoClaimState.isEnabled()) {
            Faction actingFaction = Factions.registry().findOrDefault(this.player);
            AlpineFactions.schedule(() -> Claiming.one(this.player, actingFaction, this.autoClaimState.getFaction()));
        }

        // now send the minimized faction map
        if (this.autoFactionMap) {
            AlpineFactions.schedule(() -> Messaging.send(this.player, AsciiFactionMap.create(this.player, true)));
        }

        // check the player's access
        boolean isElevated = Optional.ofNullable(claims.getClaim(newChunk)).map(claim -> claim.isAccessed(this.player)).orElse(false);
        boolean wasElevated = Optional.ofNullable(claims.getClaim(oldChunk)).map(claim -> claim.isAccessed(this.player)).orElse(false);
        if (isElevated != wasElevated) {
            MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
            ConfigText message = isElevated ? config.elevatedAccess : config.standardAccess;
            AlpineFactions.schedule(() -> message.send(this.player));
        }
    }

    private void displayTerritorialTitle(@NotNull FPlayer state, @NotNull Chunk chunk) {
        Faction faction = Factions.claims().getFactionOrDefault(chunk);
        TerritorialTitleMode mode = state.getTerritorialTitleMode();

        Component description = Optional.ofNullable(faction.getDescription()).orElse(Faction.DEFAULT_DESCRIPTION);

        if (mode == TerritorialTitleMode.TITLE) {
            Messaging.title(this.player, RelationHelper.formatLiteralFactionName(this.player, faction),
                    Component.text("").color(NamedTextColor.GRAY).append(description));
        }
        else {
            Component desc = Components.joinSpaces(Component.text(faction.getName() + ":"), description);
            desc = RelationHelper.formatComponent(this.player, faction, desc);

            if (mode == TerritorialTitleMode.ACTION_BAR) {
                Messaging.actionBar(this.player, desc);
            }
            else {
                Messaging.send(this.player, desc);
            }
        }
    }
}
