package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
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
final class RankCommand extends AlpineCommand {

    private static final Map<CommandSender, Long> CONFIRMATION_MAP = new HashMap<>();

    public RankCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Arg("rank") Rank rank
    ) {
        setRank(sender, other, rank);
    }

    @Execute(name = "show")
    public void show(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.get().factions().findOrDefault(other);

        config.memberRank.send(sender,
                "faction", RelationHelper.formatLiteralFactionName(sender, faction),
                "faction_name", faction.getName(),
                "player", RelationHelper.formatLiteralPlayerName(sender, other),
                "player_name", other.getName(),
                "rank", faction.getMemberRankOrDefault(other.getUniqueId()).getId()
        );
    }

    private static void setRank(@NotNull CommandSender actor, @NotNull OfflinePlayer other, @NotNull Rank rank) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        boolean overriding = PlayerHandler.getInstance().isOverriding(actor);
        UUID actorId = PlayerHelper.getId(actor);

        FactionAccessor factions = Factions.get().factions();
        Faction senderFaction = factions.findOrDefault(actor);
        Faction targetFaction = factions.findOrDefault(other);

        if (targetFaction.isWilderness()) {
            config.playerNotInFaction.send(actor,
                    "player", RelationHelper.formatPlayerName(actor, other),
                    "player_name", other.getName());
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(actor, targetFaction,
                Permissions.MODIFY_RELATIONS, "modify relations");
        if (!permitted) {
            return;
        }

        if (!overriding && other.getUniqueId().equals(actorId)) {
            config.unableToUpdateSelf.send(actor);
            return;
        }

        // ensure rank can be applied
        Rank senderRank = targetFaction.getMemberRankOrDefault(actorId);
        Rank memberRank = targetFaction.getMemberRankOrDefault(other.getUniqueId());
        boolean isPromotion = rank.isSuperior(memberRank);
        boolean leader = rank == Rank.LEADER && (overriding || senderRank == Rank.LEADER && !other.getUniqueId().equals(actorId));
        if (isPromotion && !overriding && !leader && rank.isSuperiorOrMatching(senderRank)) {
            config.rankTooHigh.send(actor);
            return;
        }
        if (!isPromotion && !overriding && !leader && !senderRank.isSuperior(memberRank)) {
            config.rankTooHigh.send(actor);
            return;
        }


        // ensure the rank has changed
        if (rank == memberRank) {
            config.unableToUpdateRank.send(actor,
                    "player", RelationHelper.formatPlayerName(actor, other),
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
                    "actor", RelationHelper.formatPlayerName(observer, actor),
                    "actor_name", PlayerHelper.getName(actor),
                    "faction", RelationHelper.formatFactionName(observer, targetFaction),
                    "faction_name", targetFaction.getName(),
                    "player", RelationHelper.formatPlayerName(observer, other),
                    "player_name", other.getName(),
                    "new_rank", rank.getId(),
                    "old_rank", memberRank.getId()
            );
        });
    }

    @Command(name = "factions promote")
    @Description("Promote a faction member.")
    public static final class Promote extends AlpineCommand {
        public Promote(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            Rank rank = Factions.get().factions().findOrDefault(other).getMemberRankOrDefault(other.getUniqueId());
            setRank(sender, other, rank.getNextRank());
        }
    }

    @Command(name = "factions demote")
    @Description("Demote a faction member.")
    public static final class Demote extends AlpineCommand {
        public Demote(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            Rank rank = Factions.get().factions().findOrDefault(other).getMemberRankOrDefault(other.getUniqueId());
            setRank(sender, other, rank.getPreviousRank());
        }
    }

    @Command(name = "factions leader")
    @Description("Promote a new faction leader.")
    public static final class Leader extends AlpineCommand {
        public Leader(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            setRank(sender, other, Rank.LEADER);
        }
    }

    @Command(name = "factions coleader")
    @Description("Promote a member to co-leader.")
    public static final class CoLeader extends AlpineCommand {
        public CoLeader(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            setRank(sender, other, Rank.COLEADER);
        }
    }

    @Command(name = "factions officer")
    @Description("Promote a member to officer.")
    public static final class Officer extends AlpineCommand {
        public Officer(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            setRank(sender, other, Rank.OFFICER);
        }
    }

    @Command(name = "factions mod")
    @Description("Promote a member to officer.")
    public static final class Mod extends AlpineCommand {
        public Mod(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("player") @Async OfflinePlayer other
        ) {
            setRank(sender, other, Rank.OFFICER);
        }
    }
}
