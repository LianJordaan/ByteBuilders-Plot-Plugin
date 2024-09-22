package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.fastasyncworldedit.core.extent.*;
import com.fastasyncworldedit.core.limit.FaweLimit;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.MaskingExtent;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class WorldEditLimitListener {

    @Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event) {
        if (event.getActor() == null) {
            return;
        }
        if (!event.getActor().isPlayer()) {
            return;
        }




        if (event.getStage() != EditSession.Stage.BEFORE_HISTORY) {
            return;
        }
        World worldObj = event.getWorld();
        if (worldObj == null) {
            return;
        }
        if (!event.getWorld().getName().startsWith("dim-")) {
            return;
        }
        if (event.getWorld().getName().equals("dim-code")) {
            return;
        }

        int size = ByteBuildersPlotPlugin.plotSize - 1;


        CuboidRegion plotArea = new CuboidRegion(BlockVector3.at(0, -64, 0), BlockVector3.at(size, 320, size));
        event.setExtent(new MaskingExtent(event.getExtent(),new RegionMask(plotArea)));
        event.setExtent(new SingleRegionExtent(event.getExtent(),new FaweLimit(),plotArea));


//        Bukkit.getServer().sendMessage(Component.text("WorldEdit Limit Listener: Successfully set the extent to a plot area"));
    }
}
