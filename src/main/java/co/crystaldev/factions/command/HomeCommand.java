package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.TeleportManager;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/12/2024
 */
@Command(name = "factions home")
@Description("Warp to the faction home.")
public final class HomeCommand extends FactionsCommand {
    public HomeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction faction = targetFaction.orElse(Accessors.factions().findOrDefault(player));
        if (!faction.isPermitted(player, Permissions.ACCESS_HOME)) {
            FactionHelper.missingPermission(player, faction, "access home");
            return;
        }

        Location home = faction.getHome();
        if (home != null && !Accessors.claims().getFactionOrDefault(home).equals(faction)) {
            faction.setHome(null);
            FactionHelper.broadcast(faction, config.unsetHome.build());
            return;
        }

        if (home == null) {
            config.noHome.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName());
            return;
        }

        config.home.send(player,
                "faction", FactionHelper.formatRelational(player, faction, false),
                "faction_name", faction.getName(),
                "seconds", TeleportManager.getInstance().queueTeleport(player, home));
    }
}
