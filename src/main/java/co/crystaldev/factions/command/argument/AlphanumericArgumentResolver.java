package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
final class AlphanumericArgumentResolver extends AlpineArgumentResolver<String> {
    public AlphanumericArgumentResolver() {
        super(String.class, Args.ALPHANUMERIC);
    }

    @Override
    protected ParseResult<String> parse(Invocation<CommandSender> invocation, Argument<String> context, String argument) {
        if (argument.matches("^[a-zA-Z0-9]+$"))
            return ParseResult.success(argument);
        return ParseResult.failure(MessageConfig.getInstance().alphanumeric.buildString(AlpineFactions.getInstance()));
    }
}
