package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.CreateFactionEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.Args;
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
 * @since 0.1.0
 */
@Command(name = "factions create")
@Description("Create a new faction.")
final class CreateCommand extends AlpineCommand {
    public CreateCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name
    ) {
        MessageConfig messageConfig = this.plugin.getConfiguration(MessageConfig.class);
        FactionConfig factionConfig = this.plugin.getConfiguration(FactionConfig.class);
        FactionAccessor factions = Factions.registry();

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
