package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/19/2024
 */
@UtilityClass
public final class PlayerHelper {

    @NotNull
    public static String getName(@Nullable ServerOperator sender) {
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
}
