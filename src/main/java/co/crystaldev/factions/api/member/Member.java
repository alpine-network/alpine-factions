package co.crystaldev.factions.api.member;

import co.crystaldev.factions.api.FactionPlayer;
import co.crystaldev.factions.store.PlayerStore;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@Data
public final class Member {

    private final UUID id;

    private final long joinedAt = System.currentTimeMillis();

    private Rank rank;

    private Component title;

    public Member(@NotNull UUID id, @NotNull Rank rank) {
        this.id = id;
        this.rank = rank;
    }

    @NotNull
    public FactionPlayer getUser() {
        return PlayerStore.getInstance().getPlayer(this.id);
    }
}
