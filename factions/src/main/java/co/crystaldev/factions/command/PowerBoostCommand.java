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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
@Command(name = "factions powerboost")
final class PowerBoostCommand extends AlpineCommand {
    public PowerBoostCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    @Description("Set a player's powerboost")
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("powerboost") int powerboost
    ) {
        Faction faction = Factions.registry().findOrDefault(other);

        PlayerAccessor players = Factions.players();
        FPlayer state = players.get(other);
        state.setPowerBoost(powerboost);
        players.save(state);

        this.plugin.getConfiguration(MessageConfig.class).modifyPowerBoost.send(sender,
                "player", RelationHelper.formatLiteralPlayerName(sender, other),
                "player_name", other.getName(),
                "powerboost", powerboost,
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) state.getMaxPower()),
                "power", state.getEffectivePower(),
                "maxpower", state.getMaxPower());
    }
}
