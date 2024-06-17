package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @since 0.1.0
 */
@Command(name = "factions rank")
@Description("Manage faction ranks.")
final class RankCommand extends FactionsCommand {

    private static final Map<CommandSender, Long> CONFIRMATION_MAP = new HashMap<>();

    public RankCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("rank") @Key(Args.FACTION_RANK) Rank rank
    ) {
        setRank(sender, other, rank);
    }

    @Execute(name = "show")
    public void show(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction faction = Factions.get().getFactions().findOrDefault(other);

        config.memberRank.send(sender,
                "faction", FactionHelper.formatRelational(sender, faction, false),
                "faction_name", faction.getName(),
                "player", FactionHelper.formatRelational(sender, faction, other, false),
                "player_name", other.getName(),
                "rank", faction.getMemberRankOrDefault(other.getUniqueId()).getId()
        );
    }

    private static void setRank(@NotNull CommandSender actor, @NotNull OfflinePlayer other, @NotNull Rank rank) {
        MessageConfig config = MessageConfig.getInstance();
        boolean overriding = PlayerHandler.getInstance().isOverriding(actor);
        UUID actorId = PlayerHelper.getId(actor);

        FactionAccessor factions = Factions.get().getFactions();
        Faction senderFaction = factions.findOrDefault(actor);
        Faction targetFaction = factions.findOrDefault(other);

        if (targetFaction.isWilderness()) {
            config.playerNotInFaction.send(actor,
                    "player", FactionHelper.formatRelational(actor, targetFaction, other),
                    "player_name", other.getName());
            return;
        }

        if (!targetFaction.isPermitted(actor, Permissions.MODIFY_RELATIONS)) {
            FactionHelper.missingPermission(actor, targetFaction, "modify relations");
            return;
        }

        if (!overriding && other.getUniqueId().equals(actorId)) {
            config.unableToUpdateSelf.send(actor);
            return;
        }

        // ensure rank can be applied
        Rank senderRank = targetFaction.getMemberRankOrDefault(actorId);
        Rank memberRank = targetFaction.getMemberRankOrDefault(other.getUniqueId());
        boolean leader = rank == Rank.LEADER && (overriding || senderRank == Rank.LEADER && !other.getUniqueId().equals(actorId));
        if (!overriding && !leader && rank.isSuperiorOrMatching(senderRank)) {
            config.rankTooHigh.send(actor);
            return;
        }

        // ensure the rank has changed
        if (rank == memberRank) {
            config.unableToUpdateRank.send(actor,
                    "player", FactionHelper.formatRelational(actor, targetFaction, other),
                    "player_name", other.getName());
            return;
        }

        // set the rank
        if (leader) {
            if (!CONFIRMATION_MAP.containsKey(actor) || System.currentTimeMillis() - CONFIRMATION_MAP.get(actor) > 10_000L) {
                CONFIRMATION_MAP.put(actor, System.currentTimeMillis());
                config.confirm.send(actor);
                return;
            }

            targetFaction.setOwner(other.getUniqueId());
        }
        else {
            targetFaction.setMemberRank(other.getUniqueId(), rank);
        }

        // notify the faction
        FactionHelper.broadcast(targetFaction, actor, observer -> {
            if (observer == null) {
                return ComponentHelper.nil();
            }

            ConfigText text = leader ? config.leader : (rank.isSuperior(memberRank) ? config.promote : config.demote);
            return text.build(
                    "actor", FactionHelper.formatRelational(observer, senderFaction, actor),
                    "actor_name", PlayerHelper.getName(actor),
                    "faction", FactionHelper.formatRelational(observer, targetFaction),
                    "faction_name", targetFaction.getName(),
                    "player", FactionHelper.formatRelational(observer, targetFaction, other),
                    "player_name", other.getName(),
                    "new_rank", rank.getId(),
                    "old_rank", memberRank.getId()
            );
        });
    }

    @Command(name = "factions promote")
    @Description("Promote a faction member.")
    public static final class Promote extends FactionsCommand {
        public Promote(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other
        ) {
            Rank rank = Factions.get().getFactions().findOrDefault(other).getMemberRankOrDefault(other.getUniqueId());
            setRank(sender, other, rank.getNextRank());
        }
    }

    @Command(name = "factions demote")
    @Description("Demote a faction member.")
    public static final class Demote extends FactionsCommand {
        public Demote(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other
        ) {
            Rank rank = Factions.get().getFactions().findOrDefault(other).getMemberRankOrDefault(other.getUniqueId());
            setRank(sender, other, rank.getPreviousRank());
        }
    }

    @Command(name = "factions leader")
    @Description("Promote a new faction leader.")
    public static final class Leader extends FactionsCommand {
        public Leader(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other
        ) {
            setRank(sender, other, Rank.LEADER);
        }
    }
}
