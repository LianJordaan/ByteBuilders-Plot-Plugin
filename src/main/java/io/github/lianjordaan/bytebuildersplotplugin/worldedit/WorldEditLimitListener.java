package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.fastasyncworldedit.core.extent.transform.SelectTransform;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.cui.SelectionMinMaxEvent;
import com.sk89q.worldedit.internal.cui.SelectionPointEvent;
import com.sk89q.worldedit.internal.cui.SelectionShapeEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class WorldEditLimitListener {

    @Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event) {
        Bukkit.getServer().sendMessage(Component.text("Actor: " + event.getActor()));
        Bukkit.getServer().sendMessage(Component.text("Stage: " + event.getStage()));
        Bukkit.getServer().sendMessage(Component.text("World: " + event.getWorld()));
        Bukkit.getServer().sendMessage(Component.text("Extent: " + event.getExtent()));
        if (event.getActor() == null) {
            return;
        }
        if (!event.getActor().isPlayer()) {
            return;
        }

        Player player = (Player) event.getActor();

        BlockVector3 corner1 = LocationClamper.clampLocation(player.getSelection().getMinimumPoint());
        BlockVector3 corner2 = LocationClamper.clampLocation(player.getSelection().getMaximumPoint());
        Bukkit.getServer().sendMessage(Component.text("Corner 1: " + corner1));
        Bukkit.getServer().sendMessage(Component.text("Corner 2: " + corner2));

        player.setSelection(new CuboidRegion(corner1, corner2));

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

        Set<CuboidRegion> mask;
        mask = Collections.singleton(plotArea);

        event.setExtent(new WEExtent(mask, event.getExtent()));
        Bukkit.getServer().sendMessage(Component.text("WorldEdit Limit Listener: Successfully set the extent to a plot area"));
    }
}
