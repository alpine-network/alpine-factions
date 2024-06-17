package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.util.AsciiFactionMap;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions map", aliases = "factions ma")
@Description("Display the factions map.")
final class MapCommand extends FactionsCommand {
    public MapCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        Messaging.send(player, AsciiFactionMap.create(player, false));
    }

    @Execute(name = "on")
    public void on(@Context Player player) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        state.setAutoFactionMap(true);

        // send the status message
        MessageConfig.getInstance().stateChange.send(player, "subject", "Faction Map", "state", true);

        // send the map to the player
        Messaging.send(player, AsciiFactionMap.create(player, true));
    }

    @Execute(name = "off")
    public void off(@Context Player player) {
        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        state.setAutoFactionMap(false);

        // send the status message
        MessageConfig.getInstance().stateChange.send(player, "subject", "Faction Map", "state", false);
    }
}
