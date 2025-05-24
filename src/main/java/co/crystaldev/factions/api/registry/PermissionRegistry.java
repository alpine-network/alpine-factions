package co.crystaldev.factions.api.registry;

import co.crystaldev.factions.api.faction.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @since 0.1.0
 */
public final class PermissionRegistry {

    private final Map<Plugin, List<Permission>> registeredPermissions = new HashMap<>();

    public boolean register(@NotNull Plugin plugin, @NotNull Permission permission) {
        return this.getPermissions(plugin).add(permission);
    }

    public void unregister(@NotNull Plugin plugin, @NotNull Permission permission) {
        this.getPermissions(plugin).remove(permission);
    }

    public void unregister(@NotNull Plugin plugin) {
        this.registeredPermissions.remove(plugin);
    }

    public @NotNull List<Permission> getAll() {
        List<Permission> permissions = new LinkedList<>();
        this.registeredPermissions.forEach((plugin, permSet) -> permissions.addAll(permSet));
        return permissions;
    }

    private @NotNull List<Permission> getPermissions(@NotNull Plugin plugin) {
        return this.registeredPermissions.computeIfAbsent(plugin, pl -> new LinkedList<>());
    }
}
