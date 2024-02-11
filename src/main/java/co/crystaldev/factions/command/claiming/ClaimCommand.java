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
            @Arg("radius") Optional<Integer> rad,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        int radius = Math.max(rad.orElse(1), 1);

        Chunk origin = player.getLocation().getChunk();
        FactionStore store = FactionStore.getInstance();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);
        Faction claimingFaction = faction.orElse(store.findFaction(player));
        Faction actingFaction = faction.orElse(store.findFactionOrDefault(player));

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
        Claiming.attemptClaim(player, type.toString(), actingFaction, claimingFaction, chunks, origin);
    }

    @Execute(name = "fill", aliases = "f")
    public void fill(
            @Context Player player,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Chunk origin = player.getLocation().getChunk();
        FactionStore store = FactionStore.getInstance();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);
        Faction claimingFaction = faction.orElse(store.findFaction(player));
        Faction actingFaction = faction.orElse(store.findFactionOrDefault(player));

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
        Claiming.attemptClaim(player, "fill", actingFaction, claimingFaction, chunks, origin);
    }

    @Execute(name = "one", aliases = "o")
    public void one(
            @Context Player player,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        FactionStore store = FactionStore.getInstance();
        Faction claimingFaction = faction.orElse(store.findFaction(player));
        Faction actingFaction = faction.orElse(store.findFactionOrDefault(player));
        Claiming.one(player, actingFaction, claimingFaction);
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
        autoClaim.toggle(claimingFaction);

        if (autoClaim.isEnabled()) {
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
