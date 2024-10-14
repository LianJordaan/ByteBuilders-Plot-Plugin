package io.github.lianjordaan.bytebuildersplotplugin.plotLimits.playArea;

import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class playPistonListener implements Listener {

    @EventHandler
    public void pistonPushEvent(BlockPistonExtendEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        if (!LocationUtils.isWithinPlayWord(blockLocation)) {
            return;
        }

        // Get the direction the piston is pushing
        BlockFace direction = event.getDirection();

        // Calculate the location of the piston head
        Location pistonHeadLocation = event.getBlock().getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ());

        // Check if the new location is within the plot bounds
        if (!LocationUtils.isWithinPlotBounds(pistonHeadLocation)) {
            // Cancel the event if the piston head would be pushed out of bounds
            event.setCancelled(true);
            return;
        }

        // Iterate through each block affected by the piston push
        for (Block block : event.getBlocks()) {
            // Calculate the new location based on the push direction
            Location newLocation = block.getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ());

            // Check if the new location is within the plot bounds
            if (!LocationUtils.isWithinPlotBounds(newLocation)) {
                // Cancel the event if any block would be pushed out of bounds
                event.setCancelled(true);
                break;  // Exit the loop early as we already cancelled the event
            }
        }
    }

}
