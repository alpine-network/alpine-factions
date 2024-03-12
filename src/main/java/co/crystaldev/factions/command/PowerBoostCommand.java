package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.accessor.Accessors;
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
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/11/2024
 */
@Command(name = "factions powerboost")
@Description("Modify a player powerboost.")
public final class PowerBoostCommand extends FactionsCommand {
    public PowerBoostCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("powerboost") int powerboost
    ) {
        Faction faction = Accessors.factions().findOrDefault(other);

        PlayerAccessor players = Accessors.players();
        FPlayer state = players.get(other);
        state.setPowerBoost(powerboost);
        players.save(state);

        MessageConfig.getInstance().modifyPowerBoost.send(sender,
                "player", FactionHelper.formatRelational(sender, faction, other, false),
                "player_name", other.getName(),
                "powerboost", powerboost,
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) state.getMaxPower()),
                "power", state.getEffectivePower(),
                "maxpower", state.getMaxPower());
    }
}
