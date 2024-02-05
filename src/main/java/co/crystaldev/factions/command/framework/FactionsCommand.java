package co.crystaldev.factions.command.framework;

import co.crystaldev.alpinecore.AlpinePlugin;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.permission.Permission;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions", aliases = { "f", "faction" })
@Description("The base command for interacting with Factions")
@Permission("factions.command.factions")
final class FactionsCommand extends BaseFactionsCommand {
    FactionsCommand(AlpinePlugin plugin) {
        super(plugin);
    }
}
