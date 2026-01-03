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
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.ChunkAccessUpdateEvent;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions accessall")
final class AccessAllCommand extends AlpineCommand {
    public AccessAllCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "player", aliases = "p")
    @Description("Manage a player access to faction")
    public void player(
            @Context CommandSender sender,
            @Arg("faction") @Async OfflinePlayer other,
            @Arg("access") boolean access,
            @Arg("target_faction") Optional<Faction> targetFaction
    ) {
        FactionAccessor factions = Factions.registry();
        Faction target = targetFaction.orElse(factions.findOrDefault(sender));
        if (target.isWilderness()) {
            PermissionHelper.notify(sender, target, "grant access");
            return;
        }

        Faction otherFaction = factions.findOrDefault(other);
        setAccess(sender, other, access, target,
                RelationHelper.formatLiteralPlayerName(sender, other),
                Component.text(other.getName()));
    }

    @Execute(name = "faction", aliases = "f")
    @Description("Manage a faction access to faction")
    public void faction(
            @Context CommandSender sender,
            @Arg("faction") Faction faction,
            @Arg("access") boolean access,
            @Arg("target_faction") Optional<Faction> targetFaction
    ) {
        Faction target = targetFaction.orElse(Factions.registry().findOrDefault(sender));
        if (target.isWilderness()) {
            PermissionHelper.notify(sender, target, "grant access");
            return;
        }

        setAccess(sender, faction, access, target,
                RelationHelper.formatLiteralFactionName(sender, faction),
                Component.text(faction.getName()));
    }

    private static void setAccess(@NotNull CommandSender sender, @NotNull Object subject, boolean access,
                                  @NotNull Faction targetFaction, @NotNull Component formattedSubject,
                                  @NotNull Component subjectName) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        ClaimAccessor claims = Factions.claims();

        // ensure the player has permission for the faction
        if (!targetFaction.isPermitted(sender, Permissions.MODIFY_ACCESS)) {
            PermissionHelper.notify(sender, targetFaction, "grant access");
            return;
        }

        List<ClaimedChunk> chunks = claims.getClaims(targetFaction);

        ChunkAccessUpdateEvent event = AlpineFactions.callEvent(new ChunkAccessUpdateEvent(targetFaction, sender, chunks, subject));
        if (event.isCancelled() || chunks.isEmpty()) {
            config.operationCancelled.send(sender);
            return;
        }

        if (chunks.size() == 1) {
            // handle a single chunk

            // set access
            ClaimedChunk chunk = chunks.get(0);
            setAccess(chunk.getClaim(), subject, access);
            claims.save(chunk.getWorld(), chunk.getX(), chunk.getZ());

            // notify
            ConfigText message = access ? config.accessGrantedSingle : config.accessRevokedSingle;
            message.send(sender,
                    "subject", formattedSubject,
                    "subject_name", subjectName);
        }
        else {
            // handle multiple chunks

            // set access
            String owningFaction = targetFaction.getId();
            for (ClaimedChunk chunk : chunks) {
                Claim claim = chunk.getClaim();
                if (claim == null || !Objects.equals(claim.getFactionId(), owningFaction)) {
                    continue;
                }

                // set access
                setAccess(claim, subject, access);
                claims.save(chunk.getWorld(), chunk.getX(), chunk.getZ());
            }

            // notify
            ConfigText message = access ? config.accessGrantedAll : config.accessRevokedAll;
            message.send(sender,
                    "subject", formattedSubject,
                    "subject_name", subjectName,
                    "amount", chunks.size());
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
