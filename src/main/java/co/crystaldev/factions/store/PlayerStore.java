package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Constants;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.FactionConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @since 0.1.0
 */
public final class PlayerStore extends AlpineStore<UUID, FPlayer> implements PlayerAccessor {

    PlayerStore(@NotNull AlpinePlugin plugin) {
        super(plugin, ((AlpineFactions) plugin).buildPlayerStorageDriver(Constants.GSON));
    }

    @Override
    public void save(@NotNull FPlayer player) {
        this.put(player.getId(), player);
    }

    @Override
    public @NotNull FPlayer getById(@NotNull UUID player) {
        return this.getOrCreate(player, () -> {
            FPlayer fPlayer = new FPlayer(player);
            fPlayer.setPowerLevel(this.plugin.getConfiguration(FactionConfig.class).initialPlayerPower);
            return fPlayer;
        });
    }
}
