package co.crystaldev.factions.engine.player;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.util.FactionHelper;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/23/2024
 */
public final class InteractionEngine extends AlpineEngine {
    InteractionEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlockPlaced().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockMultiPlaceEvent event) {
        for (BlockState state : event.getReplacedBlockStates()) {
            if (!FactionHelper.isPermitted(event.getPlayer(), state.getChunk(), Permissions.BUILD, "build")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingDestroy(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (!(remover instanceof Player)) {
            return;
        }

        if (!FactionHelper.isPermitted((Player) remover, event.getEntity().getLocation().getChunk(), Permissions.BUILD, "build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
    }
}
