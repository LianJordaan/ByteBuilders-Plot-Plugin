package io.github.lianjordaan.bytebuildersplotplugin.plotLimits.codeArea;

import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class codeBlockListeners implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinCodeWord(location)) {
            return;
        }
        if (!LocationUtils.isWithinCodeBounds(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinCodeWord(location)) {
            return;
        }
        if (!LocationUtils.isWithinCodeBounds(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}
