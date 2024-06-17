package co.crystaldev.factions.api;

import org.bukkit.Bukkit;

import java.util.Optional;

/**
 * @since 0.1.0
 */
final class Reference {
    public static final Factions FACTIONS = Optional
            .ofNullable((Factions) Bukkit.getPluginManager().getPlugin("AlpineFactions"))
            .orElseThrow(() -> new IllegalStateException("Attempted to access Factions before initialization"));
}
