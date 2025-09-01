/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.show;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.show.component.ShowComponent;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.RelationHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
public final class ShowFormatter {

    private final List<ShowComponent> components = new LinkedList<>();

    public void register(@NotNull ShowComponent... components) {
        for (ShowComponent component : components) {
            this.addComponent(component);
        }
    }

    @ApiStatus.Internal
    public @NotNull Component build(@NotNull CommandSender sender, @NotNull Faction faction, @NotNull Faction senderFaction) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);

        ShowContext context = new ShowContext(sender, faction, senderFaction);
        Set<Component> components = this.components.stream()
                .filter(v -> overriding || v.isVisible(context))
                .map(v -> v.buildComponent(context))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Component title = config.showTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, faction),
                "faction_name", faction.getName());
        return Components.joinNewLines(Formatting.title(title), Components.joinNewLines(components));
    }

    private void addComponent(@NotNull ShowComponent component) {
        // if the component is already registered, remove and re-add it
        this.components.remove(component);

        int length = this.components.size();
        int index = component.getOrdering().computeIndex(this.components, length);

        // negative number was input, insert at the end
        if (index < 0) {
            this.components.add(component);
            return;
        }

        this.components.add(Math.min(length, index), component);
    }
}
