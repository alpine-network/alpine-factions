package co.crystaldev.factions.command.claiming;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.ClaimTypeArgumentResolver;
import co.crystaldev.factions.command.argument.FactionArgumentResolver;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.util.ChunkCoordinate;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.LocationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@Command(name = "factions claim")
@Description("Claim faction territory.")
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
        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);
        Faction claimingFaction = faction.orElse(FactionStore.getInstance().findFaction(player));

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, claimingFaction, true)) {
            return;
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks;
        switch (type) {
            case LINE: {
                chunks = Claiming.line(origin, radius, LocationHelper.getFacing(player.getLocation()));
                break;
            }
            case CIRCLE: {
                chunks = Claiming.circle(origin, radius);
                break;
            }
            default: {
                chunks = Claiming.square(origin, radius);
            }
        }

        // attempt the claim
        Claiming.attemptClaim(player, type.toString(), replacedFaction, claimingFaction, chunks, origin);
    }

    @Execute(name = "fill", aliases = "f")
    public void fill(
            @Context Player player,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);
        Faction claimingFaction = faction.orElse(FactionStore.getInstance().findFaction(player));

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, claimingFaction, true)) {
            return;
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks = Claiming.fill(origin);
        if (chunks == null) {
            config.fillLimit.send(player, "limit", FactionConfig.getInstance().maxClaimFillVolume);
            return;
        }

        // attempt to claim
        Claiming.attemptClaim(player, "fill", replacedFaction, claimingFaction, chunks, origin);
    }

    @Execute(name = "one", aliases = "o")
    public void one(
            @Context Player player,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);
        Faction claimingFaction = faction.orElse(FactionStore.getInstance().findFaction(player));

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, claimingFaction, true)) {
            return;
        }

        // attempt to claim
        Set<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(new ChunkCoordinate(origin.getX(), origin.getZ())));
        Claiming.attemptClaim(player, "square", replacedFaction, claimingFaction, chunks, origin);
    }

    @Execute(name = "near")
    public void claimNear(
            @Context Player player,
            @Arg("x") int chunkX,
            @Arg("z") int chunkZ
    ) {
        Faction replacedFaction = ClaimStore.getInstance().getFaction(player.getWorld().getName(), chunkX, chunkZ);
        Faction claimingFaction = FactionStore.getInstance().findFaction(player);

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, claimingFaction, true)) {
            return;
        }

        // attempt to claim
        Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
        HashSet<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(new ChunkCoordinate(chunkX, chunkZ)));
        Claiming.attemptClaim(player, "near", replacedFaction, claimingFaction, chunks, chunk);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(
            @Context Player player,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction claimingFaction = faction.orElse(FactionStore.getInstance().findFactionOrDefault(player));

        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggleAutoClaim(claimingFaction);

        if (autoClaim.isAutoClaim()) {
            config.enableAutoClaim.send(player,
                    "faction", FactionHelper.formatRelational(player, claimingFaction),
                    "faction_name", claimingFaction.getName());

            // attempt to claim the chunk the player is standing in
            this.one(player, Optional.of(claimingFaction));
        }
        else {
            config.disableAutoSetting.send(player);
        }
    }
}
