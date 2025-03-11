package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * @since 0.1.0
 */
public final class PhysicsEngine extends AlpineEngine {
    PhysicsEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (checkBlocks(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (checkBlocks(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof TNTPrimed) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Faction faction = Factions.get().claims().getFactionOrDefault(event.getEntity().getLocation());
        if (!faction.getFlagValueOrDefault(FactionFlags.EXPLOSIONS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        checkExplosion(event.blockList());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(BlockExplodeEvent event) {
        checkExplosion(event.blockList());
    }

    private static void checkExplosion(@NotNull List<Block> blockList) {
        ClaimAccessor claims = Factions.get().claims();

        int lastChunkX = 0, lastChunkZ = 0;
        boolean checked = false, allowed = false;

        Iterator<Block> iterator = blockList.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Chunk chunk = block.getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();

            if (checked && lastChunkX == chunkX && lastChunkZ == chunkZ) {
                if (!allowed) {
                    iterator.remove();
                }

                continue;
            }

            checked = true;
            lastChunkX = chunkX;
            lastChunkZ = chunkZ;

            Faction faction = claims.getFactionOrDefault(block);
            allowed = faction.getFlagValueOrDefault(FactionFlags.EXPLOSIONS);
            if (!allowed) {
                iterator.remove();
            }
        }
    }

    private static boolean checkBlocks(@NotNull Block sourceBlock, @NotNull List<Block> blocks, @NotNull BlockFace facing) {
        ClaimAccessor claims = Factions.get().claims();
        Faction sourceFaction = claims.getFactionOrDefault(sourceBlock);

        Chunk lastChunk = null;
        for (Block block : blocks) {
            Chunk chunk = block.getChunk();
            if (chunk.equals(lastChunk)) {
                continue;
            }
            lastChunk = chunk;

            Faction faction = claims.getFactionOrDefault(block);
            if (!faction.equals(sourceFaction) && !faction.isWilderness()) {
                Location add = block.getLocation().add(facing.getModX(), facing.getModY(), facing.getModZ());
                faction = claims.getFactionOrDefault(add);
                return !faction.equals(sourceFaction) && !faction.isWilderness();
            }
        }
        return false;
    }
}
