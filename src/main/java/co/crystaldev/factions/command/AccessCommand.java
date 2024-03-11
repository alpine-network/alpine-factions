package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/10/2024
 */
@Command(name = "factions access")
@Description("Manage chunk access.")
public final class AccessCommand extends FactionsCommand {
    public AccessCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "player")
    public void player(
            @Context Player player,
            @Arg("faction") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("access") boolean access
    ) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Accessors.factions();
        ClaimAccessor claims = Accessors.claims();

        Location location = player.getLocation();
        Claim claim = claims.getClaim(location);

        // ensure the chunk is claimed
        if (claim == null) {
            FactionHelper.missingPermission(player, factions.getWilderness(), "grant access");
            return;
        }

        // ensure the player has permission for the faction
        Faction claimedFaction = claim.getFaction();
        Faction otherFaction = factions.findOrDefault(other);
        if (!claimedFaction.isPermitted(player, Permissions.MODIFY_ACCESS)) {
            FactionHelper.missingPermission(player, claimedFaction, "grant access");
            return;
        }

        // set access
        claim.setAccess(other, access);
        claims.save(location);

        // notify
        ConfigText message = access ? config.accessGranted : config.accessRevoked;
        message.send(player,
                "subject", FactionHelper.formatRelational(player, otherFaction, other, false),
                "subject_name", other.getName());
    }

    @Execute(name = "faction")
    public void faction(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Faction faction,
            @Arg("access") boolean access
    ) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Accessors.factions();
        ClaimAccessor claims = Accessors.claims();

        Location location = player.getLocation();
        Claim claim = claims.getClaim(location);

        // ensure the chunk is claimed
        if (claim == null) {
            FactionHelper.missingPermission(player, factions.getWilderness(), "grant access");
            return;
        }

        // ensure the player has permission for the faction
        Faction claimedFaction = claim.getFaction();
        if (!claimedFaction.isPermitted(player, Permissions.MODIFY_ACCESS)) {
            FactionHelper.missingPermission(player, claimedFaction, "grant access");
            return;
        }

        // set access
        claim.setAccess(faction, access);
        claims.save(location);

        // notify
        ConfigText message = access ? config.accessGranted : config.accessRevoked;
        message.send(player,
                "subject", FactionHelper.formatRelational(player, faction, false),
                "subject_name", faction.getName());
    }

    @Execute(name = "show")
    public void show(@Context Player player) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Accessors.factions();
        ClaimAccessor claims = Accessors.claims();

        Location location = player.getLocation();
        Claim claim = claims.getClaim(location);

        Faction claimedFaction = claim == null ? factions.getWilderness() : claim.getFaction();

        Set<FPlayer> permittedPlayers = claim == null ? null : claim.getPlayers();
        Component compiledPlayers = claim == null || permittedPlayers.isEmpty()
                ? config.none.build()
                : Components.joinCommas(permittedPlayers.stream()
                    .map(v -> Component.text(v.getOfflinePlayer().getName()))
                    .collect(Collectors.toSet()));

        Set<Faction> permittedFactions = claim == null ? null : claim.getFactions();
        Component compiledFactions = claim == null || permittedFactions.isEmpty()
                ? config.none.build()
                : Components.joinCommas(permittedFactions.stream()
                    .map(v -> FactionHelper.formatRelational(player, v, false))
                    .collect(Collectors.toSet()));

        Messaging.send(player, Components.joinNewLines(
                Formatting.title(config.accessViewTitle.build(
                        "world", location.getWorld().getName(),
                        "chunk_x", location.getBlockX() >> 4,
                        "chunk_z", location.getBlockZ() >> 4)),
                config.accessViewBody.build(
                        "faction", FactionHelper.formatRelational(player, claimedFaction, false),
                        "players", compiledPlayers,
                        "factions", compiledFactions)
        ));
    }
}
