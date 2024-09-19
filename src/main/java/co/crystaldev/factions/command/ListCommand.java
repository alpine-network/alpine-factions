package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * @since 0.1.0
 */
@Command(name = "factions list")
@Description("List all factions.")
final class ListCommand extends FactionsCommand {
    public ListCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> humanPage
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        int page = Math.max(1, humanPage.orElse(1));

        // sort factions from greatest to least online members
        List<Faction> factions = new LinkedList<>(Factions.get().factions().get());
        factions.sort((o1, o2) -> o2.countMembers() - o1.countMembers());

        // build the page
        Component compiledPage = Formatting.page(config.listTitle.build(), factions, "/f list %page%", page, 10, faction -> {
            boolean wilderness = faction.isWilderness();
            String factionName = wilderness ? "Factionless" : faction.getName();
            Component formattedFactionName = wilderness
                    ? ComponentHelper.mini("<gray>Factionless</gray>")
                    : FactionHelper.formatRelational(sender, faction, faction.getName());

            boolean infinite = faction.hasInfinitePower();
            int land = faction.getClaimCount();

            return config.listEntry.build(
                    "faction", formattedFactionName,
                    "faction_name", factionName,
                    "online", faction.countOnlineMembers(),
                    "members", faction.countMembers(),
                    "land", land,
                    "power", infinite ? "∞" : faction.getPowerLevel(),
                    "max_power", infinite ? "∞" : faction.getMaxPowerLevel()
            );
        });

        // send the page
        Messaging.send(sender, compiledPage);
    }
}
