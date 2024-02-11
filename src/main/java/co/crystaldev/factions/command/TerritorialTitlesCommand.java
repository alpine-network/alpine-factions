package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.PlayerStore;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/10/2024
 */
@Command(name = "factions territorialtitles", aliases = { "factions territorialtitle", "factions tt" })
@Description("Switch between territorial title modes.")
public final class TerritorialTitlesCommand extends FactionsCommand {
    public TerritorialTitlesCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        FPlayer state = PlayerStore.getInstance().getPlayer(player);
        state.setTerritorialTitleMode(next(state.getTerritorialTitleMode()));

        config.stateChange.send(player,
                "subject", "Territorial Titles",
                "state", state.getTerritorialTitleMode().getName());
    }

    @NotNull
    private static TerritorialTitleMode next(@NotNull TerritorialTitleMode current) {
        TerritorialTitleMode[] values = TerritorialTitleMode.values();
        int ordinal = current.ordinal() + 1;
        return values[ordinal >= values.length ? 0 : ordinal];
    }
}
