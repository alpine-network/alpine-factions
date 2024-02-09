package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.util.Messaging;
import co.crystaldev.factions.util.AsciiFactionMap;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
@Command(name = "factions map", aliases = "factions ma")
@Description("Display the factions map.")
public final class MapCommand extends BaseFactionsCommand {
    public MapCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        Messaging.send(player, AsciiFactionMap.create(player, false));
    }

    @Execute(name = "on")
    public void on(@Context Player player) {

    }

    @Execute(name = "off")
    public void off(@Context Player player) {

    }
}
