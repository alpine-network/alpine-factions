package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/11/2024
 */
@Command(name = "factions list")

public final class ListCommand extends FactionsCommand {
    public ListCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> humanPage
    ) {
        MessageConfig config = MessageConfig.getInstance();
        int page = Math.max(1, humanPage.orElse(1));

        // sort factions from greatest to least online members
        List<Faction> factions = new LinkedList<>(FactionStore.getInstance().getAllFactions());
        factions.sort((o1, o2) -> o2.countMembers() - o1.countMembers());

        // build the page
        Component compiledPage = Formatting.page(config.listTitle.build(), factions, "/f list %page%", page, 10, faction -> {
            if (faction == null) {
                return Component.text("< null >");
            }

            boolean wilderness = faction.getId().equals(FactionStore.WILDERNESS_ID);
            String factionName = wilderness ? "Factionless" : faction.getName();
            Component formattedFactionName = wilderness
                    ? ComponentHelper.mini("<gray>Factionless</gray>")
                    : FactionHelper.formatRelational(sender, faction, faction.getName());

            boolean infinite = faction.hasInfinitePower();
            int land = faction.getClaimCount();
            long power = infinite ? land + 1 : faction.getPowerLevel();
            long maxPower = infinite ? land + 1 : faction.getMaxPowerLevel();

            return config.listEntry.build(
                    "faction", formattedFactionName,
                    "faction_name", factionName,
                    "online", faction.countOnlineMembers(),
                    "members", faction.countMembers(),
                    "land", land,
                    "power", power,
                    "max_power", maxPower
            );
        });

        // send the page
        Messaging.send(sender, compiledPage);
    }
}
