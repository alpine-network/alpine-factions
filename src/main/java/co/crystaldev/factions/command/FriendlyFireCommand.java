package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @since 0.1.0
 */
@Command(name = "factions friendlyfire", aliases = { "factions friendlyfire", "factions fr" })
@Description("Toggle friendly fire.")
final class FriendlyFireCommand extends FactionsCommand {
    public FriendlyFireCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        PlayerAccessor players = Factions.get().players();
        players.wrap(player, state -> {
            state.setFriendlyFire(!state.isFriendlyFire());

            config.stateChange.send(player,
                    "subject", "Friendly Fire",
                    "state", state.isFriendlyFire());
        });
    }
}
