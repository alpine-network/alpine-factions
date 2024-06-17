package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Command(name = "factions territorialtitles", aliases = { "factions territorialtitle", "factions tt" })
@Description("Switch between territorial title modes.")
final class TerritorialTitlesCommand extends FactionsCommand {
    public TerritorialTitlesCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        PlayerAccessor players = Factions.get().getPlayers();
        players.wrap(player, state -> {
            state.setTerritorialTitleMode(next(state.getTerritorialTitleMode()));

            config.stateChange.send(player,
                    "subject", "Territorial Titles",
                    "state", state.getTerritorialTitleMode().getName());
        });
    }

    @NotNull
    private static TerritorialTitleMode next(@NotNull TerritorialTitleMode current) {
        TerritorialTitleMode[] values = TerritorialTitleMode.values();
        int ordinal = current.ordinal() + 1;
        return values[ordinal >= values.length ? 0 : ordinal];
    }
}
