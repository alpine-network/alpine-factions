package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.PlayerChangedFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
@Command(name = "factions leave")
@Description("Leave your faction.")
public final class LeaveCommand extends FactionsCommand {
    public LeaveCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = factions.find(player);

        // ensure the player is in a faction
        if (faction == null) {
            config.notInFaction.send(player);
            return;
        }

        // ensure that the player can leave
        if (faction.canDisband()) {
            if (faction.getMemberCount() > 1 && faction.getMemberRank(player.getUniqueId()) == Rank.LEADER) {
                // the leader can't just leave the faction!
                config.promoteLeader.send(player);
                return;
            }
            else if (faction.getMemberCount() == 1) {
                // the leader was the only member, disband
                faction.disband(player);
                return;
            }
        }

        // call event
        PlayerChangedFactionEvent event = AlpineFactions.callEvent(new PlayerChangedFactionEvent(factions.getWilderness(), faction, player));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // notify the faction
        FactionHelper.broadcast(faction, observer -> {
            if (observer.equals(player)) {
                return null;
            }

            return config.memberLeave.build(
                    "player", FactionHelper.formatRelational(observer, faction, player),
                    "player_name", player.getName());
        });

        // notify the player
        Messaging.send(player, config.leave.build(
                "faction", FactionHelper.formatRelational(player, faction),
                "faction_name", faction.getName()
        ));

        // remove the member
        faction.removeMember(player.getUniqueId());
    }
}
