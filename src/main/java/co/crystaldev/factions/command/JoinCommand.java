package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
@Command(name = "factions join")
@Description("Join a faction.")
public final class JoinCommand extends FactionsCommand {
    public JoinCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Faction faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        boolean overriding = PlayerHandler.getInstance().isOverriding(player);

        FactionAccessor factions = Accessors.factions();
        Faction otherFaction = factions.findOrDefault(player);

        // ensure the player is not in a faction
        if (!otherFaction.isWilderness()) {
            config.alreadyInFaction.send(player);
            return;
        }

        // ensure the player is even invited
        if (!overriding && !faction.canJoin(player.getUniqueId())) {
            config.notInvited.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName());

            // notify the faction of this attempt
            Messaging.broadcast(faction, observer -> config.attemptedMemberJoin.build(
                    "player", FactionHelper.formatRelational(observer, factions.getWilderness(), player),
                    "player_name", player.getName()
            ));
            return;
        }

        // ensure the faction is not full
        if (!overriding && faction.getMemberCount() >= faction.getMemberLimit()) {
            config.fullFaction.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName(),
                    "limit", faction.getMemberLimit());
            return;
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(faction, otherFaction, player));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // add the member
        faction.addMember(player.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, observer -> {
            if (observer.equals(player)) {
                return null;
            }

            return config.memberJoin.build(
                    "player", FactionHelper.formatRelational(observer, faction, player),
                    "player_name", player.getName());
        });

        // notify the new member
        Messaging.send(player, config.join.build(
                "faction", FactionHelper.formatRelational(player, faction, false),
                "faction_name", faction.getName()
        ));
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Faction faction,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer joiningPlayer
    ) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Accessors.factions();
        Faction otherFaction = factions.find(joiningPlayer);
        Faction actingFaction = factions.findOrDefault(player);
        Faction wilderness = factions.getWilderness();

        // require player to be overriding
        if (!PlayerHandler.getInstance().isOverriding(player)) {
            return;
        }

        // ensure the player is not in a faction
        if (otherFaction != null) {
            config.playerAlreadyInFaction.send(player,
                    "player", FactionHelper.formatRelational(player, otherFaction, joiningPlayer),
                    "player_name", joiningPlayer.getName());
            return;
        }

        // ensure the player is even invited
        if (!faction.canJoin(joiningPlayer.getUniqueId())) {
            config.playerNotInvited.send(player,
                    "player", FactionHelper.formatRelational(player, wilderness, joiningPlayer),
                    "player_name", joiningPlayer.getName(),

                    "faction", FactionHelper.formatRelational(player, wilderness, joiningPlayer),
                    "faction_name", faction.getName());
            return;
        }

        // ensure the faction is not full
        if (faction.getMemberCount() >= faction.getMemberLimit()) {
            config.fullFaction.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName(),
                    "limit", faction.getMemberLimit());
            return;
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(faction, factions.getWilderness(), player));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // add the member
        faction.addMember(joiningPlayer.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, player, observer -> {
            if (observer.getUniqueId().equals(joiningPlayer.getUniqueId())) {
                return null;
            }

            return config.memberForceJoin.build(
                    "inviter", FactionHelper.formatRelational(observer, actingFaction, player),
                    "inviter_name", player.getName(),

                    "player", FactionHelper.formatRelational(observer, wilderness, joiningPlayer),
                    "player_name", joiningPlayer.getName());
        });

        // notify the new member
        Messaging.attemptSend(joiningPlayer, config.forceJoin.build(
                "actor", FactionHelper.formatRelational(joiningPlayer, actingFaction, player),
                "actor_name", player.getName(),

                "faction", FactionHelper.formatRelational(joiningPlayer, faction, false),
                "faction_name", faction.getName()
        ));
    }
}
