package co.crystaldev.factions.api;

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

    @NotNull
    public Plugin getPluginForFlag(@NotNull FactionFlag<?> flag) {
        for (Map.Entry<Plugin, Set<FactionFlag<?>>> entry : this.registeredFlags.entrySet()) {
            Plugin plugin = entry.getKey();
            Set<FactionFlag<?>> flags = entry.getValue();
            if (flags.contains(flag)) {
                return plugin;
            }
        }

        throw new IllegalStateException("flag was not registered");
    }

    @NotNull
    public List<FactionFlag<?>> getAll() {
        List<FactionFlag<?>> flags = new ArrayList<>();
        this.registeredFlags.forEach((plugin, flagSet) -> flags.addAll(flagSet));
        flags.sort(Comparator.comparing(FactionFlag::getId));
        return ImmutableList.copyOf(flags);
    }

    @NotNull
    public List<FactionFlag<?>> getAll(@NotNull Permissible permissible) {
        List<FactionFlag<?>> flags = new ArrayList<>();
        this.registeredFlags.forEach((plugin, flagSet) -> flags.addAll(flagSet));
        flags.removeIf(f -> f.getPermission() != null && !permissible.hasPermission(f.getPermission()));
        flags.sort(Comparator.comparing(FactionFlag::getId));
        return ImmutableList.copyOf(flags);
    }

    @NotNull
    private Set<FactionFlag<?>> getFlags(@NotNull Plugin plugin) {
        return this.registeredFlags.computeIfAbsent(plugin, pl -> new HashSet<>());
    }
}
