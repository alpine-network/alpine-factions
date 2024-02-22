package co.crystaldev.factions.command.argument;

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
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
public final class OfflinePlayerArgumentResolver extends ArgumentResolver<CommandSender, OfflinePlayer> {
    @Override
    protected ParseResult<OfflinePlayer> parse(Invocation<CommandSender> invocation, Argument<OfflinePlayer> context, String argument) {
        MessageConfig config = MessageConfig.getInstance();

        OfflinePlayer player = Bukkit.getOfflinePlayer(argument);
        if (player == null || !player.hasPlayedBefore()) {
            return ParseResult.failure(config.unknownPlayer.buildString("player_name", argument));
        }

        return ParseResult.success(player);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
