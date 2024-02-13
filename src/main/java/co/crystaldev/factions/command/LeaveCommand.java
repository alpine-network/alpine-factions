package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
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
        FactionStore store = FactionStore.getInstance();
        Faction faction = store.findFaction(player);

        // ensure the player is in a faction
        if (faction == null) {
            config.notInFaction.send(player);
            return;
        }

        // notify the faction before the player is removed
        Messaging.broadcast(faction, pl -> config.memberLeave.build(
                "player", FactionHelper.formatRelational(pl, faction, player),
                "player_name", player.getName()
        ));

        // remove the member
        faction.removeMember(player.getUniqueId());

        // notify the player
        Messaging.send(player, config.leave.build(
                "faction", FactionHelper.formatRelational(player, faction),
                "faction_name", faction.getName()
        ));
    }
}
