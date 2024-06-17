package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.api.faction.FactionRelation;
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
final class FactionRelationArgumentResolver extends AlpineArgumentResolver<FactionRelation> {
    public FactionRelationArgumentResolver() {
        super(FactionRelation.class, Args.FACTION_RELATION);
    }

    @Override
    protected ParseResult<FactionRelation> parse(Invocation<CommandSender> invocation, Argument<FactionRelation> context, String argument) {
        try {
            return ParseResult.success(FactionRelation.valueOf(argument.toUpperCase()));
        }
        catch (IllegalArgumentException ignored) {
            return ParseResult.failure(InvalidUsage.Cause.INVALID_ARGUMENT);
        }
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<FactionRelation> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        return Stream.of(FactionRelation.NEUTRAL, FactionRelation.ENEMY, FactionRelation.TRUCE, FactionRelation.ALLY)
                .map(v -> v.name().toLowerCase())
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
