package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions here", aliases = { "factions h" })
@Description("Display information on a the faction who owns the chunk you're in.")
final class HereCommand extends FactionsCommand {
    public HereCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        Faction senderFaction = Factions.get().factions().findOrDefault(player);
        Faction resolvedFaction = Factions.get().claims().getFactionOrDefault(player.getLocation());
        Messaging.send(player, AlpineFactions.getInstance().showFormatter().build(player, resolvedFaction, senderFaction));
    }
}
