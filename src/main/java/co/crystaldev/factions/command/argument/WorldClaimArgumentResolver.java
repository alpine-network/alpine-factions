package co.crystaldev.factions.command.argument;

import co.crystaldev.factions.util.claims.WorldClaimType;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
public final class WorldClaimArgumentResolver extends ArgumentResolver<CommandSender, WorldClaimType> {
    @Override
    protected ParseResult<WorldClaimType> parse(Invocation<CommandSender> invocation, Argument<WorldClaimType> context, String argument) {
        return ParseResult.success(argument.equalsIgnoreCase("all") ? WorldClaimType.ALL : WorldClaimType.WORLD);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<WorldClaimType> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        return SuggestionResult.of(Stream.of("all", "map")
                .filter(v -> v.startsWith(current.toLowerCase()))
                .collect(Collectors.toList()));
    }
}
