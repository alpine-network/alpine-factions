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
import co.crystaldev.factions.api.event.FactionDescriptionUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Command(name = "factions description", aliases = "factions desc")
@Description("Modify your faction's description.")
final class DescriptionCommand extends AlpineCommand {
    public DescriptionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Join("name") String description
    ) {
        set(Factions.registry().findOrDefault(sender), sender, Component.text(description));
    }

    @Execute(name = "clear")
    public void clear(@Context CommandSender sender) {
        set(Factions.registry().findOrDefault(sender), sender, null);
    }

    private static void set(@NotNull Faction faction, @NotNull CommandSender sender, @Nullable Component description) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);

        // ensure the user has permission
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_DESCRIPTION, "modify description");
        if (!permitted) {
            return;
        }

        // call event
        FactionDescriptionUpdateEvent event = AlpineFactions.callEvent(new FactionDescriptionUpdateEvent(faction, sender, description));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // change the faction description
        Component newDescription = event.getDescription();
        faction.setDescription(newDescription);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.description.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "description", newDescription == null ? Faction.DEFAULT_DESCRIPTION : newDescription);
        });
    }
}
