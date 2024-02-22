package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions name")
@Description("Rename your faction.")
public final class NameCommand extends FactionsCommand {
    public NameCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = targetFaction.orElseGet(() -> factions.findOrDefault(player));

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_NAME)) {
            FactionHelper.missingPermission(player, faction, "modify name");
            return;
        }

        // ensure the name is different
        if (name.equals(faction.getName())) {
            messageConfig.factionNameUnchanged.send(player);
            return;
        }

        // ensure there is no other faction with the same name
        Faction other = factions.getByName(name);
        if (other != null) {
            messageConfig.factionWithName.send(player, "faction_name", name);
            return;
        }

        // ensure the name is long enough
        if (name.length() < factionConfig.minNameLength) {
            messageConfig.nameTooShort.send(player, "length", factionConfig.minNameLength);
            return;
        }

        // ensure the name isn't too long
        if (name.length() > factionConfig.maxNameLength) {
            messageConfig.nameTooLong.send(player, "length", factionConfig.maxNameLength);
            return;
        }

        // rename the faction
        faction.setName(name);
        faction.markDirty();

        // notify the faction
        Messaging.broadcast(faction, player, observer -> {
            return messageConfig.rename.build(
                    "player_name", player.getName(),
                    "player", FactionHelper.formatRelational(observer, faction, player),
                    "faction_name", name);
        });
    }
}
