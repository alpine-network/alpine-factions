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
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.event.FactionHomeUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions sethome")
@Description("Set the faction home to your current location.")
final class SetHomeCommand extends AlpineCommand {
    public SetHomeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Location location = sender.getLocation();
        Faction faction = Factions.claims().getFactionOrDefault(location);
        Faction selfFaction = Factions.registry().findOrDefault(sender);
        if (faction.isWilderness() || !faction.isMember(sender.getUniqueId())) {
            config.outsideTerritory.send(sender,
                    "faction", RelationHelper.formatLiteralFactionName(sender, selfFaction),
                    "faction_name", faction.getName());
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_HOME, "modify home");
        if (!permitted) {
            return;
        }

        FactionHomeUpdateEvent event = AlpineFactions.callEvent(new FactionHomeUpdateEvent(selfFaction, sender, location));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        Location newLocation = event.getLocation();
        faction.setHome(newLocation);

        // event unset the location, do not notify
        if (newLocation == null) {
            return;
        }

        FactionHelper.broadcast(faction, observer -> {
            return config.setHome.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", sender.getName(),
                    "world", sender.getWorld().getName(),
                    "x", newLocation.getBlockX(),
                    "y", newLocation.getBlockY(),
                    "z", newLocation.getBlockZ());
        });
    }
}
