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
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.FactionMemberTitleUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Command(name = "factions title")
final class TitleCommand extends AlpineCommand {
    public TitleCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Description("Modify a faction member title")
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other,
            @Join("title") String title
    ) {
        Component parsedComponent = ComponentHelper.legacy(title);
        int maxLength = this.plugin.getConfiguration(FactionConfig.class).maxTitleLength;

        int length = title.length();
        while (Components.length(parsedComponent) > maxLength) {
            // trim down the component length while ignoring color codes
            parsedComponent = ComponentHelper.legacy(title.substring(0, length - 1));
            length--;
        }

        set(sender, other, parsedComponent);
    }

    @Execute(name = "clear")
    @Description("Clear a faction member title.")
    public void clear(
            @Context CommandSender sender,
            @Arg("player") @Async OfflinePlayer other
    ) {
        set(sender, other, null);
    }

    private static void set(@NotNull CommandSender sender, @NotNull OfflinePlayer other, @Nullable Component title) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = factions.findOrDefault(other);

        if (faction.isWilderness()) {
            config.playerNotInFaction.send(sender, "player", other.getName());
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, faction,
                Permissions.MODIFY_TITLE, "modify titles");
        if (!permitted) {
            return;
        }

        faction.wrapMember(other.getUniqueId(), member -> {
            FactionMemberTitleUpdateEvent event = AlpineFactions.callEvent(new FactionMemberTitleUpdateEvent(faction, member, title));
            if (event.isCancelled()) {
                config.operationCancelled.send(sender);
                return;
            }

            member.setTitle(event.getTitle());

            FactionHelper.broadcast(faction, sender, observer -> config.titleChange.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", RelationHelper.formatLiteralPlayerName(observer, other),
                    "player_name", other.getName(),
                    "title", title == null ? "none" : title
            ));
        });
    }
}
