package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/10/2024
 */
@Command(name = "factions description", aliases = "factions desc")
@Description("Modify your faction's description.")
public final class DescriptionCommand extends FactionsCommand {
    public DescriptionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Join("name") String description
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        Faction faction = Accessors.factions().findOrDefault(player);

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_DESCRIPTION)) {
            FactionHelper.missingPermission(player, faction, "modify description");
            return;
        }

        // change the faction description
        faction.setDescription(Component.text(description));
        faction.markDirty();

        // notify the faction
        Messaging.broadcast(faction, player, observer -> {
            return messageConfig.description.build(
                    "player_name", player.getName(),
                    "player", FactionHelper.formatRelational(observer, faction, player),
                    "description", description);
        });
    }

    @Execute(name = "clear")
    public void clear(@Context Player player) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        Faction faction = Accessors.factions().findOrDefault(player);

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_DESCRIPTION)) {
            FactionHelper.missingPermission(player, faction, "modify description");
            return;
        }

        // clear the faction description
        faction.setDescription(null);
        faction.markDirty();

        // notify the faction
        Messaging.broadcast(faction, player, observer -> {
            return messageConfig.description.build(
                    "player_name", player.getName(),
                    "player", FactionHelper.formatRelational(observer, faction, player),
                    "description", Faction.DEFAULT_DESCRIPTION);
        });
    }
}
