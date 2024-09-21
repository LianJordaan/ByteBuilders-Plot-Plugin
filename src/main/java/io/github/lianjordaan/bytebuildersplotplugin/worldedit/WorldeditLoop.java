package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.limit.PermissiveSelectorLimits;
import com.sk89q.worldedit.session.SessionManager;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class WorldeditLoop {
    public static void loop(org.bukkit.entity.Player player) {
        CuboidRegion plotArea = new CuboidRegion(BlockVector3.at(0, -64, 0), BlockVector3.at(127, 320, 127));

        new BukkitRunnable() {
            public void run() {
                if (!player.isOnline())
                    cancel();

                Player actor = BukkitAdapter.adapt(player);
                SessionManager manager = WorldEdit.getInstance().getSessionManager();
                LocalSession localSession = manager.get(actor);
                if (localSession.getSelectionWorld() != null) {
                    RegionSelector selector = localSession.getRegionSelector(localSession.getSelectionWorld());
                    if (selector.isDefined()) {
                        try {
                            List<BlockVector3> vertices = selector.getVertices();
                            if (vertices.size() == 1) {
                                Iterator<BlockVector3> it = selector.getIncompleteRegion().iterator();
                                while (it.hasNext()) {
                                    BlockVector3 curBlock = it.next();
                                    if (!plotArea.contains(curBlock)) {
                                        player.sendMessage(Component.text("Worldedit selection reset because a point was outside plot."));
                                        selector.clear();
                                        break;
                                    }
                                }
                            } else {
                                selector.clear();
                                int i = 0;
                                for (BlockVector3 vertex : vertices) {
                                    i++;
                                    if (i == 1) {
                                        selector.selectPrimary(LocationClamper.clampLocation(vertex), PermissiveSelectorLimits.getInstance());
                                    } else {
                                        selector.selectSecondary(LocationClamper.clampLocation(vertex), PermissiveSelectorLimits.getInstance());
                                    }
                                }
                                localSession.setRegionSelector(localSession.getSelectionWorld(), selector);
                            }
                        } catch (IncompleteRegionException e) {
                            player.sendMessage(Component.text("Worldedit selection reset because there was an incomplete selection."));
                            selector.clear();
                        }

                    }

                }
                localSession.setMask(new RegionMask(plotArea));
            }
        }.runTaskTimer(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), 1, 1);
    }
}
