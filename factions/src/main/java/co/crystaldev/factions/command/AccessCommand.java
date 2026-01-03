/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.ChunkAccessUpdateEvent;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.*;
import co.crystaldev.factions.util.claims.ClaimHelper;
import co.crystaldev.factions.util.claims.ClaimType;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
@Command(name = "factions access")
final class AccessCommand extends AlpineCommand {
    public AccessCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "player", aliases = "p")
    @Description("Manage a players chunk access to chunk")
    public void player(
            @Context Player sender,
            @Arg("faction") @Async OfflinePlayer other,
            @Arg("access") boolean access,
            @Arg("type") Optional<ClaimType> optionalType,
            @Arg("radius") Optional<Integer> optionalRadius
    ) {
        setAccess(sender, other, access, optionalType, optionalRadius,
                RelationHelper.formatLiteralPlayerName(sender, other),
                Component.text(other.getName()));
    }

    @Execute(name = "faction", aliases = "f")
    @Description("Manage a factions chunk access to chunk")
    public void faction(
            @Context Player sender,
            @Arg("faction") Faction faction,
            @Arg("access") boolean access,
            @Arg("type") Optional<ClaimType> optionalType,
            @Arg("radius") Optional<Integer> optionalRadius
    ) {
        setAccess(sender, faction, access, optionalType, optionalRadius,
                RelationHelper.formatLiteralFactionName(sender, faction),
                Component.text(faction.getName()));
    }

    @Execute(name = "show", aliases = { "s", "view", "v" })
    @Description("View a chunks access")
    public void show(@Context Player sender) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        FactionAccessor factions = Factions.registry();
        ClaimAccessor claims = Factions.claims();

        Location location = sender.getLocation();
        Claim claim = claims.getClaim(location);

        Faction claimedFaction = claim == null ? factions.getWilderness() : claim.getFaction();

        Set<FPlayer> permittedPlayers = claim == null ? null : claim.getPlayers();
        Component compiledPlayers = claim == null || permittedPlayers.isEmpty()
                ? config.none.build()
                : Components.joinCommas(permittedPlayers.stream()
                    .map(v -> Component.text(v.getOfflinePlayer().getName()))
                    .collect(Collectors.toSet()));

        Set<Faction> permittedFactions = claim == null ? null : claim.getFactions();
        Component compiledFactions;
        if (claim == null || permittedFactions.isEmpty()) {
            compiledFactions = config.none.build();
        }
        else {
            compiledFactions = permittedFactions.stream()
                    .map(v -> RelationHelper.formatLiteralFactionName(sender, v))
                    .collect(Component.toComponent(Component.text(", ")));
        }

        Messaging.send(sender, Components.joinNewLines(
                Formatting.title(config.accessViewTitle.build(
                        "world", location.getWorld().getName(),
                        "chunk_x", location.getBlockX() >> 4,
                        "chunk_z", location.getBlockZ() >> 4)),
                config.accessViewBody.build(
                        "faction", RelationHelper.formatLiteralFactionName(sender, claimedFaction),
                        "players", compiledPlayers,
                        "factions", compiledFactions)
        ));
    }

    private static void setAccess(@NotNull Player player, @NotNull Object subject, boolean access,
                                      @NotNull Optional<ClaimType> optionalType, @NotNull Optional<Integer> optionalRadius,
                                      @NotNull Component formattedSubject, @NotNull Component subjectName) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);

        FactionAccessor factions = Factions.registry();
        ClaimAccessor claims = Factions.claims();

        Location location = player.getLocation();
        Claim claim = claims.getClaim(location);

        // ensure the origin chunk is claimed
        if (claim == null) {
            PermissionHelper.notify(player, factions.getWilderness(), "grant access");
            return;
        }

        // ensure the player has permission for the faction
        Faction claimedFaction = claim.getFaction();
        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, claimedFaction,
                Permissions.MODIFY_ACCESS, "grant access");
        if (!permitted) {
            PermissionHelper.notify(player, claimedFaction, "grant access");
            return;
        }

        Set<ChunkCoordinate> chunks;
        ClaimType type = ClaimType.SQUARE;
        Chunk origin = player.getLocation().getChunk();

        if (optionalType.isPresent() && optionalRadius.isPresent()) {
            // handle multiple claims

            type = optionalType.get();
            switch (type) {
                case CIRCLE: {
                    chunks = ClaimHelper.circle(origin, optionalRadius.get());
                    break;
                }
                case LINE: {
                    chunks = ClaimHelper.line(origin, optionalRadius.get(), LocationHelper.getFacing(player.getLocation()));
                    break;
                }
                default: {
                    int maxRadius = PlayerHandler.getInstance().isOverriding(player) ? -1 : 30;
                    chunks = ClaimHelper.square(origin, optionalRadius.get(), maxRadius);
                }
            }
        }
        else {
            chunks = Collections.singleton(ChunkCoordinate.of(origin.getX(), origin.getZ()));
        }

        String world = player.getWorld().getName();
        String owningFaction = claim.getFactionId();
        Set<ClaimedChunk> claimedChunks = chunks.stream()
                .map(ch -> new ClaimedChunk(claims.getClaim(world, ch), world, ch.getX(), ch.getZ()))
                .filter(ch -> ch.getClaim() != null && Objects.equals(ch.getClaim().getFactionId(), owningFaction))
                .collect(Collectors.toCollection(HashSet::new));

        ChunkAccessUpdateEvent event = AlpineFactions.callEvent(new ChunkAccessUpdateEvent(claimedFaction, player, claimedChunks, subject));
        if (event.isCancelled() || claimedChunks.isEmpty()) {
            config.operationCancelled.send(player);
            return;
        }

        if (claimedChunks.size() == 1) {
            // handle a single chunk

            // set access
            setAccess(claim, subject, access);
            claims.save(origin);

            // notify
            ConfigText message = access ? config.accessGrantedSingle : config.accessRevokedSingle;
            message.send(player,
                    "subject", formattedSubject,
                    "subject_name", subjectName);
        }
        else {
            // handle multiple chunks

            // set access
            for (ClaimedChunk chunk : claimedChunks) {
                setAccess(chunk.getClaim(), subject, access);
                claims.save(world, chunk.getX(), chunk.getZ());
            }

            // notify
            ConfigText message = access ? config.accessGranted : config.accessRevoked;
            message.send(player,
                    "subject", formattedSubject,
                    "subject_name", subjectName,
                    "amount", chunks.size(),
                    "world", world,
                    "chunk_x", origin.getX(),
                    "chunk_z", origin.getZ(),
                    "type", type);
        }
    }

    private static void setAccess(@NotNull Claim claim, @NotNull Object subject, boolean access) {
        if (subject instanceof Faction) {
            claim.setAccess((Faction) subject, access);
        }
        else if (subject instanceof OfflinePlayer) {
            claim.setAccess((OfflinePlayer) subject, access);
        }
    }
}
