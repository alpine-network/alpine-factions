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
import co.crystaldev.factions.api.registry.PermissionRegistry;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.event.FactionPermissionUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions permission", aliases = { "factions perm" })
final class PermissionCommand extends AlpineCommand {
    public PermissionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    @Description("Set the faction permission")
    public void set(
            @Context CommandSender sender,
            @Arg("permission") Permission permission,
            @Arg("relational") Relational relational,
            @Arg("value") boolean value,
            @Arg("faction") Optional<Faction> faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction resolvedFaction = faction.orElse(Factions.registry().findOrDefault(sender));
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, resolvedFaction,
                Permissions.MODIFY_PERMS, "modify permissions");
        if (!permitted) {
            return;
        }

        // call event
        FactionPermissionUpdateEvent event = AlpineFactions.callEvent(new FactionPermissionUpdateEvent(
                resolvedFaction, sender, permission, relational, value));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        boolean newValue = event.isAllowed();
        resolvedFaction.setPermission(permission, newValue, relational);

        FactionHelper.broadcast(resolvedFaction, sender, observer -> {
            return config.updatedPermissionValue.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),

                    "permission_id", permission.getId(),
                    "permission_name", permission.getName(),
                    "permission_description", permission.getDescription(),
                    "relation", relational.getId(),
                    "state", newValue
            );
        });
    }

    @Execute(name = "show")
    @Description("View a list of faction permissions")
    public void show(
            @Context CommandSender sender,
            @Arg("faction") Optional<Faction> faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        PermissionRegistry registry = AlpineFactions.getInstance().permissions();

        Faction resolvedFaction = faction.orElse(Factions.registry().findOrDefault(sender));
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, resolvedFaction,
                Permissions.MODIFY_PERMS, "modify permissions");
        if (!permitted) {
            return;
        }

        List<Permission> permissions = registry.getAll();
        Component title = config.permissionStateListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, resolvedFaction),
                "faction_name", resolvedFaction.getName());
        Component compiledTitle = Components.joinNewLines(
                Formatting.applyTitlePadding(title),
                config.permissionRelationStateTitle.build()
        );

        Component elements = Formatting.elements(permissions, permission -> {
            Component relationStates = config.permissionRelationState.build(
                    "enemy", (resolvedFaction.isPermitted(FactionRelation.ENEMY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "neutral", (resolvedFaction.isPermitted(FactionRelation.ENEMY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "truce", (resolvedFaction.isPermitted(FactionRelation.TRUCE, permission) ? config.permissionYes : config.permissionNo).build(),
                    "ally", (resolvedFaction.isPermitted(FactionRelation.ALLY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "recruit", (resolvedFaction.isPermitted(Rank.RECRUIT, permission) ? config.permissionYes : config.permissionNo).build(),
                    "member", (resolvedFaction.isPermitted(Rank.MEMBER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "officer", (resolvedFaction.isPermitted(Rank.OFFICER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "coleader", (resolvedFaction.isPermitted(Rank.COLEADER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "leader", (resolvedFaction.isPermitted(Rank.LEADER, permission) ? config.permissionYes : config.permissionNo).build()
            );

            return config.permissionStateListEntry.build(
                    "permission_id", permission.getId(),
                    "permission_name", permission.getName(),
                    "permission_description", permission.getDescription(),
                    "relation_states", relationStates
            );
        });

        Messaging.send(sender, Components.joinNewLines(compiledTitle, elements));
    }

    @Execute(name = "list")
    @Description("View a formatted list of faction permissions")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        PermissionRegistry registry = AlpineFactions.getInstance().permissions();

        List<Permission> permissions = registry.getAll();

        String command = "/f permission list %page%";
        Component title = config.permissionListTitle.build();
        Component compiledPage = Formatting.page(title, permissions, command, page.orElse(1), 10, perm -> {
            return config.permissionListEntry.build(
                    "permission_id", perm.getId(),
                    "permission_name", perm.getName(),
                    "permission_description", perm.getDescription()
            );
        });

        Messaging.send(sender, compiledPage);
    }
}
