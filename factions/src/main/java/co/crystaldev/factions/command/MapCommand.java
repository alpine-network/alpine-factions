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
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.util.AsciiFactionMap;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions map", aliases = "factions ma")
@Description("Display the factions map.")
final class MapCommand extends AlpineCommand {
    public MapCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player sender) {
        Messaging.send(sender, AsciiFactionMap.create(sender, false));
    }

    @Execute(name = "on")
    public void on(@Context Player sender) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(sender);
        state.setAutoFactionMap(true);

        // send the status message
        this.plugin.getConfiguration(MessageConfig.class).stateChange.send(sender, "subject", "Faction Map", "state", true);

        // send the map to the player
        Messaging.send(sender, AsciiFactionMap.create(sender, true));
    }

    @Execute(name = "off")
    public void off(@Context Player sender) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(sender);
        state.setAutoFactionMap(false);

        // send the status message
        this.plugin.getConfiguration(MessageConfig.class).stateChange.send(sender, "subject", "Faction Map", "state", false);
    }
}
