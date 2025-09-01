/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @since 0.1.0
 */
final class FactionArgumentResolver extends AlpineArgumentResolver<Faction> {
    public FactionArgumentResolver() {
        super(Faction.class, null);
    }

    @Override
    protected ParseResult<Faction> parse(Invocation<CommandSender> invocation, Argument<Faction> context, String argument) {
        FactionAccessor factions = Factions.registry();
        boolean foundPlayer = false;

        // attempt to find faction by its name
        Faction faction = factions.getByName(argument);

        // faction was not found, attempt to find a fac with the given player
        if (faction == null && argument.matches("^[a-zA-Z0-9_]{2,16}$")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(argument);
            if (player != null) {
                foundPlayer = player.hasPlayedBefore();
                faction = factions.find(player);
            }
        }

        // faction was not found
        if (faction == null && !foundPlayer) {
            return ParseResult.failure(AlpineFactions.getInstance().getConfiguration(MessageConfig.class).unknownFaction.buildString("value", argument));
        }

        return ParseResult.success(Optional.ofNullable(faction).orElse(factions.getWilderness()));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Faction> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();

        return Factions.registry().all()
                .stream()
                .map(Faction::getName)
                .filter(v -> v.toLowerCase().startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
