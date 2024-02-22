package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.CreateFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
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
public final class CreateCommand extends FactionsCommand {
    public CreateCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();
        FactionAccessor factions = Accessors.factions();

        // ensure the player isn't already in a faction
        Faction faction = factions.find(player);
        if (faction != null) {
            messageConfig.alreadyInFaction.send(player);
            return;
        }

        // ensure no other faction has the same name
        faction = factions.getByName(name);
        if (faction != null) {
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

        // call event
        faction = new Faction(name, player);
        CreateFactionEvent event = AlpineFactions.callEvent(new CreateFactionEvent(faction, player));
        if (event.isCancelled()) {
            messageConfig.operationCancelled.send(player);
            return;
        }

        // register the faction
        factions.register(faction);
        messageConfig.create.send(player, "faction_name", name);
    }
}
