package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.FactionConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class PlayerStore extends AlpineStore<UUID, FPlayer> implements PlayerAccessor {

    PlayerStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<UUID, FPlayer>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "players"))
                .gson(Reference.GSON)
                .dataType(FPlayer.class)
                .build());
    }

    @Override
    public void update(@NotNull FPlayer player) {
        this.put(player.getId(), player);
    }

    @Override
    public @NotNull FPlayer getById(@NotNull UUID player) {
        return this.getOrCreate(player, () -> {
            FPlayer fPlayer = new FPlayer(player);
            fPlayer.setPowerLevel(FactionConfig.getInstance().initialPlayerPower);
            return fPlayer;
        });
    }
}
