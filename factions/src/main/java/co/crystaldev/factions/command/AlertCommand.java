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
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import com.cryptomorin.xseries.XSound;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions alert")
@Description("Notify the faction.")
final class AlertCommand extends AlpineCommand {
    public AlertCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player sender,
            @Join("message") String message
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.registry().findOrDefault(sender);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.ALERT, "alert");
        if (!permitted) {
            return;
        }

        Component parsedAlert = ComponentHelper.legacy(message);

        // broadcast chat message
        FactionHelper.broadcast(faction, observer -> {
            XSound.ENTITY_PLAYER_LEVELUP.play(observer);

            return config.alertMessage.build(
                    "actor", RelationHelper.formatLiteralPlayerName(observer, sender),
                    "actor_name", sender.getName(),
                    "alert", parsedAlert
            );
        });

        // broadcast title
        Component title = config.alertTitle.build("alert", parsedAlert);
        for (Member member : faction.getMembers()) {
            Player observer = member.getPlayer();
            if (observer != null) {
                Component subtitle = config.alertSubtitle.build(
                        "actor", RelationHelper.formatLiteralPlayerName(observer, sender),
                        "actor_name", sender.getName(),
                        "alert", parsedAlert
                );
                Messaging.title(observer, title, subtitle);
            }
        }
    }
}
