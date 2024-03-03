package co.crystaldev.factions.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/02/2024
 */
public final class PhysicsEngine extends AlpineEngine {
    PhysicsEngine(AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (checkBlocks(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (checkBlocks(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    private static boolean checkBlocks(@NotNull Block sourceBlock, @NotNull List<Block> blocks, @NotNull BlockFace facing) {
        ClaimAccessor claims = Accessors.claims();
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
