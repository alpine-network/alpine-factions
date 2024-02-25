package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/20/2024
 */
@SuppressWarnings("rawtypes")
public final class LowercaseEnumArgumentResolver extends AlpineArgumentResolver<Enum> {

    private final Map<Class<Enum>, SuggestionResult> cachedEnumSuggestions = new ConcurrentHashMap<>();

    public LowercaseEnumArgumentResolver() {
        super(Enum.class, Args.LC_ENUM);
    }

    @Override
    protected ParseResult<Enum> parse(Invocation<CommandSender> invocation, Argument<Enum> context, String argument) {
        Class<Enum> enumClass = context.getWrapperFormat().getParsedType();

        try {
            return ParseResult.success(getEnum(enumClass, argument.toUpperCase()));
        }
        catch (IllegalArgumentException ignored) {
            return ParseResult.failure(InvalidUsage.Cause.INVALID_ARGUMENT);
        }
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Enum> argument, SuggestionContext context) {
        Class<Enum> enumClass = argument.getWrapperFormat().getParsedType();

        return cachedEnumSuggestions.computeIfAbsent(enumClass, key -> {
            Enum[] enums = enumClass.getEnumConstants();
            if (enums == null || enums.length == 0) {
                return SuggestionResult.empty();
            }

            return Arrays.stream(enums)
                    .map(anEnum -> anEnum.name().toLowerCase())
                    .collect(SuggestionResult.collector());
        });
    }

    @SuppressWarnings("unchecked")
    private Enum getEnum(Class<? extends Enum> clazz, String name) throws IllegalArgumentException {
        return Enum.valueOf(clazz, name);
    }
}
