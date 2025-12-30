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
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions override", aliases = "factions admin")
@Description("Override factions permission checks.")
final class OverrideCommand extends AlpineCommand {
    public OverrideCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(@Context Player sender) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(sender);
        state.setOverriding(!state.isOverriding());
        this.plugin.getConfiguration(MessageConfig.class).stateChange.send(sender, "subject", "Override Mode", "state", state.isOverriding());
    }
}
