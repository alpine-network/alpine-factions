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
import co.crystaldev.factions.api.event.FactionRelationUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.RelatedFaction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.StyleConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions relation")
@Description("Manage faction relations.")
final class RelationCommand extends AlpineCommand {
    public RelationCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("target_faction") Faction target,
            @Arg("relation") FactionRelation relation,
            @Arg("faction") Optional<Faction> faction
    ) {
        setRelation(sender, target, faction.orElse(Factions.registry().findOrDefault(sender)), relation);
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction target = targetFaction.orElse(Factions.registry().findOrDefault(sender));

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, target,
                Permissions.MODIFY_RELATIONS, "manage relations");
        if (!permitted) {
            return;
        }

        String command = "/f relation list %page% " + target.getName();
        Component title = config.relationListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, target),
                "faction_name", target.getName());
        Component compiledPage = Formatting.page(title, target.getRelatedFactions(),
                command, page.orElse(1), 10, entry -> relatedFactionToEntry(sender, entry));
        Messaging.send(sender, compiledPage);
    }

    @Execute(name = "wishes")
    public void wishes(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction target = targetFaction.orElse(Factions.registry().findOrDefault(sender));

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, target,
                Permissions.MODIFY_RELATIONS, "manage relations");
        if (!permitted) {
            return;
        }

        String command = "/f relation list %page% " + target.getName();
        Component compiledPage = Formatting.page(config.relationWishListTitle.build(), target.getRelationWishes(),
                command, page.orElse(1), 10, entry -> relatedFactionToEntry(sender, entry));
        Messaging.send(sender, compiledPage);
    }

    private static @NotNull Component relatedFactionToEntry(@NotNull CommandSender sender, @NotNull RelatedFaction entry) {
        Faction faction = entry.getFaction();
        FactionRelation relation = entry.getRelation();

        return AlpineFactions.getInstance().getConfiguration(MessageConfig.class).relationListEntry.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, faction),
                "faction_name", faction.getName(),
                "relation", Components.stylize(AlpineFactions.getInstance().getConfiguration(StyleConfig.class).relationalStyles.get(relation), Component.text(relation.name().toLowerCase())),
                "relation_name", relation.name().toLowerCase()
        );
    }

    private static void setRelation(@NotNull CommandSender sender, @NotNull Faction targetFaction,
                                    @NotNull Faction actingFaction, @NotNull FactionRelation relation) {
        MessageConfig messageConfig = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        FactionConfig factionConfig = AlpineFactions.getInstance().getConfiguration(FactionConfig.class);
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, actingFaction,
                Permissions.MODIFY_RELATIONS, "manage relations");
        if (!permitted) {
            return;
        }

        if (actingFaction.equals(targetFaction)) {
            messageConfig.relationSelf.send(sender,
                    "faction", RelationHelper.formatLiteralFactionName(sender, targetFaction),
                    "faction_name", targetFaction.getName());
            return;
        }

        if (actingFaction.isRelationWish(targetFaction, relation)) {
            messageConfig.alreadyRelation.send(sender,
                    "faction", RelationHelper.formatLiteralFactionName(sender, targetFaction),
                    "faction_name", targetFaction.getName());
            return;
        }

        // ensure the ally/truce count is within the limit
        int limit = relation == FactionRelation.ALLY ? factionConfig.maxAlliances
                : relation == FactionRelation.TRUCE ? factionConfig.maxTruces
                : -1;
        if (!overriding && limit >= 0 && actingFaction.getRelatedFactions(relation).size() >= limit) {
            (relation == FactionRelation.ALLY ? messageConfig.allyLimit : messageConfig.truceLimit).send(sender, "limit", limit);
            return;
        }

        // call event
        FactionRelationUpdateEvent event = AlpineFactions.callEvent(
                new FactionRelationUpdateEvent(actingFaction, targetFaction, sender, relation));
        relation = event.getRelation();
        if (event.isCancelled() || relation == FactionRelation.SELF) {
            messageConfig.operationCancelled.send(sender);
            return;
        }

        // set the relation wish internally
        actingFaction.setRelation(targetFaction, relation);

        // if override mode is enabled, force set this relation
        boolean force = PlayerHandler.getInstance().isOverriding(sender);
        if (force) {
            targetFaction.setRelation(actingFaction, relation);
        }

        // notify the target faction
        boolean wish = !force && !targetFaction.isRelation(actingFaction, relation)
                && relation.getWeight() > targetFaction.relationWishTo(actingFaction).getWeight();
        ConfigText targetMessage = (wish ? messageConfig.relationWishes : messageConfig.relationDeclarations).get(relation);
        FactionHelper.broadcast(targetFaction, observer -> {
            return targetMessage.build(
                    "faction", RelationHelper.formatLiteralFactionName(observer, actingFaction),
                    "faction_name", actingFaction.getName()
            );
        });

        // notify the declaring faction
        ConfigText actingMessage = (wish ? messageConfig.relationRequest : messageConfig.relationDeclarations).get(relation);
        FactionHelper.broadcast(actingFaction, sender, observer -> {
            return actingMessage.build(
                    "faction", RelationHelper.formatLiteralFactionName(observer, targetFaction),
                    "faction_name", targetFaction.getName()
            );
        });
    }

    @Command(name = "factions neutral")
    @Description("Declare neutrality between factions.")
    public static final class Neutral extends AlpineCommand {
        public Neutral(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") Faction targetFaction,
                @Arg("faction") Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(Factions.registry().findOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.NEUTRAL);
        }
    }

    @Command(name = "factions enemy")
    @Description("Declare a faction as an enemy.")
    public static final class Enemy extends AlpineCommand {
        public Enemy(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") Faction targetFaction,
                @Arg("faction") Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(Factions.registry().findOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.ENEMY);
        }
    }

    @Command(name = "factions ally")
    @Description("Declare an alliance between factions.")
    public static final class Ally extends AlpineCommand {
        public Ally(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") Faction targetFaction,
                @Arg("faction") Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(Factions.registry().findOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.ALLY);
        }
    }

    @Command(name = "factions truce")
    @Description("Declare a truce between factions.")
    public static final class Truce extends AlpineCommand {
        public Truce(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") Faction targetFaction,
                @Arg("faction") Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(Factions.registry().findOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.TRUCE);
        }
    }
}
