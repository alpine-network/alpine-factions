package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.event.FactionHomeUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions sethome")
@Description("Set the faction home to your current location.")
final class SetHomeCommand extends AlpineCommand {
    public SetHomeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Location location = player.getLocation();
        Faction faction = Factions.get().claims().getFactionOrDefault(location);
        Faction selfFaction = Factions.get().factions().findOrDefault(player);
        if (faction.isWilderness() || !faction.isMember(player.getUniqueId())) {
            config.outsideTerritory.send(player,
                    "faction", RelationHelper.formatLiteralFactionName(player, selfFaction),
                    "faction_name", faction.getName());
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, faction,
                Permissions.MODIFY_HOME, "modify home");
        if (!permitted) {
            return;
        }

        FactionHomeUpdateEvent event = AlpineFactions.callEvent(new FactionHomeUpdateEvent(selfFaction, player, location));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        Location newLocation = event.getLocation();
        faction.setHome(newLocation);

        // event unset the location, do not notify
        if (newLocation == null) {
            return;
        }

        FactionHelper.broadcast(faction, observer -> {
            return config.setHome.build(
                    "actor", RelationHelper.formatPlayerName(observer, player),
                    "actor_name", player.getName(),
                    "world", player.getWorld().getName(),
                    "x", newLocation.getBlockX(),
                    "y", newLocation.getBlockY(),
                    "z", newLocation.getBlockZ());
        });
    }
}
