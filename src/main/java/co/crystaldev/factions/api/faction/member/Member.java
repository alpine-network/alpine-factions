package co.crystaldev.factions.api.faction.member;

import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.player.FPlayer;
import com.google.common.collect.ComparisonChain;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

/**
 * @since 0.1.0
 */
@Data
public final class Member {

    public static final Comparator<Member> COMPARATOR = new MemberComparator();

    private final UUID id;

    private final long joinedAt = System.currentTimeMillis();

    private @NotNull Rank rank;

    private @Nullable Component title;

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
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.id);
    }

    @NotNull
    public FPlayer getUser() {
        return Factions.get().players().getById(this.id);
    }

    private static final class MemberComparator implements Comparator<Member> {
        @Override
        public int compare(Member o1, Member o2) {
            return ComparisonChain.start()
                    .compare(o1.getRank(), o2.getRank())
                    .compare(o1.getOfflinePlayer().getName(), o2.getOfflinePlayer().getName(), String::compareToIgnoreCase)
                    .result();
        }
    }
}
