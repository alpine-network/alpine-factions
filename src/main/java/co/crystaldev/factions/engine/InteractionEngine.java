package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.alpinecore.util.MaterialHelper;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.util.MaterialMapping;
import co.crystaldev.factions.util.PermissionHelper;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                event.getBlockPlaced().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockMultiPlaceEvent event) {
        for (BlockState state : event.getReplacedBlockStates()) {
            boolean permitted = PermissionHelper.checkPermissionAndNotify(
                    event.getPlayer(),
                    state.getChunk(),
                    Permissions.BUILD,
                    "edit the terrain",
                    true);
            if (!permitted) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.plugin.getConfiguration(FactionConfig.class).silkSpawnerIgnoreTerritory
                && MaterialMapping.SPAWNERS.test(event.getBlock())) {
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                event.getBlock().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDamage(BlockDamageEvent event) {
        boolean permitted = event.getInstaBreak() || PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                event.getBlock().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                event.getBlock().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHangingPlace(HangingPlaceEvent event) {
        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                event.getBlock().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHangingDestroy(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (!(remover instanceof Player)) {
            return;
        }

        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                remover,
                event.getEntity().getChunk(),
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL || !event.hasBlock()) {
            return;
        }

        if (event.hasBlock()) {
            // containers
            boolean result = MaterialMapping.CONTAINERS.test(block) && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.USE_CONTAINERS,
                    "use containers",
                    true);
            if (result) {
                event.setCancelled(true);
                return;
            }

            // doors
            result = MaterialMapping.DOORS.test(block) && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.OPEN_DOORS,
                    "open doors",
                    true);
            if (result) {
                event.setCancelled(true);
                return;
            }

            // pressure plates
            result = MaterialMapping.PRESSURE_PLATES.test(block) && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.USE_PRESSURE_PLATES,
                    "use pressure plates",
                    true);
            if (result) {
                event.setCancelled(true);
                return;
            }

            // buttons/levers
            result = MaterialMapping.SWITCHES.test(block) && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.USE_SWITCHES,
                    "use switches",
                    true);
            if (result) {
                event.setCancelled(true);
                return;
            }

            // block interaction
            result = MaterialMapping.MATERIAL_EDIT_ON_INTERACT.test(block)
                    || MaterialMapping.BEDS.test(block)
                    || MaterialMapping.FLOWER_POTS.test(block)
                    || MaterialMapping.CANDLES.test(item)
                    || MaterialMapping.CANDLES.test(block)
                    || MaterialMapping.BUCKETS.test(item) && MaterialMapping.CAULDRONS.test(block)
                    || ItemHelper.AXES.test(item) && MaterialMapping.STRIPPED_LOGS.test(block)
                    || ItemHelper.HOES.test(item) && MaterialHelper.getType(block) == XMaterial.FARMLAND
                    || ItemHelper.SHOVELS.test(item) && MaterialHelper.getType(block) == XMaterial.DIRT_PATH;
            result = result && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.BUILD,
                    "edit the terrain",
                    true);
            if (result) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.hasItem() && block != null) {
            boolean result = MaterialMapping.MATERIAL_EDIT_TOOLS.test(item) && !PermissionHelper.checkPermissionAndNotify(
                    player,
                    block.getChunk(),
                    Permissions.BUILD,
                    "edit the terrain",
                    true);
            if (result) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        BlockFace face = event.getBlockFace();
        Location location = event.getBlockClicked().getLocation().clone();
        location.add(face.getModX(), face.getModY(), face.getModZ());

        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                location,
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        BlockFace face = event.getBlockFace();
        Location location = event.getBlockClicked().getLocation().clone();
        location.add(face.getModX(), face.getModY(), face.getModZ());

        boolean permitted = PermissionHelper.checkPermissionAndNotify(
                event.getPlayer(),
                location,
                Permissions.BUILD,
                "edit the terrain",
                true);
        if (!permitted) {
            event.setCancelled(true);
        }
    }
}
