package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.util.claims.Claiming;
import co.crystaldev.factions.util.claims.WorldClaimType;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions unclaim")
@Description("Unclaim faction territory.")
final class UnclaimCommand extends AlpineCommand {
    public UnclaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") ClaimType type,
            @OptionalArg("radius") Optional<Integer> rad
    ) {
        Faction actingFaction = Factions.registry().findOrDefault(player);
        Claiming.mode(player, actingFaction, null, type, Math.max(rad.orElse(1), 1));
    }

    @Execute(name = "fill", aliases = "f")  @Async
    public void fill(@Context Player player) {
        Faction actingFaction = Factions.registry().findOrDefault(player);
        Claiming.fill(player, actingFaction, null);
    }

    @Execute(name = "one", aliases = "o")
    public void one(@Context Player player) {
        FactionAccessor factions = Factions.registry();
        Claiming.one(player, factions.findOrDefault(player), null);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggle(null);

        if (autoClaim.isEnabled()) {
            Faction wilderness = Factions.registry().getWilderness();
            config.enableAutoUnclaim.send(player,
                    "faction", RelationHelper.formatFactionName(player, wilderness),
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
            @Arg("world") WorldClaimType world,
            @Arg("faction") Faction faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, faction,
                Permissions.MODIFY_TERRITORY, "modify territory");
        if (!permitted) {
            return;
        }

        // fetch all chunks
        ClaimAccessor claims = Factions.claims();
        List<ClaimedChunk> foundClaims;
        ConfigText message;
        String worldName;
        if (world == WorldClaimType.WORLD) {
            foundClaims = new ArrayList<>();
            for (World w : Bukkit.getWorlds()) {
                foundClaims.addAll(claims.getClaims(faction, w));
            }
            message = config.landClaimAll;
            worldName = "<red>all</red>";
        }
        else {
            World w = player.getWorld();
            foundClaims = claims.getClaims(faction, w);
            message = config.landClaimWorld;
            worldName = w.getName();
        }

        // unclaim all chunks
        foundClaims.forEach(claim -> claims.remove(claim.getWorld(), claim.getX(), claim.getZ()));

        // notify
        Faction wilderness = Factions.registry().getWilderness();
        Faction playerFaction = Factions.registry().findOrDefault(player);
        FactionHelper.broadcast(faction, player, observer -> {
            return message.build(
                    "actor", RelationHelper.formatPlayerName(observer, player),
                    "actor_name", player.getName(),

                    "faction", RelationHelper.formatFactionName(observer, faction),
                    "faction_name", faction.getName(),
                    "amount", foundClaims.size(),
                    "claim_type", config.unclaimed.build(),
                    "world", worldName,

                    "old_faction", RelationHelper.formatFactionName(observer, faction),
                    "old_faction_name", faction.getName(),

                    "new_faction", RelationHelper.formatFactionName(observer, wilderness),
                    "new_faction_name", wilderness.getName()
            );
        });
    }
}
