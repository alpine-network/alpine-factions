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
import co.crystaldev.factions.api.faction.Faction;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions show", aliases = { "factions faction", "factions who", "factions f" })
final class ShowCommand extends AlpineCommand {
    public ShowCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Description("Display information on a specific faction")
    public void execute(
            @Context CommandSender sender,
            @Arg("faction") Optional<Faction> faction
    ) {
        Faction senderFaction = Factions.registry().findOrDefault(sender);
        Faction resolvedFaction = faction.orElse(senderFaction);
        Messaging.send(sender, AlpineFactions.getInstance().showFormatter().build(sender, resolvedFaction, senderFaction));
    }
}
