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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions friendlyfire", aliases = { "factions friendlyfire", "factions fr" })
@Description("Toggle friendly fire.")
final class FriendlyFireCommand extends AlpineCommand {
    public FriendlyFireCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        PlayerAccessor players = Factions.players();
        players.wrap(sender, state -> {
            state.setFriendlyFire(!state.isFriendlyFire());

            config.stateChange.send(sender,
                    "subject", "Friendly Fire",
                    "state", state.isFriendlyFire());
        });
    }
}
