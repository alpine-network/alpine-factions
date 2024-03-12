package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.FactionNameUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions name")
@Description("Rename your faction.")
public final class NameCommand extends FactionsCommand {

    private final Map<CommandSender, Long> confirmationMap = new HashMap<>();

    public NameCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = targetFaction.orElseGet(() -> factions.findOrDefault(sender));

        // ensure the user has permission
        if (!faction.isPermitted(sender, Permissions.MODIFY_NAME)) {
            FactionHelper.missingPermission(sender, faction, "modify name");
            return;
        }

        // ensure the name is different
        if (name.equals(faction.getName())) {
            config.factionNameUnchanged.send(sender);
            return;
        }

        // ensure there is no other faction with the same name
        Faction other = factions.getByName(name);
        if (other != null) {
            config.factionWithName.send(sender, "faction_name", name);
            return;
        }

        // ensure the name is long enough
        if (name.length() < factionConfig.minNameLength) {
            config.nameTooShort.send(sender, "length", factionConfig.minNameLength);
            return;
        }

        // ensure the name isn't too long
        if (name.length() > factionConfig.maxNameLength) {
            config.nameTooLong.send(sender, "length", factionConfig.maxNameLength);
            return;
        }

        // require a confirmation from the user
        if (!this.confirmationMap.containsKey(sender) || System.currentTimeMillis() - this.confirmationMap.get(sender) > 10_000L) {
            this.confirmationMap.put(sender, System.currentTimeMillis());
            config.confirm.send(sender);
            return;
        }

        // call event
        FactionNameUpdateEvent event = AlpineFactions.callEvent(new FactionNameUpdateEvent(faction, sender, name));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // rename the faction
        faction.setName(name);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rename.build(
                    "actor", FactionHelper.formatRelational(observer, faction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "faction_name", name);
        });
    }
}
