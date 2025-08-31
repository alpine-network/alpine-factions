package co.crystaldev.factions.api.faction.member;

import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.util.PlayerHelper;
import com.google.common.collect.ComparisonChain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

/**
 * @since 0.1.0
 */
public final class Member {

    public static final Comparator<Member> COMPARATOR = new MemberComparator();

    private final @NotNull UUID id;

    private @NotNull Rank rank;

    private @Nullable Component title;

    private final long joinedAt = System.currentTimeMillis();

    public Member(@NotNull UUID id, @NotNull Rank rank) {
        this.id = id;
        this.rank = rank;
    }

    public @NotNull FPlayer getUser() {
        return Factions.players().getById(this.id);
    }

    public boolean isOnline() {
        return this.getPlayer() != null && !PlayerHelper.isVanished(this.getPlayer());
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        Player player = Bukkit.getPlayer(this.id);
        return player != null ? player : Bukkit.getOfflinePlayer(this.id);
    }

    public boolean hasJoinedServer() {
        OfflinePlayer player = this.getOfflinePlayer();
        return player.hasPlayedBefore();
    }

    public @NotNull UUID getId() {
        return this.id;
    }

    public @NotNull Rank getRank() {
        return this.rank;
    }

    public void setRank(@NotNull Rank rank) {
        this.rank = rank;
    }

    public @NotNull Component getTitle() {
        return this.title == null ? Component.empty() : this.title;
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public void setTitle(@Nullable Component title) {
        this.title = title;
    }

    public long getJoinedAt() {
        return this.joinedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Member)) return false;
        Member member = (Member) object;
        return Objects.equals(this.getId(), member.getId())
                && this.getJoinedAt() == member.getJoinedAt()
                && this.getRank() == member.getRank()
                && Objects.equals(this.getTitle(), member.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getJoinedAt(), this.getRank(), this.getTitle());
    }

    @Override
    public String toString() {
        return "Member(id=" + this.getId() + ", joinedAt=" + this.getJoinedAt() + ", rank=" + this.getRank() + ", title=" + this.getTitle() + ")";
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
