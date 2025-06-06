package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class PlayerHelper {

    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static @NotNull String getName(@Nullable ServerOperator sender) {
        if (sender == null) {
            return "< null >";
        }

        if (sender instanceof ConsoleCommandSender) {
            return "@console";
        }

        if (!(sender instanceof OfflinePlayer)) {
            return sender.toString();
        }

        return ((OfflinePlayer) sender).getName();
    }

    public static @NotNull UUID getId(@NotNull ServerOperator operator) {
        if (operator instanceof OfflinePlayer) {
            return ((OfflinePlayer) operator).getUniqueId();
        }
        else {
            return EMPTY_UUID;
        }
    }

    public static boolean isVanished(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        for (MetadataValue value : player.getMetadata("vanished")) {
            if (value.asBoolean()) {
                return true;
            }
        }

        return false;
    }
}
