package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.AlphanumericArgumentResolver;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions name")
@Description("Rename your faction.")
public final class NameCommand extends BaseFactionsCommand {

    public NameCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("name") @Key(AlphanumericArgumentResolver.KEY) String name
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction faction = store.findFactionOrDefault(player);

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_NAME)) {
            messageConfig.missingFactionPerm.send(player,
                    "faction_name", faction.getName(),
                    "faction", FactionHelper.formatRelationalFactionName(player, faction));
            return;
        }

        // ensure the name is different
        if (name.equals(faction.getName())) {
            messageConfig.factionNameUnchanged.send(player);
            return;
        }

        // ensure there is no other faction with the same name
        Faction other = store.findFaction(name);
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
        Messaging.broadcast(faction, messageConfig.rename.build("player_name", player.getName(), "faction_name", name));
    }

    @Execute
    @Permission(PermissionNodes.ADMIN)
    public void execute(
            @Context Player player,
            @Arg("faction") @Key(AlphanumericArgumentResolver.KEY) String facName,
            @Arg("name") @Key(AlphanumericArgumentResolver.KEY) String name
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction faction = store.findFaction(facName);

        // ensure the given faction exists
        if (faction == null) {
            messageConfig.unknownFaction.send(player, "faction_name", facName);
            return;
        }

        // ensure the name is different
        if (name.equals(faction.getName())) {
            messageConfig.factionNameUnchanged.send(player);
            return;
        }

        // ensure there is no other faction with the same name
        Faction other = store.findFaction(name);
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
        Messaging.broadcast(faction, messageConfig.rename.build("player_name", FactionHelper.formatRelational(player, faction, player.getName()), "faction_name", name));
    }
}
