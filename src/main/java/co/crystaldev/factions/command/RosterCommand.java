package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Initializable;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/29/2024
 */
@Command(name = "factions roster")
@Description("Manage the faction roster.")
public final class RosterCommand extends FactionsCommand implements Initializable {
    public RosterCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean init() {
        return FactionConfig.getInstance().rosterEnabled;
    }

    @Execute(name = "add")
    public void add(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("rank") @Key(Args.FACTION_RANK) Optional<Rank> rank,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction senderFaction = Accessors.factions().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        if (!faction.isPermitted(sender, Permissions.MODIFY_ROSTER)) {
            FactionHelper.missingPermission(sender, faction, "manage roster");
            return;
        }

        // ensure the roster isn't full
        if (faction.getRosterCount() >= faction.getRosterLimit()) {
            config.rosterFull.send(sender,
                    "player", FactionHelper.formatRelational(sender, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player isn't on the roster
        if (faction.isOnRoster(other.getUniqueId())) {
            config.alreadyOnRoster.send(sender,
                    "player", FactionHelper.formatRelational(sender, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to add this rank
        Rank parsedRank = rank.orElse(Rank.getDefault());
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && !parsedRank.isSuperiorOrMatching(faction.getMemberRankOrDefault(PlayerHelper.getId(sender)))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // add to the roster
        faction.addMemberToRoster(other.getUniqueId(), parsedRank);

        // notify faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rosterAdd.build(
                    "actor", FactionHelper.formatRelational(observer, faction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", FactionHelper.formatRelational(observer, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(observer, faction),
                    "faction_name", faction.getName(),
                    "rank", parsedRank.getId()
            );
        });

        // notify enlistee
        Messaging.attemptSend(other, config.rosterAdded.build(
                "faction", FactionHelper.formatRelational(other, faction, false),
                "faction_name", faction.getName(),
                "actor", FactionHelper.formatRelational(other, faction, sender),
                "actor_name", PlayerHelper.getName(sender)
        ));
    }

    @Execute(name = "remove")
    public void remove(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction senderFaction = Accessors.factions().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        if (!faction.isPermitted(sender, Permissions.MODIFY_ROSTER)) {
            FactionHelper.missingPermission(sender, faction, "manage roster");
            return;
        }

        // ensure the player isn't on the roster
        if (!faction.isOnRoster(other.getUniqueId())) {
            config.notOnRoster.send(sender,
                    "player", FactionHelper.formatRelational(sender, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to modify this member
        Rank parsedRank = faction.getMemberRank(other.getUniqueId());
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && parsedRank.isSuperiorOrMatching(faction.getMemberRankOrDefault(PlayerHelper.getId(sender)))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // notify faction
        FactionHelper.broadcast(faction, sender, observer -> {
            if (observer.getUniqueId().equals(other.getUniqueId())) {
                return null;
            }

            return config.rosterRemove.build(
                    "actor", FactionHelper.formatRelational(observer, faction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", FactionHelper.formatRelational(observer, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(observer, faction),
                    "faction_name", faction.getName(),
                    "rank", parsedRank.getId()
            );
        });

        // remove from the roster
        faction.removeMemberFromRoster(other.getUniqueId());

        // notify enlistee
        Messaging.attemptSend(other, config.rosterRemoved.build(
                "faction", FactionHelper.formatRelational(other, faction, false),
                "faction_name", faction.getName(),
                "actor", FactionHelper.formatRelational(other, faction, sender),
                "actor_name", PlayerHelper.getName(sender)
        ));
    }

    @Execute(name = "setrank")
    public void setRank(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("rank") @Key(Args.FACTION_RANK) Rank rank,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction senderFaction = Accessors.factions().findOrDefault(sender);
        Faction faction = targetFaction.orElse(senderFaction);

        if (!faction.isPermitted(sender, Permissions.MODIFY_ROSTER)) {
            FactionHelper.missingPermission(sender, faction, "manage roster");
            return;
        }

        // ensure the player isn't on the roster
        if (!faction.isOnRoster(other.getUniqueId())) {
            config.notOnRoster.send(sender,
                    "player", FactionHelper.formatRelational(sender, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "faction", FactionHelper.formatRelational(sender, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the player is able to set this rank
        Rank parsedRank = faction.getMemberRank(other.getUniqueId());
        Rank senderRank = faction.getMemberRankOrDefault(PlayerHelper.getId(sender));
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);
        if (!overriding && (parsedRank.isSuperiorOrMatching(senderRank) || rank.isSuperiorOrMatching(senderRank))) {
            config.rankTooHigh.send(sender, "rank", parsedRank.getId());
            return;
        }

        // set the rank
        faction.setRosterRank(other.getUniqueId(), rank);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rosterSetRank.build(
                    "actor", FactionHelper.formatRelational(observer, faction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", FactionHelper.formatRelational(observer, faction, other),
                    "player_name", PlayerHelper.getName(other),
                    "rank", rank.getId()
            );
        });
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = targetFaction.orElse(factions.findOrDefault(sender));

        if (!faction.isPermitted(sender, Permissions.MODIFY_ROSTER)) {
            FactionHelper.missingPermission(sender, faction, "manage roster");
            return;
        }

        Component title = config.rosterListTitle.build(
                "faction", FactionHelper.formatRelational(sender, faction, false),
                "faction_name", faction.getName()
        );
        String command = "/f roster list %page% " + faction.getName();
        Messaging.send(sender, Formatting.page(title, faction.getRoster(), command, page.orElse(1), 10, member -> {
            if (member == null) {
                return ComponentHelper.nil();
            }

            OfflinePlayer player = member.getOfflinePlayer();
            return config.rosterListEntry.build(
                    "player", FactionHelper.formatRelational(sender, faction, player, false),
                    "player_name", player.getName(),
                    "rank", member.getRank().getId()
            );
        }));
    }
}
