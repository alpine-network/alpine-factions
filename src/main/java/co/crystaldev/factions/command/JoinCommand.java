package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions join")
@Description("Join a faction.")
final class JoinCommand extends AlpineCommand {
    public JoinCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("faction") Faction faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        boolean overriding = PlayerHandler.getInstance().isOverriding(player);

        FactionAccessor factions = Factions.get().factions();
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
            FactionHelper.broadcast(faction, observer -> config.attemptedMemberJoin.build(
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
        FactionHelper.broadcast(faction, observer -> {
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
            @Arg("faction") Faction faction,
            @Arg("player") @Async OfflinePlayer joiningPlayer
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        FactionAccessor factions = Factions.get().factions();
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

                    "faction", FactionHelper.formatRelational(player, faction, false),
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
        FactionHelper.broadcast(faction, player, observer -> {
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
