package io.github.lianjordaan.bytebuildersplotplugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.session.SessionOwner;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.LocationClamper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntityLoop {
    public static void startLoop() {
        try {
            new BukkitRunnable() {
                public void run() {
                    for (World world : Bukkit.getWorlds()){
                        if (!world.getName().startsWith("dim-") || world.getName().equalsIgnoreCase("dim-code")){
                            continue;
                        }
                        for (Entity entity : world.getEntities()){
                            if (entity instanceof Player){
                                continue;
                            }
                            if (!LocationUtils.isWithinPlotBounds(entity.getLocation())){
                                entity.teleport(LocationClamper.clampLocationToPlotBounds(entity.getLocation()));
                            }
                        }
                    }
                }
            }.runTaskTimer(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), 1, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
