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
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions motd")
final class MOTDCommand extends AlpineCommand {
    public MOTDCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Description("Set the faction's message of the day")
    public void set(
            @Context Player sender,
            @Join("name") String motd
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.registry().findOrDefault(sender);

        // ensure the user has permission
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_MOTD, "modify motd");
        if (!permitted) {
            return;
        }

        // set the motd
        Component formatted = ComponentHelper.legacy(motd);
        faction.setMotd(formatted);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.motd.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", sender.getName(),
                    "motd", formatted);
        });
    }

    @Execute
    @Description("View the faction's message of the day")
    public void view(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.registry().findOrDefault(sender);

        Component motd = Optional.ofNullable(faction.getMotd()).orElse(Faction.DEFAULT_MOTD);
        Component title = config.motdTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, faction),
                "faction_name", faction.getName());

        Messaging.send(sender, Components.joinNewLines(Formatting.title(title), motd));
    }

    @Execute(name = "clear")
    @Description("Clear the faction's message of the day")
    public void clear(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.registry().findOrDefault(sender);

        // ensure the user has permission
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_MOTD, "modify motd");
        if (!permitted) {
            return;
        }

        // remove the motd
        faction.setMotd(null);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.motd.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", sender.getName(),
                    "motd", Faction.DEFAULT_MOTD);
        });
    }
}
