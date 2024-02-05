package co.crystaldev.factions.api;

import co.crystaldev.factions.api.faction.flag.FactionFlag;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
public final class FlagRegistry {

    private final Map<Plugin, Set<FactionFlag<?>>> registeredFlags = new HashMap<>();

    public boolean register(@NotNull Plugin plugin, @NotNull FactionFlag<?> flag) {
        return this.getFlags(plugin).add(flag);
    }

    public void unregister(@NotNull Plugin plugin, @NotNull FactionFlag<?> flag) {
        this.getFlags(plugin).remove(flag);
    }

    public void unregister(@NotNull Plugin plugin) {
        this.registeredFlags.remove(plugin);
    }

    @NotNull
    public Set<FactionFlag<?>> getAll() {
        Set<FactionFlag<?>> flags = new HashSet<>();
        this.registeredFlags.forEach((plugin, flagSet) -> flags.addAll(flagSet));
        return flags;
    }

    @NotNull
    private Set<FactionFlag<?>> getFlags(@NotNull Plugin plugin) {
        return this.registeredFlags.computeIfAbsent(plugin, pl -> new HashSet<>());
    }
}
