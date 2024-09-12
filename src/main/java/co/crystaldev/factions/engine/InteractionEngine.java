package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.alpinecore.util.MaterialHelper;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.MaterialMapping;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class InteractionEngine extends AlpineEngine {
    InteractionEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlockPlaced().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockMultiPlaceEvent event) {
        for (BlockState state : event.getReplacedBlockStates()) {
            if (!FactionHelper.isPermitted(event.getPlayer(), state.getChunk(), Permissions.BUILD, "edit the terrain")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getInstaBreak() && !FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!FactionHelper.isPermitted(event.getPlayer(), event.getBlock().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingDestroy(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (!(remover instanceof Player)) {
            return;
        }

        if (!FactionHelper.isPermitted((Player) remover, event.getEntity().getLocation().getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL || !event.hasBlock()) {
            return;
        }

        if (event.hasBlock()) {
            // containers
            boolean result = MaterialMapping.CONTAINERS.test(block);
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.USE_CONTAINERS, "use containers")) {
                event.setCancelled(true);
                return;
            }

            // doors
            result = MaterialMapping.DOORS.test(block);
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.OPEN_DOORS, "open doors")) {
                event.setCancelled(true);
                return;
            }

            // pressure plates
            result = MaterialMapping.PRESSURE_PLATES.test(block);
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.USE_PRESSURE_PLATES, "use pressure plates")) {
                event.setCancelled(true);
                return;
            }

            // buttons/levers
            result = MaterialMapping.SWITCHES.test(block);
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.USE_SWITCHES, "use switches")) {
                event.setCancelled(true);
                return;
            }

            // block interaction
            result = MaterialMapping.MATERIAL_EDIT_ON_INTERACT.test(block)
                    || MaterialMapping.FLOWER_POTS.test(block)
                    || MaterialMapping.CANDLES.test(item)
                    || MaterialMapping.CANDLES.test(block)
                    || MaterialMapping.BUCKETS.test(item) && MaterialMapping.CAULDRONS.test(block)
                    || ItemHelper.AXES.test(item) && MaterialMapping.STRIPPED_LOGS.test(block)
                    || ItemHelper.HOES.test(item) && MaterialHelper.getType(block) == XMaterial.FARMLAND
                    || ItemHelper.SHOVELS.test(item) && MaterialHelper.getType(block) == XMaterial.DIRT_PATH;
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.BUILD, "edit the terrain")) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.hasItem() && block != null) {
            boolean result = MaterialMapping.MATERIAL_EDIT_TOOLS.test(item);
            if (result && !FactionHelper.isPermitted(player, block.getLocation().getChunk(), Permissions.BUILD, "edit the terrain")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        BlockFace face = event.getBlockFace();
        Location location = event.getBlockClicked().getLocation().clone();
        location.add(face.getModX(), face.getModY(), face.getModZ());

        if (!FactionHelper.isPermitted(event.getPlayer(), location.getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        BlockFace face = event.getBlockFace();
        Location location = event.getBlockClicked().getLocation().clone();
        location.add(face.getModX(), face.getModY(), face.getModZ());

        if (!FactionHelper.isPermitted(event.getPlayer(), location.getChunk(), Permissions.BUILD, "edit the terrain")) {
            event.setCancelled(true);
        }
    }
}
