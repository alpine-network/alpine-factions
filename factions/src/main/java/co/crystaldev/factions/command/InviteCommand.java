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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.MemberInvitation;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

/**
 * @since 0.1.0
 */
@Command(name = "factions invite", aliases = "factions inv")
final class InviteCommand extends AlpineCommand {
    public InviteCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "add")
    @Description("Add a faction membership invitation")
    public void add(
            @Context Player sender,
            @Arg("player") @Async OfflinePlayer invitee
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = factions.findOrDefault(sender);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.INVITE_MEMBERS, "invite members");
        if (!permitted) {
            return;
        }

        // you can't invite an existing member!
        if (faction.isMember(invitee.getUniqueId())) {
            config.inviteFail.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, invitee),
                    "player_name", invitee.getName(),
                    "faction", RelationHelper.formatFactionName(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // invite the member
        boolean previouslyInvited = faction.isInvited(invitee.getUniqueId());
        faction.addInvitation(invitee.getUniqueId(), sender.getUniqueId());

        // notify the faction
        FactionHelper.broadcast(faction, observer -> {
            if (previouslyInvited && !sender.equals(observer)) {
                // if the member was already invited, do not send broadcast to the
                // rest of the faction, only to the sender
                return null;
            }

            return config.invite.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", sender.getName(),

                    "invitee", RelationHelper.formatPlayerName(observer, invitee),
                    "invitee_name", invitee.getName()
            );
        });

        // notify the invitee
        Messaging.attemptSend(invitee, config.invited.build(
                "actor", RelationHelper.formatPlayerName(invitee, sender),
                "actor_name", sender.getName(),

                "faction", RelationHelper.formatLiteralFactionName(invitee, faction),
                "faction_name", faction.getName()
        ));
    }

    @Execute(name = "remove")
    @Description("Remove a faction membership invitation")
    public void remove(
            @Context Player sender,
            @Arg("player") @Async OfflinePlayer invitee
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = factions.findOrDefault(sender);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.INVITE_MEMBERS, "invite members");
        if (!permitted) {
            return;
        }

        // remove the invite
        faction.removeInvitation(invitee.getUniqueId());

        // notify the faction
        FactionHelper.broadcast(faction, observer -> {
            return config.inviteRevoke.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", sender.getName(),

                    "invitee", RelationHelper.formatPlayerName(observer, invitee),
                    "invitee_name", invitee.getName()
            );
        });
    }

    @Execute(name = "list")
    @Description("View a list of outgoing faction membership invitations")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction resolvedFaction = faction.orElse(factions.findOrDefault(sender));
        Set<MemberInvitation> invitations = resolvedFaction.getInvitations();

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, resolvedFaction,
                Permissions.INVITE_MEMBERS, "invite members");
        if (!permitted) {
            return;
        }

        Component title = config.inviteListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, resolvedFaction),
                "faction_name", resolvedFaction.getName()
        );
        String command = "/f invite list %page% " + resolvedFaction.getName();
        Messaging.send(sender, Formatting.page(title, invitations, command, page.orElse(1), 10, invite -> {
            OfflinePlayer invitee = Bukkit.getOfflinePlayer(invite.getPlayerId());
            OfflinePlayer inviter = Bukkit.getOfflinePlayer(invite.getInviterId());

            return config.inviteListEntry.build(
                    "player", RelationHelper.formatPlayerName(sender, invitee),
                    "player_name", invitee.getName(),

                    "inviter", RelationHelper.formatPlayerName(sender, inviter),
                    "inviter_name", inviter.getName()
            );
        }));
    }
}
