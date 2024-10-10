package io.github.lianjordaan.bytebuildersplotplugin.plotLimits.codeArea;

import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class codePistonListener implements Listener {

    @EventHandler
    public void pistonPushEvent(BlockPistonExtendEvent event) {
        Location location = event.getBlock().getLocation();
        if (!LocationUtils.isWithinCodeWord(location)) {
            return;
        }
        event.setCancelled(true);
    }

}
