package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
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
@Command(name = "factions powerboost")
@Description("Modify a player powerboost.")
final class PowerBoostCommand extends AlpineCommand {
    public PowerBoostCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("powerboost") int powerboost
    ) {
        Faction faction = Factions.get().factions().findOrDefault(other);

        PlayerAccessor players = Factions.get().players();
        FPlayer state = players.get(other);
        state.setPowerBoost(powerboost);
        players.save(state);

        this.plugin.getConfiguration(MessageConfig.class).modifyPowerBoost.send(sender,
                "player", FactionHelper.formatRelational(sender, faction, other, false),
                "player_name", other.getName(),
                "powerboost", powerboost,
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) state.getMaxPower()),
                "power", state.getEffectivePower(),
                "maxpower", state.getMaxPower());
    }
}
