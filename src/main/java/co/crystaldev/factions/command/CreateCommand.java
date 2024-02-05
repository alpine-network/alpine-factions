package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.command.argument.AlphanumericArgumentResolver;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions create")
@Description("Create a new faction.")
public final class CreateCommand extends BaseFactionsCommand {

    public CreateCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("name") @Key(AlphanumericArgumentResolver.KEY) String name
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();

        if (name.length() < factionConfig.minNameLength) {
            messageConfig.nameTooShort.send(player, "length", factionConfig.minNameLength);
            return;
        }

        if (name.length() > factionConfig.maxNameLength) {
            messageConfig.nameTooLong.send(player, "length", factionConfig.maxNameLength);
            return;
        }

        messageConfig.create.send(player, "faction_name", name);
    }
}
