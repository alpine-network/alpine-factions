package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
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
final class SetPowerCommand extends FactionsCommand {
    public SetPowerCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("power") int power
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();

        Faction faction = Factions.get().getFactions().findOrDefault(other);

        PlayerAccessor players = Factions.get().getPlayers();
        FPlayer state = players.get(other);
        state.setPowerLevel(Math.max(0, Math.min(factionConfig.maxPlayerPower, power)));
        players.save(state);

        messageConfig.modifyPower.send(sender,
                "player", FactionHelper.formatRelational(sender, faction, other, false),
                "player_name", other.getName(),
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) state.getMaxPower()),
                "power", state.getEffectivePower(),
                "maxpower", state.getMaxPower());
    }
}
