package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.event.ServerTickEvent;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.util.FactionHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) {
            return;
        }

        Block block = event.getClickedBlock();

    }

    private boolean toggled;

//    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//    public void onPlayerChatPre(AsyncPlayerChatEvent event) {
//        if ("ooga".equals(event.getMessage())) {
//            this.toggled = !toggled;
//            Messaging.send(event.getPlayer(), this.toggled ? Component.text("booga").color(NamedTextColor.GREEN) : Component.text("womp womp").color(NamedTextColor.RED));
//        }
//    }
//
//    @EventHandler
//    public void onEvent(Event event) {
//        if (event instanceof ServerTickEvent || !this.toggled)
//            return;
//
//        System.out.println(event.getClass().getName());
//    }
}
