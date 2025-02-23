package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
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
final class FactionWarpArgumentResolver extends AlpineArgumentResolver<String> {
    public FactionWarpArgumentResolver() {
        super(String.class, Args.WARP);
    }

    @Override
    protected ParseResult<String> parse(Invocation<CommandSender> invocation, Argument<String> context, String argument) {
        Faction faction = Factions.get().factions().findOrDefault(invocation.sender());

        for (String warp : faction.getWarps()) {
            if (argument.matches(warp)) {
                return ParseResult.success(warp);
            }
        }

        return ParseResult.failure(AlpineFactions.getInstance().getConfiguration(MessageConfig.class).noWarp.buildString(
                "faction", faction.getName(),
                "warp", argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<String> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel();
        Faction faction = Factions.get().factions().findOrDefault(invocation.sender());
        return faction.getWarps().stream()
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }

}
