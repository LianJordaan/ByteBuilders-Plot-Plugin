package io.github.lianjordaan.bytebuildersplotplugin.plotLimits;

import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.LocationClamper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class entityLoop {
    public static void startLoop() {
        try {
            new BukkitRunnable() {
                public void run() {
                    for (World world : Bukkit.getWorlds()){
                        if (!world.getName().startsWith("dim-")){
                            continue;
                        }
                        if (world.getName().equalsIgnoreCase("dim-code")) {
                            for (Entity entity : world.getEntities()) {
                                if (entity instanceof Player) {
                                    continue;
                                }
                                if (!LocationUtils.isWithinCodeBounds(entity.getLocation())) {
                                    if (entity instanceof Item) {
                                        entity.setGravity(false);
                                    }
                                    entity.teleport(LocationClamper.clampLocationToCodeBounds(entity.getLocation()));
                                }
                            }
                        } else {
                            for (Entity entity : world.getEntities()) {
                                if (entity instanceof Player) {
                                    continue;
                                }
                                if (!LocationUtils.isWithinPlotBounds(entity.getLocation())) {
                                    entity.teleport(LocationClamper.clampLocationToPlotBounds(entity.getLocation()));
                                }
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
