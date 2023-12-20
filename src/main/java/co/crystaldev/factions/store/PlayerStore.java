package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.FactionPlayer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class PlayerStore extends AlpineStore<UUID, FactionPlayer> {

    @Getter
    private static PlayerStore instance;
    { instance = this; }

    PlayerStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<UUID, FactionPlayer>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "players"))
                .gson(Reference.GSON)
                .dataType(FactionPlayer.class)
                .build());
    }

    @NotNull
    public FactionPlayer getPlayer(@NotNull UUID id) {
        return this.getOrCreate(id, () -> new FactionPlayer(id));
    }
}
