package co.crystaldev.factions.api.show;

import co.crystaldev.factions.api.faction.Faction;
import lombok.Data;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
@Data
public final class ShowContext {
    private final CommandSender sender;

    private final Faction faction;

    private final Faction senderFaction;
}
