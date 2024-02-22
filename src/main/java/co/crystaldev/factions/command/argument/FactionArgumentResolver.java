package co.crystaldev.factions.command.argument;

import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
public final class FactionArgumentResolver extends ArgumentResolver<CommandSender, Faction> {
    @Override
    protected ParseResult<Faction> parse(Invocation<CommandSender> invocation, Argument<Faction> context, String argument) {
        FactionAccessor factions = Accessors.factions();
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
            return ParseResult.failure(MessageConfig.getInstance().unknownFaction.buildString("value", argument));
        }

        return ParseResult.success(Optional.ofNullable(faction).orElse(factions.getWilderness()));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Faction> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return Accessors.factions().get()
                .stream()
                .map(Faction::getName)
                .filter(v -> v.toLowerCase().startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
