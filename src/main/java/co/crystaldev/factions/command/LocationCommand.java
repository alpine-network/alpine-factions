package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.api.Factions;
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
 * @author slides <puqqera@gmail.com>
 * @since 09/20/2024
 */
@Command(name = "factions location", aliases = { "factions loc", "factions coordinates", "factions coord" })
@Description("Send current location to faction members.")
final class LocationCommand extends AlpineCommand {
    public LocationCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction faction = Factions.get().factions().findOrDefault(player);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, faction,
                Permissions.BROADCAST_LOCATION, "broadcast location");
        if (!permitted) {
            return;
        }

        Location location = player.getLocation();

        FactionHelper.broadcast(faction, observer -> {
            return config.locationBroadcast.build(
                    "player", RelationHelper.formatLiteralPlayerName(observer, player),
                    "world", player.getWorld().getName(),
                    "x", location.getBlockX(),
                    "y", location.getBlockY(),
                    "z", location.getBlockZ());
        });
        // TODO: add alpine client waypoints
    }
}
