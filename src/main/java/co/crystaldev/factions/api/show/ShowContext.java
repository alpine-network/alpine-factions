package co.crystaldev.factions.api.show;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
@AllArgsConstructor @ToString
public final class ShowContext {

    private final CommandSender sender;

    @Getter
    private final Faction faction;

    @Getter
    private final Faction senderFaction;

    public boolean isSameFaction() {
        return this.faction.equals(this.senderFaction);
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public boolean isMember() {
        return this.isPlayer() && this.faction.isMember(this.getPlayer().getUniqueId());
    }

    public boolean isRankAtLeast(@NotNull Rank rank) {
        return this.getMember().getRank().ordinal() <= rank.ordinal();
    }

    @NotNull
    public Member getMember() {
        return this.faction.getMember(this.getPlayer());
    }

    @NotNull
    public CommandSender getSender() {
        return this.sender;
    }

    @NotNull
    public Player getPlayer() {
        return (Player) this.sender;
    }
}
