/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions roster")
@Description("Manage the faction roster.")
final class RosterCommand extends AlpineCommand implements Initializable {
    public RosterCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean init() {
        return this.plugin.getConfiguration(FactionConfig.class).rosterEnabled;
    }

    @Execute(name = "add")
    public void add(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("rank") Optional<Rank> rank,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction senderFaction = Factions.registry().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_ROSTER, "manage roster");
        if (!permitted) {
            return;
        }

        // ensure the roster isn't full
        if (faction.getRosterCount() >= faction.getRosterLimit()) {
            config.rosterFull.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player isn't on the roster
        if (faction.isOnRoster(other.getUniqueId())) {
            config.alreadyOnRoster.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to add this rank
        Rank parsedRank = rank.orElse(Rank.getDefault());
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && parsedRank.isSuperiorOrMatching(faction.getMemberRankOrDefault(PlayerHelper.getId(sender)))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // add to the roster
        faction.addMemberToRoster(other.getUniqueId(), parsedRank);

        // notify faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rosterAdd.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", RelationHelper.formatPlayerName(observer, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(observer, faction),
                    "faction_name", faction.getName(),
                    "rank", parsedRank.getId()
            );
        });

        // notify enlistee
        Messaging.attemptSend(other, config.rosterAdded.build(
                "faction", RelationHelper.formatLiteralFactionName(other, faction),
                "faction_name", faction.getName(),
                "actor", RelationHelper.formatPlayerName(other, sender),
                "actor_name", PlayerHelper.getName(sender)
        ));
    }

    @Execute(name = "remove")
    public void remove(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction senderFaction = Factions.registry().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_ROSTER, "manage roster");
        if (!permitted) {
            return;
        }

        // ensure the player isn't on the roster
        if (!faction.isOnRoster(other.getUniqueId())) {
            config.notOnRoster.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to modify this member
        Rank parsedRank = faction.getMemberRank(other.getUniqueId());
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && parsedRank.isSuperiorOrMatching(faction.getMemberRankOrDefault(PlayerHelper.getId(sender)))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // notify faction
        FactionHelper.broadcast(faction, sender, observer -> {
            if (observer.getUniqueId().equals(other.getUniqueId())) {
                return null;
            }

            return config.rosterRemove.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", RelationHelper.formatPlayerName(observer, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(observer, faction),
                    "faction_name", faction.getName(),
                    "rank", parsedRank.getId()
            );
        });

        // remove from the roster
        faction.removeMemberFromRoster(other.getUniqueId());

        // notify enlistee
        Messaging.attemptSend(other, config.rosterRemoved.build(
                "faction", RelationHelper.formatLiteralFactionName(other, faction),
                "faction_name", faction.getName(),
                "actor", RelationHelper.formatPlayerName(other, sender),
                "actor_name", PlayerHelper.getName(sender)
        ));
    }

    @Execute(name = "setrank")
    public void setRank(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("rank") Rank rank,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction senderFaction = Factions.registry().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_ROSTER, "manage roster");
        if (!permitted) {
            return;
        }

        // ensure the player isn't on the roster
        if (!faction.isOnRoster(other.getUniqueId())) {
            config.notOnRoster.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", RelationHelper.formatFactionName(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to set this rank
        Rank parsedRank = faction.getMemberRank(other.getUniqueId());
        Rank senderRank = faction.getMemberRankOrDefault(PlayerHelper.getId(sender));
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && (parsedRank.isSuperiorOrMatching(senderRank) || rank.isSuperiorOrMatching(senderRank))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // set the rank
        faction.setRosterRank(other.getUniqueId(), rank);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rosterSetRank.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", RelationHelper.formatPlayerName(observer, other),
                    "player_name", PlayerHelper.getName(other),
                    "rank", rank.getId()
            );
        });
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = targetFaction.orElse(factions.findOrDefault(sender));

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_ROSTER, "manage roster");
        if (!permitted) {
            return;
        }

        Component title = config.rosterListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, faction),
                "faction_name", faction.getName()
        );
        String command = "/f roster list %page% " + faction.getName();
        Messaging.send(sender, Formatting.page(title, faction.getRoster(), command, page.orElse(1), 10, member -> {
            OfflinePlayer player = member.getOfflinePlayer();
            return config.rosterListEntry.build(
                    "player", RelationHelper.formatLiteralPlayerName(sender, player),
                    "player_name", player.getName(),
                    "rank", member.getRank().getId()
            );
        }));
    }
}
