package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.ClaimTypeArgumentResolver;
import co.crystaldev.factions.command.argument.FactionArgumentResolver;
import co.crystaldev.factions.command.argument.WorldClaimArgumentResolver;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.util.claims.Claiming;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@Command(name = "factions unclaim")
@Description("Unclaim faction territory.")
public final class UnclaimCommand extends FactionsCommand {
    public UnclaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key(ClaimTypeArgumentResolver.KEY) ClaimType type,
            @OptionalArg("radius") Optional<Integer> rad
    ) {
        Faction actingFaction = FactionStore.getInstance().findFactionOrDefault(player);
        Claiming.mode(player, actingFaction, null, type, Math.max(rad.orElse(1), 1));
    }

    @Execute(name = "fill", aliases = "f")
    public void fill(@Context Player player) {
        Faction actingFaction = FactionStore.getInstance().findFactionOrDefault(player);
        Claiming.fill(player, actingFaction, null);
    }

    @Execute(name = "one", aliases = "o")
    public void one(@Context Player player) {
        FactionStore store = FactionStore.getInstance();
        Claiming.one(player, store.findFactionOrDefault(player), null);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggle(null);

        if (autoClaim.isEnabled()) {
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
                    "actor", FactionHelper.formatRelational(pl, playerFaction, player),
                    "actor_name", player.getName(),

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
}
