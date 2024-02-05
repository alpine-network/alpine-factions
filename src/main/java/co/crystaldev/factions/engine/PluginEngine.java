package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.AlpineFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
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
