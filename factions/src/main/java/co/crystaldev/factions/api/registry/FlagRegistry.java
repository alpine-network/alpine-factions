/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.registry;

import co.crystaldev.factions.api.faction.flag.FactionFlag;
import com.google.common.collect.ImmutableList;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 0.1.0
 */
public final class FlagRegistry {

    private final Map<Plugin, Set<FactionFlag<?>>> registeredFlags = new HashMap<>();

    public void register(@NotNull Plugin plugin, @NotNull FactionFlag<?> flag) {
        this.getFlags(plugin).add(flag);
    }

    public void unregister(@NotNull Plugin plugin, @NotNull FactionFlag<?> flag) {
        this.getFlags(plugin).remove(flag);
    }

    public void unregister(@NotNull Plugin plugin) {
        this.registeredFlags.remove(plugin);
    }

    public @NotNull Plugin getPluginForFlag(@NotNull FactionFlag<?> flag) {
        for (Map.Entry<Plugin, Set<FactionFlag<?>>> entry : this.registeredFlags.entrySet()) {
            Plugin plugin = entry.getKey();
            Set<FactionFlag<?>> flags = entry.getValue();
            if (flags.contains(flag)) {
                return plugin;
            }
        }

        throw new IllegalStateException("flag was not registered");
    }

    public @NotNull List<FactionFlag<?>> getAll() {
        List<FactionFlag<?>> flags = new ArrayList<>();
        this.registeredFlags.forEach((plugin, flagSet) -> flags.addAll(flagSet));
        flags.sort(Comparator.comparing(FactionFlag::getId));
        return ImmutableList.copyOf(flags);
    }

    public @NotNull List<FactionFlag<?>> getAll(@NotNull Permissible permissible) {
        List<FactionFlag<?>> flags = new ArrayList<>();
        this.registeredFlags.forEach((plugin, flagSet) -> flags.addAll(flagSet));
        flags.removeIf(f -> f.getPermission() != null && !permissible.hasPermission(f.getPermission()));
        flags.sort(Comparator.comparing(FactionFlag::getId));
        return ImmutableList.copyOf(flags);
    }

    private @NotNull Set<FactionFlag<?>> getFlags(@NotNull Plugin plugin) {
        return this.registeredFlags.computeIfAbsent(plugin, pl -> new HashSet<>());
    }
}
