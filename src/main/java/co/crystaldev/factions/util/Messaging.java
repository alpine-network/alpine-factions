package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.Reference;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@UtilityClass
public final class Messaging {

    public static void send(@NotNull CommandSender sender, @NotNull Component component) {
        if (sender instanceof Audience) {
            ((Audience) sender).sendMessage(component);
        }
        else {
            wrap(sender).sendMessage(component);
        }
    }

    public static void broadcast(@NotNull Component component) {
        // notify the console
        wrap(Bukkit.getServer().getConsoleSender()).sendMessage(component);

        // notify all players
        Bukkit.getOnlinePlayers().forEach(player -> send(player, component));
    }

    public static void broadcast(@NotNull Component component, @NotNull Predicate<Player> playerPredicate) {
        // notify the console
        wrap(Bukkit.getServer().getConsoleSender()).sendMessage(component);

        // selectively notify all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerPredicate.test(player)) {
                send(player, component);
            }
        }
    }

    @NotNull
    public static Audience wrap(@NotNull CommandSender sender) {
        return sender instanceof Player ? Reference.AUDIENCES.player((Player) sender) : Reference.AUDIENCES.sender(sender);
    }
}
