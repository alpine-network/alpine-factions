package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.AlpineFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * @since 0.1.0
 */
public final class PluginEngine extends AlpineEngine {
    PluginEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        AlpineFactions plugin = AlpineFactions.getInstance();
        plugin.getFlagRegistry().unregister(event.getPlugin());
        plugin.getPermissionRegistry().unregister(event.getPlugin());
    }
}
