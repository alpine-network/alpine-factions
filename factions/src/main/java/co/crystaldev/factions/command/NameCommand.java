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
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.FactionNameUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions name")
final class NameCommand extends AlpineCommand {

    private final Map<CommandSender, Long> confirmationMap = new HashMap<>();

    public NameCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    @Description("Change the name of a faction")
    public void execute(
            @Context CommandSender sender,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionConfig factionConfig = this.plugin.getConfiguration(FactionConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = targetFaction.orElseGet(() -> factions.findOrDefault(sender));

        // ensure the user has permission
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender,
                Permissions.MODIFY_NAME, "modify name");
        if (!permitted) {
            return;
        }

        // ensure the name is different
        if (name.equals(faction.getName())) {
            config.factionNameUnchanged.send(sender);
            return;
        }

        // ensure there is no other faction with the same name
        Faction other = factions.getByName(name);
        if (other != null && !other.equals(faction)) {
            config.factionWithName.send(sender, "faction_name", name);
            return;
        }

        // ensure the name is long enough
        if (name.length() < factionConfig.minNameLength) {
            config.nameTooShort.send(sender, "length", factionConfig.minNameLength);
            return;
        }

        // ensure the name isn't too long
        if (name.length() > factionConfig.maxNameLength) {
            config.nameTooLong.send(sender, "length", factionConfig.maxNameLength);
            return;
        }

        // require a confirmation from the user
        if (!this.confirmationMap.containsKey(sender) || System.currentTimeMillis() - this.confirmationMap.get(sender) > 10_000L) {
            this.confirmationMap.put(sender, System.currentTimeMillis());
            config.confirm.send(sender);
            return;
        }

        // call event
        FactionNameUpdateEvent event = AlpineFactions.callEvent(new FactionNameUpdateEvent(faction, sender, name));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // rename the faction
        faction.setName(name);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.rename.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "faction_name", name);
        });
    }
}
