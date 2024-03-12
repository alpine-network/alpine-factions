package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
@Command(name = "factions override", aliases = "factions admin")
@Description("Override factions permission checks.")
public final class OverrideCommand extends FactionsCommand {
    public OverrideCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(@Context Player player) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        state.setOverriding(!state.isOverriding());
        MessageConfig.getInstance().stateChange.send(player, "subject", "Override Mode", "state", state.isOverriding());
    }
}
