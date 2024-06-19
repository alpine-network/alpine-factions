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

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions show", aliases = { "factions faction", "factions who", "factions f" })
@Description("Display information on a specific faction.")
final class ShowCommand extends FactionsCommand {
    public ShowCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        Faction senderFaction = Factions.get().factions().findOrDefault(sender);
        Faction resolvedFaction = faction.orElse(senderFaction);
        Messaging.send(sender, AlpineFactions.getInstance().showFormatter().build(sender, resolvedFaction, senderFaction));
    }
}
