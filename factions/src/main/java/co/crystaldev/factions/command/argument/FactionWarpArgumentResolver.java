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
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.Warp;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
final class FactionWarpArgumentResolver extends AlpineArgumentResolver<Warp> {
    public FactionWarpArgumentResolver() {
        super(Warp.class, null);
    }

    @Override
    protected ParseResult<Warp> parse(Invocation<CommandSender> invocation, Argument<Warp> context, String argument) {
        Faction faction = Factions.registry().findOrDefault(invocation.sender());

        Warp argWarp = faction.getWarpByName(argument);

        if (argWarp != null) {
            for (Warp warp : faction.getWarps()) {
                if (argument.matches(warp.getName())) {
                    return ParseResult.success(warp);
                }
            }
        }

        return ParseResult.failure(AlpineFactions.getInstance().getConfiguration(MessageConfig.class).noWarp.buildString(
                "faction", faction.getName(),
                "warp", argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Warp> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        Faction faction = Factions.registry().findOrDefault(invocation.sender());
        return faction.getWarps()
                .stream()
                .map(Warp::getName)
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }

}
