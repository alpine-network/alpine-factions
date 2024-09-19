package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.teleport.TeleportTask;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author slides <puqqera@gmail.com>
 * @since 09/19/2024
 */
@Command(name = "wilderness", aliases = "wild")
@Description("Teleports player to a random location.")
@Permission("alpinefactions.command.wilderness")
public final class WildernessCommand extends AlpineCommand {
    WildernessCommand(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();
        FactionConfig factionConfig = this.plugin.getConfiguration(FactionConfig.class);
        ClaimAccessor claims = Factions.get().claims();

        // Get a random location
        Location location = getRandomLocation(player.getWorld(), factionConfig.maxWildDistance, factionConfig.maxWildDistance);

        // Keep getting a location until it is valid
        int maxTries = 50;
        int tries = 0;
        while (claims.getClaim(location.getChunk()) != null && maxTries > tries) {
            location = getRandomLocation(player.getWorld(), factionConfig.maxWildDistance, factionConfig.maxWildDistance);
            tries++;
        }

        // If no location can be found, notify player and return
        if (maxTries <= tries) {
            Messaging.send(player, config.invalidWildLocation.build());
            return;
        }

        // Teleport the player
        TeleportTask.builder(player, location)
                .delay(5, TimeUnit.SECONDS)
                .onInit(ctx -> {
                    // Notify the player
                    ctx.message(config.wildTeleport.build(
                            "seconds", ctx.timeUntilTeleport(TimeUnit.SECONDS)));
                })
                .initiate(this.plugin);

        // Log location in console
        Reference.LOGGER.info("Teleporting {} to location (x={}, y={}, z={})", player.getName(), location.getX(), location.getY(), location.getZ());
    }

    private static Location getRandomLocation(World world, int xRadius, int zRadius) {
        Random random = new Random();

        int x = random.nextInt(xRadius * 2) - xRadius;
        int z = random.nextInt(zRadius * 2) - zRadius;
        int y = world.getHighestBlockYAt(x, z) + 1;

        return new Location(world, x, y, z);
    }
}