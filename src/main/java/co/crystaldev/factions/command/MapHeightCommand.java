package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.5
 */
@Command(name = "factions mapheight", aliases = { "factions mapsize" })
@Description("Set the height of the map when Auto Map is enabled")
public final class MapHeightCommand extends AlpineCommand {
    MapHeightCommand(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    private static final int MAXIMUM_MAP_HEIGHT = 17;
    private static final int MINIMAL_MAP_HEIGHT = 8;

    @Execute
    public void execute(
            @Context Player player,
            @Arg("size") int size
    ) {
        // Do not allow lower than minimal map height, or higher than actual /f map
        size = Math.max(MINIMAL_MAP_HEIGHT, Math.min(size, MAXIMUM_MAP_HEIGHT));

        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        PlayerAccessor players = Factions.get().players();
        FPlayer state = players.get(player);

        state.setAutoMapHeight(size);

        config.mapHeightChange.send(player,
                "size", size);
    }
}
