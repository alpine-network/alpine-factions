package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.util.claims.WorldClaimType;
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
final class WorldClaimTypeArgumentResolver extends AlpineArgumentResolver<WorldClaimType> {

    public WorldClaimTypeArgumentResolver() {
        super(WorldClaimType.class, Args.WORLD_CLAIM_TYPE);
    }

    @Override
    protected ParseResult<WorldClaimType> parse(Invocation<CommandSender> invocation, Argument<WorldClaimType> context, String argument) {
        return ParseResult.success(argument.equalsIgnoreCase("all") ? WorldClaimType.ALL : WorldClaimType.WORLD);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<WorldClaimType> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        return Stream.of("all", "map")
                .filter(v -> v.startsWith(current.toLowerCase()))
                .collect(SuggestionResult.collector());
    }
}
