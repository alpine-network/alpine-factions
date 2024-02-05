package co.crystaldev.factions.api;

import co.crystaldev.factions.api.faction.permission.Permission;
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
public final class PermissionRegistry {

    private final Map<Plugin, Set<Permission>> registeredPermissions = new HashMap<>();

    public boolean register(@NotNull Plugin plugin, @NotNull Permission permission) {
        return this.getPermissions(plugin).add(permission);
    }

    public void unregister(@NotNull Plugin plugin, @NotNull Permission permission) {
        this.getPermissions(plugin).remove(permission);
    }

    public void unregister(@NotNull Plugin plugin) {
        this.registeredPermissions.remove(plugin);
    }

    @NotNull
    public Set<Permission> getAll() {
        Set<Permission> permissions = new HashSet<>();
        this.registeredPermissions.forEach((plugin, permSet) -> permissions.addAll(permSet));
        return permissions;
    }

    @NotNull
    private Set<Permission> getPermissions(@NotNull Plugin plugin) {
        return this.registeredPermissions.computeIfAbsent(plugin, pl -> new HashSet<>());
    }
}
