package co.crystaldev.factions.handler;

import co.crystaldev.factions.config.MessageConfig;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/12/2024
 *
 * TODO: move this class AlpineCore
 */
public final class TeleportManager {

    private static final int TELEPORT_DELAY_SEC = 5;

    private static final double MAX_MOVEMENT = 0.5;

    @Getter
    private static TeleportManager instance;
    { instance = this; }

    private final Map<UUID, QueuedTeleport> queuedTeleports = new HashMap<>();

    public int queueTeleport(@NotNull Player player, @NotNull Location destination, @NotNull Runnable onTeleport) {
        QueuedTeleport teleport = new QueuedTeleport(player, destination, onTeleport);
        this.queuedTeleports.put(player.getUniqueId(), teleport);
        return teleport.delay;
    }

    public int queueTeleport(@NotNull Player player, @NotNull Location destination) {
        return this.queueTeleport(player, destination, () -> {});
    }

    @ApiStatus.Internal
    public void onPlayerMove(@NotNull Player player, double distance) {
        QueuedTeleport teleport = this.queuedTeleports.get(player.getUniqueId());
        if (teleport == null) {
            return;
        }

        if (distance > MAX_MOVEMENT) {
            this.queuedTeleports.remove(player.getUniqueId());
            MessageConfig.getInstance().teleportFailed.send(player);
        }
    }

    @ApiStatus.Internal
    public void execute() {
        if (this.queuedTeleports.isEmpty()) {
            return;
        }

        MessageConfig config = MessageConfig.getInstance();

        Iterator<Map.Entry<UUID, QueuedTeleport>> iterator = this.queuedTeleports.entrySet().iterator();
        while (iterator.hasNext()) {
            QueuedTeleport teleport = iterator.next().getValue();
            Player player = teleport.player;

            boolean sameWorld = player.getWorld().equals(teleport.initialLocation.getWorld());
            boolean moved = player.getLocation().distance(teleport.initialLocation) > MAX_MOVEMENT;
            if (!sameWorld || moved) {
                iterator.remove();
                config.teleportFailed.send(player);
                return;
            }

            if (--teleport.delay <= 0) {
                iterator.remove();
                config.teleport.send(player);

                Location playerLocation = player.getLocation();
                Location destination = teleport.destination.clone();
                destination.setPitch(playerLocation.getPitch());
                destination.setYaw(playerLocation.getYaw());

                player.teleport(teleport.destination);
                teleport.onTeleport.run();
            }
        }
    }

    private static final class QueuedTeleport {
        private final Player player;
        private final Location destination;
        private final Location initialLocation;
        private final Runnable onTeleport;
        private int delay = TELEPORT_DELAY_SEC;

        public QueuedTeleport(@NotNull Player player, @NotNull Location destination, @NotNull Runnable onTeleport) {
            this.player = player;
            this.destination = destination;
            this.initialLocation = player.getLocation();
            this.onTeleport = onTeleport;
        }
    }
}
