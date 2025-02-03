package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions kick")
@Description("Remove a member from the faction.")
final class KickCommand extends AlpineCommand {
    public KickCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.get().factions();
        Faction faction = factions.findOrDefault(other);
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);

        // ensure player is in a faction
        if (faction.isWilderness()) {
            config.playerNotInFaction.send(sender,
                    "player", RelationHelper.formatPlayerName(sender, other),
                    "player_name", other.getName());
            return;
        }

        // ensure the sender has permission to kick the player
        boolean permitted = overriding || !PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.KICK_MEMBERS, "kick members");
        if (!permitted) {
            return;
        }

        // ensure the player is not superior
        if (!overriding && sender instanceof Player) {
            Player player = (Player) sender;
            Rank senderRank = faction.getMemberRankOrDefault(player.getUniqueId(), Rank.LEADER);
            Rank memberRank = faction.getMemberRankOrDefault(other.getUniqueId(), Rank.getDefault());

            if (memberRank.isSuperiorOrMatching(senderRank)) {
                config.cantKick.send(sender,
                        "player", RelationHelper.formatPlayerName(sender, other),
                        "player_name", other.getName());
                return;
            }
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(faction, factions.getWilderness(), other));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            if (observer.getUniqueId().equals(other.getUniqueId())) {
                return null;
            }

            return config.kick.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),

                    "player", RelationHelper.formatPlayerName(observer, other),
                    "player_name", other.getName()
            );
        });

        // kick the member
        faction.removeMember(other.getUniqueId());

        // notify the player
        Messaging.attemptSend(other, config.kicked.build(
                "actor", RelationHelper.formatPlayerName(other, sender),
                "actor_name", PlayerHelper.getName(sender),

                "faction", RelationHelper.formatFactionName(other, faction),
                "faction_name", faction.getName()
        ));
    }
}
