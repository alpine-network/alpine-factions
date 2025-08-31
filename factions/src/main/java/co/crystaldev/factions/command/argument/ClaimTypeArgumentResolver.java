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
import co.crystaldev.factions.util.claims.ClaimType;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

/**
 * @since 0.1.0
 */
final class ClaimTypeArgumentResolver extends AlpineArgumentResolver<ClaimType> {
    public ClaimTypeArgumentResolver() {
        super(ClaimType.class, null);
    }

    @Override
    protected ParseResult<ClaimType> parse(Invocation<CommandSender> invocation, Argument<ClaimType> context, String argument) {
        ClaimType type = ClaimType.get(argument);
        if (type == null) {
            type = ClaimType.SQUARE;
        }

        return ParseResult.success(type);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<ClaimType> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        return Stream.of(ClaimType.values())
                .map(v -> v.name().toLowerCase())
                .filter(v -> v.startsWith(current.toLowerCase()))
                .collect(SuggestionResult.collector());
    }
}
