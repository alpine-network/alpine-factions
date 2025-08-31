package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class FactionHelper {

    /**
     * @deprecated in favor of {@link Faction#audience()}
     */
    @Deprecated
    public static void broadcast(@NotNull Faction faction, @NotNull Component component) {
        for (Member member : faction.getMembers()) {
            Player player = member.getPlayer();
            if (player != null) {
                Messaging.send(player, component);
            }
        }
    }

    public static void broadcast(@NotNull Faction faction, @Nullable List<ServerOperator> subjects, @NotNull Function<Player, @Nullable Component> playerFunction) {
        Component lastComponent = null;
        for (Member member : faction.getMembers()) {
            Player player = member.getPlayer();
            if (player != null) {
                if (subjects != null) {
                    subjects.remove(player);
                }

                Messaging.send(player, lastComponent = playerFunction.apply(player));
            }
        }

        if (subjects != null) {
            for (ServerOperator subject : subjects) {
                if (subject instanceof Player) {
                    Messaging.send((Player) subject, playerFunction.apply((Player) subject));
                }
                else if (lastComponent != null && subject instanceof CommandSender) {
                    Messaging.send((CommandSender) subject, lastComponent);
                }
            }
        }
    }

    public static void broadcast(@NotNull Faction faction, @Nullable ServerOperator subject, @NotNull Function<Player, @Nullable Component> playerFunction) {
        Component lastComponent = null;
        for (Member member : faction.getMembers()) {
            Player player = member.getPlayer();
            if (player != null) {
                if (player.equals(subject)) {
                    subject = null;
                }

                Messaging.send(player, lastComponent = playerFunction.apply(player));
            }
        }

        if (subject instanceof Player) {
            Messaging.send((Player) subject, playerFunction.apply((Player) subject));
        }
        else if (lastComponent != null && subject instanceof CommandSender) {
            Messaging.send((CommandSender) subject, lastComponent);
        }
    }

    public static void broadcast(@NotNull Faction faction, @NotNull Function<Player, Component> playerFunction) {
        broadcast(faction, (ServerOperator) null, playerFunction);
    }
}
