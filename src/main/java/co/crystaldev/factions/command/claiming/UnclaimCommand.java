package co.crystaldev.factions.command.claiming;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.ClaimTypeArgumentResolver;
import co.crystaldev.factions.command.argument.FactionArgumentResolver;
import co.crystaldev.factions.command.argument.WorldClaimArgumentResolver;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.ChunkCoordinate;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.LocationHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@Command(name = "factions unclaim")
@Description("Unclaim faction territory.")
public final class UnclaimCommand extends BaseFactionsCommand {
    public UnclaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key(ClaimTypeArgumentResolver.KEY) ClaimType type,
            @OptionalArg("radius") Optional<Integer> rad
    ) {
        int radius = Math.max(rad.orElse(1), 1);

        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, null, false)) {
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
        Claiming.attemptClaim(player, type.toString(), replacedFaction, null, chunks, origin);
    }

    @Execute(name = "fill", aliases = "f")
    public void fill(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, null, false)) {
            return;
        }

        // discover chunks to claim
        Set<ChunkCoordinate> chunks = Claiming.fill(origin);
        if (chunks == null) {
            config.fillLimit.send(player, "limit", FactionConfig.getInstance().maxClaimFillVolume);
            return;
        }

        // attempt to claim
        Claiming.attemptClaim(player, "fill", replacedFaction, null, chunks, origin);
    }

    @Execute(name = "all")
    public void all(
            @Context Player player,
            @Arg("world") @Key(WorldClaimArgumentResolver.KEY) String world,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Faction faction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        if (!faction.isPermitted(player, Permissions.MODIFY_TERRITORY)) {
            FactionHelper.missingPermission(player, faction, "modify territory");
            return;
        }

        // fetch all chunks
        ClaimStore store = ClaimStore.getInstance();
        List<ClaimedChunk> claims;
        ConfigText message;
        String worldName;
        if (world.equals("all")) {
            claims = new ArrayList<>();
            for (World w : Bukkit.getWorlds()) {
                claims.addAll(store.getClaims(faction, w));
            }
            message = config.landClaimAll;
            worldName = "<red>all</red>";
        }
        else {
            World w = player.getWorld();
            claims = store.getClaims(faction, w);
            message = config.landClaimWorld;
            worldName = w.getName();
        }

        // unclaim all chunks
        claims.forEach(claim -> store.removeClaim(claim.getWorld(), claim.getChunkX(), claim.getChunkZ()));
        store.saveClaims();

        // notify
        Faction wilderness = FactionStore.getInstance().getWilderness();
        Faction playerFaction = FactionStore.getInstance().findFactionOrDefault(player);
        Messaging.broadcast(faction, player, pl -> {
            return message.build(
                    "player", FactionHelper.formatRelational(pl, playerFaction, player),
                    "player_name", player.getName(),

                    "faction", FactionHelper.formatRelational(pl, faction),
                    "faction_name", faction.getName(),
                    "amount", claims.size(),
                    "claim_type", config.unclaimed.build(),
                    "world", worldName,

                    "old_faction", FactionHelper.formatRelational(pl, faction),
                    "old_faction_name", faction.getName(),

                    "new_faction", FactionHelper.formatRelational(pl, wilderness),
                    "new_faction_name", wilderness.getName()
            );
        });
    }

    @Execute(name = "one", aliases = "o")
    public void one(@Context Player player) {
        Chunk origin = player.getLocation().getChunk();
        Faction replacedFaction = ClaimStore.getInstance().getFaction(origin);

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, null, false)) {
            return;
        }

        // attempt to claim
        Set<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(new ChunkCoordinate(origin.getX(), origin.getZ())));
        Claiming.attemptClaim(player, "square", replacedFaction, null, chunks, origin);
    }

    @Execute(name = "near")
    public void claimNear(
            @Context Player player,
            @Arg("x") int chunkX,
            @Arg("z") int chunkZ
    ) {
        Faction replacedFaction = ClaimStore.getInstance().getFaction(player.getWorld().getName(), chunkX, chunkZ);

        // is the player able to claim this land?
        if (Claiming.shouldCancelClaim(player, replacedFaction, null, false)) {
            return;
        }

        // attempt to claim
        Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
        HashSet<ChunkCoordinate> chunks = new HashSet<>(Collections.singleton(new ChunkCoordinate(chunkX, chunkZ)));
        Claiming.attemptClaim(player, "near", replacedFaction, null, chunks, chunk);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggleAutoUnclaim();

        if (autoClaim.isAutoUnclaim()) {
            Faction wilderness = FactionStore.getInstance().getWilderness();
            config.enableAutoUnclaim.send(player,
                    "faction", FactionHelper.formatRelational(player, wilderness),
                    "faction_name", wilderness.getName());

            // attempt to unclaim the chunk the player is standing in
            this.one(player);
        }
        else {
            config.disableAutoSetting.send(player);
        }
    }
}
