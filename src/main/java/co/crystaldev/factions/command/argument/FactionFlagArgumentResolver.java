package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @since 0.1.0
 */
final class FactionFlagArgumentResolver extends AlpineArgumentResolver<FactionFlag> {
    public FactionFlagArgumentResolver() {
        super(FactionFlag.class, Args.FACTION_FLAG);
    }

    @Override
    protected ParseResult<FactionFlag> parse(Invocation<CommandSender> invocation, Argument<FactionFlag> context, String argument) {
        FlagRegistry registry = AlpineFactions.getInstance().getFlagRegistry();
        List<FactionFlag<?>> flags = registry.getAll(invocation.sender());

        for (FactionFlag<?> flag : flags) {
            if (flag.getId().equalsIgnoreCase(argument) && flag.isPermitted(invocation.sender())) {
                return ParseResult.success(flag);
            }
        }

        return ParseResult.failure(MessageConfig.getInstance().unknownFlag.buildString("value", argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<FactionFlag> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        FlagRegistry registry = AlpineFactions.getInstance().getFlagRegistry();
        List<FactionFlag<?>> flags = registry.getAll(invocation.sender());

        return flags.stream()
                .filter(flag -> flag.isPermitted(invocation.sender()))
                .map(FactionFlag::getId)
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
