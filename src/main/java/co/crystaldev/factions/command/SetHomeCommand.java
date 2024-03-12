package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/11/2024
 */
@Command(name = "factions sethome")
@Description("Set the faction home to your current location.")
public final class SetHomeCommand extends FactionsCommand {
    public SetHomeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction faction = targetFaction.orElse(Accessors.factions().findOrDefault(player));
        if (!faction.isPermitted(player, Permissions.MODIFY_HOME)) {
            FactionHelper.missingPermission(player, faction, "modify home");
            return;
        }

        
    }
}
