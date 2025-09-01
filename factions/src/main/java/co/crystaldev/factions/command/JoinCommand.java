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
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions join")
@Description("Join a faction.")
final class JoinCommand extends AlpineCommand {
    public JoinCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("faction") Faction faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        boolean overriding = PlayerHandler.getInstance().isOverriding(player);

        FactionAccessor factions = Factions.registry();
        Faction otherFaction = factions.findOrDefault(player);

        // ensure the player is not in a faction
        if (!otherFaction.isWilderness()) {
            config.alreadyInFaction.send(player);
            return;
        }

        // ensure the player is even invited
        if (!overriding && !faction.canJoin(player.getUniqueId())) {
            config.notInvited.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName());

            // notify the faction of this attempt
            FactionHelper.broadcast(faction, observer -> config.attemptedMemberJoin.build(
                    "player", RelationHelper.formatPlayerName(observer, player),
                    "player_name", player.getName()
            ));
            return;
        }

        // ensure the faction is not full
        if (!overriding && faction.getMemberCount() >= faction.getMemberLimit()) {
            config.fullFaction.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName(),
                    "limit", faction.getMemberLimit());
            return;
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(faction, otherFaction, player));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // add the member
        faction.addMember(player.getUniqueId());

        // notify the faction
        FactionHelper.broadcast(faction, observer -> {
            if (observer.equals(player)) {
                return null;
            }

            return config.memberJoin.build(
                    "player", RelationHelper.formatPlayerName(observer, player),
                    "player_name", player.getName());
        });

        // notify the new member
        Messaging.send(player, config.join.build(
                "faction", RelationHelper.formatLiteralFactionName(player, faction),
                "faction_name", faction.getName()
        ));
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context Player player,
            @Arg("faction") Faction faction,
            @Arg("player") @Async OfflinePlayer joiningPlayer
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        FactionAccessor factions = Factions.registry();
        Faction otherFaction = factions.find(joiningPlayer);
        Faction actingFaction = factions.findOrDefault(player);
        Faction wilderness = factions.getWilderness();

        // require player to be overriding
        if (!PlayerHandler.getInstance().isOverriding(player)) {
            return;
        }

        // ensure the player is not in a faction
        if (otherFaction != null) {
            config.playerAlreadyInFaction.send(player,
                    "player", RelationHelper.formatPlayerName(player, joiningPlayer),
                    "player_name", joiningPlayer.getName());
            return;
        }

        // ensure the player is even invited
        if (!faction.canJoin(joiningPlayer.getUniqueId())) {
            config.playerNotInvited.send(player,
                    "player", RelationHelper.formatPlayerName(player, joiningPlayer),
                    "player_name", joiningPlayer.getName(),

                    "faction", RelationHelper.formatLiteralFactionName(player, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the faction is not full
        if (faction.getMemberCount() >= faction.getMemberLimit()) {
            config.fullFaction.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName(),
                    "limit", faction.getMemberLimit());
            return;
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(faction, factions.getWilderness(), player));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // add the member
        faction.addMember(joiningPlayer.getUniqueId());

        // notify the faction
        FactionHelper.broadcast(faction, player, observer -> {
            if (observer.getUniqueId().equals(joiningPlayer.getUniqueId())) {
                return null;
            }

            return config.memberForceJoin.build(
                    "inviter", RelationHelper.formatPlayerName(observer, player),
                    "inviter_name", player.getName(),

                    "player", RelationHelper.formatPlayerName(observer, joiningPlayer),
                    "player_name", joiningPlayer.getName());
        });

        // notify the new member
        Messaging.attemptSend(joiningPlayer, config.forceJoin.build(
                "actor", RelationHelper.formatPlayerName(joiningPlayer, player),
                "actor_name", player.getName(),

                "faction", RelationHelper.formatLiteralFactionName(joiningPlayer, faction),
                "faction_name", faction.getName()
        ));
    }
}
