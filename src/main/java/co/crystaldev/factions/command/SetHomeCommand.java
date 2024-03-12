package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    public void execute(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        Location location = player.getLocation();
        Faction faction = Accessors.claims().getFactionOrDefault(location);
        if (faction.isWilderness() || !faction.isMember(player.getUniqueId())) {
            Faction selfFaction = Accessors.factions().findOrDefault(player);
            config.outsideTerritory.send(player,
                    "faction", FactionHelper.formatRelational(player, selfFaction, false),
                    "faction_name", faction.getName());
            return;
        }

        if (!faction.isPermitted(player, Permissions.MODIFY_HOME)) {
            FactionHelper.missingPermission(player, faction, "modify home");
            return;
        }

        faction.setHome(location);

        FactionHelper.broadcast(faction, observer -> {
            return config.setHome.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),
                    "world", player.getWorld().getName(),
                    "x", location.getBlockX(),
                    "y", location.getBlockY(),
                    "z", location.getBlockZ());
        });
    }
}
