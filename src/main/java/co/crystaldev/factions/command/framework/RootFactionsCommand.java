package co.crystaldev.factions.command.framework;

import co.crystaldev.alpinecore.AlpinePlugin;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.permission.Permission;

/**
 * @since 0.1.0
 */
@Command(name = "factions", aliases = { "f", "faction" })
@Description("The base command for interacting with Factions")
@Permission("factions.command.factions")
final class RootFactionsCommand extends FactionsCommand {
    RootFactionsCommand(AlpinePlugin plugin) {
        super(plugin);
    }
}
