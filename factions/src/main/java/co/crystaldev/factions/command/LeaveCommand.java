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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.1.0
 */
@Command(name = "factions leave")
@Description("Leave your faction.")
final class LeaveCommand extends AlpineCommand {

    private final Map<CommandSender, Long> confirmationMap = new HashMap<>();

    public LeaveCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = factions.find(sender);

        // ensure the player is in a faction
        if (faction == null) {
            config.notInFaction.send(sender);
            return;
        }

        // ensure that the player can leave
        if (faction.canDisband()) {
            if (faction.getMemberCount() > 1 && faction.getMemberRank(sender.getUniqueId()) == Rank.LEADER) {
                // the leader can't just leave the faction!
                config.promoteLeader.send(sender);
                return;
            }
            else if (faction.getMemberCount() == 1) {
                // the leader was the only member, disband

                // require a confirmation from the user
                if (!this.confirmationMap.containsKey(sender) || System.currentTimeMillis() - this.confirmationMap.get(sender) > 10_000L) {
                    this.confirmationMap.put(sender, System.currentTimeMillis());
                    config.confirm.send(sender);
                    return;
                }

                // member confirmed, disband the faction
                this.confirmationMap.remove(sender);
                faction.disband(sender);
                return;
            }
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(factions.getWilderness(), faction, sender));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // notify the faction
        FactionHelper.broadcast(faction, observer -> {
            if (observer.equals(sender)) {
                return null;
            }

            return config.memberLeave.build(
                    "player", RelationHelper.formatPlayerName(observer, sender),
                    "player_name", sender.getName());
        });

        // notify the player
        Messaging.send(sender, config.leave.build(
                "faction", RelationHelper.formatFactionName(sender, faction),
                "faction_name", faction.getName()
        ));

        // remove the member
        faction.removeMember(sender.getUniqueId());
    }
}
