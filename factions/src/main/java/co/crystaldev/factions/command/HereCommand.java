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
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions here", aliases = { "factions h" })
@Description("Display information on a the faction who owns the chunk you're in.")
final class HereCommand extends AlpineCommand {
    public HereCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player sender) {
        Faction senderFaction = Factions.registry().findOrDefault(sender);
        Faction resolvedFaction = Factions.claims().getFactionOrDefault(sender.getLocation());
        Messaging.send(sender, AlpineFactions.getInstance().showFormatter().build(sender, resolvedFaction, senderFaction));
    }
}
