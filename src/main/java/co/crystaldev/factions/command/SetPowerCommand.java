package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
@Command(name = "factions setpower")
@Description("Modify a player power.")
final class SetPowerCommand extends AlpineCommand {
    public SetPowerCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("power") int power
    ) {
        MessageConfig messageConfig = this.plugin.getConfiguration(MessageConfig.class);
        FactionConfig factionConfig = this.plugin.getConfiguration(FactionConfig.class);

        PlayerAccessor players = Factions.players();
        FPlayer state = players.get(other);
        state.setPowerLevel(Math.max(0, Math.min(factionConfig.maxPlayerPower, power)));
        players.save(state);

        messageConfig.modifyPower.send(sender,
                "player", RelationHelper.formatLiteralFactionName(sender, other),
                "player_name", other.getName(),
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) state.getMaxPower()),
                "power", state.getEffectivePower(),
                "maxpower", state.getMaxPower());
    }
}
