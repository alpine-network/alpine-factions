package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.player.FPlayer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class PlayerStore extends AlpineStore<UUID, FPlayer> {

    @Getter
    private static PlayerStore instance;
    { instance = this; }

    PlayerStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<UUID, FPlayer>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "players"))
                .gson(Reference.GSON)
                .dataType(FPlayer.class)
                .build());
    }

    @NotNull
    public FPlayer getPlayer(@NotNull UUID player) {
        return this.getOrCreate(player, () -> new FPlayer(player));
    }

    public FPlayer getPlayer(@NotNull OfflinePlayer player) {
        return this.getPlayer(player.getUniqueId());
    }

    @NotNull
    public FPlayer wrapPlayer(@NotNull UUID id, @NotNull Consumer<FPlayer> playerConsumer) {
        FPlayer player = this.getPlayer(id);
        playerConsumer.accept(player);
        this.put(id, player);
        return player;
    }
}
