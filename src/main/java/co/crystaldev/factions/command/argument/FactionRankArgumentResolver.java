package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.api.faction.member.Rank;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

/**
 * @since 0.1.0
 */
final class FactionRankArgumentResolver extends AlpineArgumentResolver<Rank> {
    public FactionRankArgumentResolver() {
        super(Rank.class, Args.FACTION_RANK);
    }

    @Override
    protected ParseResult<Rank> parse(Invocation<CommandSender> invocation, Argument<Rank> context, String argument) {
        try {
            return ParseResult.success(Rank.valueOf(argument.toUpperCase()));
        }
        catch (IllegalArgumentException ignored) {
            return ParseResult.failure(InvalidUsage.Cause.INVALID_ARGUMENT);
        }
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Rank> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        return Stream.of(Rank.RECRUIT, Rank.MEMBER, Rank.OFFICER, Rank.COLEADER, Rank.LEADER)
                .map(v -> v.name().toLowerCase())
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
