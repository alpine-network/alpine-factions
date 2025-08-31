package co.crystaldev.factions.api.show;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class ShowContext {

    private final CommandSender sender;

    private final Faction faction;

    private final Faction senderFaction;

    public ShowContext(@NotNull CommandSender sender, @NotNull Faction faction, @NotNull Faction senderFaction) {
        this.sender = sender;
        this.faction = faction;
        this.senderFaction = senderFaction;
    }

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

    public @NotNull Member getMember() {
        return this.faction.getMember(this.getPlayer());
    }

    public @NotNull CommandSender getSender() {
        return this.sender;
    }

    public @NotNull Player getPlayer() {
        return (Player) this.sender;
    }

    public @NotNull Faction getFaction() {
        return this.faction;
    }

    public @NotNull Faction getSenderFaction() {
        return this.senderFaction;
    }

    public String toString() {
        return "ShowContext(sender=" + this.getSender() + ", faction=" + this.faction + ", senderFaction=" + this.senderFaction + ")";
    }
}
