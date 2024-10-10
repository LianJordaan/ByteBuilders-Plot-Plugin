package io.github.lianjordaan.bytebuildersplotplugin.plotLimits.playArea;

import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

public class playStructureGenerateListener implements Listener {

    @EventHandler
    public void generateTreeStructure(StructureGrowEvent event) {
        Location location = event.getLocation();
        if (!LocationUtils.isWithinPlayWord(location)) {
            return;
        }
        event.getBlocks().removeIf(block -> !LocationUtils.isWithinPlotBounds(block.getLocation()));
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();

        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            // Remove the block if it's out of bounds
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void blockForm(BlockFormEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void liquidFlow(BlockFromToEvent event) {
        Location location = event.getToBlock().getLocation();
        if (!LocationUtils.isWithinPlotBounds(location) && LocationUtils.isWithinPlayWord(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlayWord(location)) {
            return;
        }
        ItemStack dispensedItem = event.getItem();

        // Check if the dispensed item is a water bucket
        if (dispensedItem.getType().toString().toLowerCase().contains("shulker_box") || dispensedItem.getType().toString().toLowerCase().contains("_bucket")) {
            // Get the block the dispenser is attached to
            Block dispenserBlock = event.getBlock();

            // Get the direction the dispenser is facing
            BlockFace facing = ((Directional) dispenserBlock.getBlockData()).getFacing();

            // Get the block in front of the dispenser (where the water will be placed)
            Block targetBlock = dispenserBlock.getRelative(facing);

            // Check if the target location is outside the plot bounds
            if (!LocationUtils.isWithinPlotBounds(targetBlock.getLocation())) {
                // Cancel the dispensing action
                event.setCancelled(true);
            }
        }
    }
}
