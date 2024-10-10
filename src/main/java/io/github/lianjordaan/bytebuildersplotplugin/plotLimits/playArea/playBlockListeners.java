package io.github.lianjordaan.bytebuildersplotplugin.plotLimits.playArea;

import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class playBlockListeners implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!LocationUtils.isWithinPlotBounds(event.getBlock().getLocation()) && LocationUtils.isWithinPlayWord(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!LocationUtils.isWithinPlotBounds(event.getBlock().getLocation()) && LocationUtils.isWithinPlayWord(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Location clickedBlockLocation = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        if (!LocationUtils.isWithinPlotBounds(clickedBlockLocation) && LocationUtils.isWithinPlayWord(clickedBlockLocation)) {
            event.setCancelled(true);
        }
    }

}
