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
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.util.RelationHelper;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.util.claims.Claiming;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions claim")
@Description("Claim faction territory.")
final class ClaimCommand extends AlpineCommand {
    public ClaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player sender,
            @Arg("type") ClaimType type,
            @Arg("radius") Optional<Integer> rad,
            @Arg("faction") Optional<Faction> faction
    ) {
        FactionAccessor factions = Factions.registry();
        Faction claimingFaction = faction.orElse(factions.find(sender));
        Faction actingFaction = faction.orElse(factions.findOrDefault(sender));
        Claiming.mode(sender, actingFaction, claimingFaction, type, Math.max(rad.orElse(1), 1));
    }

    @Execute(name = "fill", aliases = "f") @Async
    public void fill(
            @Context Player sender,
            @Arg("faction") Optional<Faction> faction
    ) {
        FactionAccessor factions = Factions.registry();
        Faction claimingFaction = faction.orElse(factions.find(sender));
        Faction actingFaction = faction.orElse(factions.findOrDefault(sender));
        Claiming.fill(sender, actingFaction, claimingFaction);
    }

    @Execute(name = "one", aliases = "o")
    public void one(
            @Context Player sender,
            @Arg("faction") Optional<Faction> faction
    ) {
        FactionAccessor factions = Factions.registry();
        Faction claimingFaction = faction.orElse(factions.find(sender));
        Faction actingFaction = faction.orElse(factions.findOrDefault(sender));
        Claiming.one(sender, actingFaction, claimingFaction);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(
            @Context Player sender,
            @Arg("faction") Optional<Faction> faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction claimingFaction = faction.orElse(Factions.registry().findOrDefault(sender));

        PlayerState state = PlayerHandler.getInstance().getPlayer(sender);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggle(claimingFaction);

        if (autoClaim.isEnabled()) {
            config.enableAutoClaim.send(sender,
                    "faction", RelationHelper.formatFactionName(sender, claimingFaction),
                    "faction_name", claimingFaction.getName());

            // attempt to claim the chunk the player is standing in
            this.one(sender, Optional.of(claimingFaction));
        }
        else {
            config.disableAutoSetting.send(sender);
        }
    }
}
