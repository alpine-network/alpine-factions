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
import co.crystaldev.factions.Constants;
import co.crystaldev.factions.util.ComponentHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
@Command(name = "factions version")
final class VersionCommand extends AlpineCommand {
    public VersionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Description("Display factions plugin versioning information")
    public void execute(@Context CommandSender sender) {
        String authors = String.join(", ", AlpineFactions.getInstance().getDescription().getAuthors());
        Messaging.send(sender, ComponentHelper.mini(String.join("<br>",
                "<info>.</info>",
                "<info>|</info> This server is running <b><gradient:#00aaaa:#78cccc:#00aaaa>AlpineFactions</gradient></b>",
                "<info>|</info> <emphasis>Version:</emphasis> v" + Constants.VERSION,
                "<info>|</info> <emphasis>Authors:</emphasis> " + authors,
                "<info>'</info>"
        )));
    }
}
