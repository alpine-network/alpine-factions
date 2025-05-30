package co.crystaldev.factions.api.accessor;

import co.crystaldev.factions.api.player.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @since 0.1.0
 */
public interface PlayerAccessor {

    void save(@NotNull FPlayer player);

    @NotNull FPlayer getById(@NotNull UUID player);

    default @NotNull FPlayer get(@NotNull OfflinePlayer player) {
        return this.getById(player.getUniqueId());
    }

    default @NotNull FPlayer get(@NotNull ServerOperator player) {
        if (!(player instanceof OfflinePlayer)) {
            throw new IllegalArgumentException();
        }
        return this.getById(((OfflinePlayer) player).getUniqueId());
    }

    default void wrap(@NotNull UUID player, @NotNull Consumer<@NotNull FPlayer> playerConsumer) {
        FPlayer wrapped = this.getById(player);
        playerConsumer.accept(wrapped);
        this.save(wrapped);
    }

    default void wrap(@NotNull OfflinePlayer player, @NotNull Consumer<@NotNull FPlayer> playerConsumer) {
        this.wrap(player.getUniqueId(), playerConsumer);
    }

    default void forEach(@NotNull Consumer<FPlayer> playerConsumer) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            playerConsumer.accept(this.get(player));
        });
    }
}
