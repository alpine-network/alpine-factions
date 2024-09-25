package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.Factions;
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
 * @author slides <puqqera@gmail.com>
 * @since 09/20/2024
 */
@Command(name = "factions location", aliases = { "factions loc", "factions coordinates", "factions coord" })
@Description("Send current location to faction members.")
final class LocationCommand extends FactionsCommand {
    public LocationCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction faction = Factions.get().factions().findOrDefault(player);

        if (!faction.isPermitted(player, Permissions.BROADCAST_LOCATION)) {
            FactionHelper.missingPermission(player, faction, "broadcast location");
            return;
        }

        Location location = player.getLocation();

        FactionHelper.broadcast(faction, observer -> {
            return config.locationBroadcast.build(
                    "player", FactionHelper.formatRelational(observer, faction, player, false),
                    "world", player.getWorld().getName(),
                    "x", location.getBlockX(),
                    "y", location.getBlockY(),
                    "z", location.getBlockZ());
        });
        // TODO: add alpine client waypoints
    }
}
