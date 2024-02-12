package co.crystaldev.factions.api.faction.member;

import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.store.PlayerStore;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Component getTitle() {
        return this.title == null ? Component.empty() : this.title;
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    @NotNull
    public FPlayer getUser() {
        return PlayerStore.getInstance().getPlayer(this.id);
    }
}
