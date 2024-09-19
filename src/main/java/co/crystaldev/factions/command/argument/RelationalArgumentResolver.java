package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.config.MessageConfig;
import com.google.common.collect.ImmutableSet;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.Set;

/**
 * @since 0.1.0
 */
final class RelationalArgumentResolver extends AlpineArgumentResolver<Relational> {

    private static final Set<Relational> RELATIONALS = ImmutableSet.<Relational>builder()
            .add(Rank.values())
            .add(FactionRelation.values())
            .build();

    public RelationalArgumentResolver() {
        super(Relational.class, Args.RELATIONAL);
    }

    @Override
    protected ParseResult<Relational> parse(Invocation<CommandSender> invocation, Argument<Relational> context, String argument) {
        for (Relational relational : RELATIONALS) {
            if (relational.isValid() && relational.getId().equalsIgnoreCase(argument)) {
                return ParseResult.success(relational);
            }
        }

        return ParseResult.failure(AlpineFactions.getInstance().getConfiguration(MessageConfig.class).unknownRelational.buildString("value", argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Relational> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return RELATIONALS.stream()
                .filter(Relational::isValid)
                .map(Relational::getId)
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
