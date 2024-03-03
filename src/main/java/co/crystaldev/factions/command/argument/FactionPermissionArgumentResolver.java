package co.crystaldev.factions.command.argument;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/17/2024
 */
public final class FactionPermissionArgumentResolver extends AlpineArgumentResolver<Permission> {
    public FactionPermissionArgumentResolver() {
        super(Permission.class, Args.FACTION_PERMISSION);
    }

    @Override
    protected ParseResult<Permission> parse(Invocation<CommandSender> invocation, Argument<Permission> context, String argument) {
        PermissionRegistry registry = AlpineFactions.getInstance().getPermissionRegistry();
        List<Permission> permissions = registry.getAll();

        for (Permission permission : permissions) {
            if (permission.getId().equalsIgnoreCase(argument)) {
                return ParseResult.success(permission);
            }
        }

        return ParseResult.failure(MessageConfig.getInstance().unknownPermission.buildString("value", argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Permission> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        PermissionRegistry registry = AlpineFactions.getInstance().getPermissionRegistry();
        List<Permission> permissions = registry.getAll();

        return permissions.stream()
                .map(Permission::getId)
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
