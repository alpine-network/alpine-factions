package co.crystaldev.factions.command.claiming;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.ClaimTypeArgumentResolver;
import co.crystaldev.factions.command.argument.FactionArgumentResolver;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.util.LocationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@Command(name = "factions claim")
public final class ClaimCommand extends BaseFactionsCommand {
    public ClaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key(ClaimTypeArgumentResolver.KEY) ClaimType type,
            @Arg("radius") int radius,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {

        Chunk chunk = player.getLocation().getChunk();
        Set<Chunk> chunks;
        switch (type) {
            case LINE: {
                chunks = Claiming.line(chunk, radius, LocationHelper.getFacing(player.getLocation()));
                break;
            }
            case CIRCLE: {
                chunks = Claiming.circle(chunk, radius);
            }
            default: {
                chunks = Claiming.square(chunk, radius);
            }
        }


    }

    @Execute(name = "auto", aliases = "a")
    public void auto(@Context Player player) {

    }

    @Execute(name = "one", aliases = "o")
    public void one(@Context Player player) {

    }

    @Execute(name = "near")
    public void claimNear(
            @Context Player player,
            @Arg("x") int chunkX,
            @Arg("z") int chunkZ
    ) {

    }
}
