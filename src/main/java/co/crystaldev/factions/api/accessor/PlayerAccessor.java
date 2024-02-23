package co.crystaldev.factions.api.accessor;

import co.crystaldev.factions.api.player.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/21/2024
 */
public interface PlayerAccessor {

    void update(@NotNull FPlayer player);

    @NotNull
    FPlayer getById(@NotNull UUID player);

    default void forEach(@NotNull Consumer<FPlayer> playerConsumer) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            playerConsumer.accept(this.get(player));
        });
    }

    @NotNull
    default FPlayer get(@NotNull OfflinePlayer player) {
        return this.getById(player.getUniqueId());
    }

    @NotNull
    default FPlayer get(@NotNull ServerOperator player) {
        if (!(player instanceof OfflinePlayer)) {
            throw new IllegalArgumentException();
        }
        return this.getById(((OfflinePlayer) player).getUniqueId());
    }
}
