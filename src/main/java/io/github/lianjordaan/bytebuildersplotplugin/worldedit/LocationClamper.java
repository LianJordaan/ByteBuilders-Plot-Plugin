package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import org.bukkit.Location;

public class LocationClamper {

    /**
     * Clamps the given BlockVector3 location to the specified bounds.
     *
     * @param location The location to clamp.
     * @return A new BlockVector3 with the clamped coordinates.
     */
    public static BlockVector3 clampLocation(BlockVector3 location) {
        // Clamp each coordinate between the minimum and maximum values
        int clampedX = Math.max(0, Math.min(location.x(), ByteBuildersPlotPlugin.plotSize - 1));
        int clampedY = Math.max(-64, Math.min(location.y(), 320));
        int clampedZ = Math.max(0, Math.min(location.z(), ByteBuildersPlotPlugin.plotSize - 1));

        // Return the clamped BlockVector3
        return BlockVector3.at(clampedX, clampedY, clampedZ);
    }
    public static Location clampLocationToPlotBounds(Location location) {

        int plotSize = ByteBuildersPlotPlugin.plotSize;

        int xMin = 0;
        int xMax = 0;
        int yMin = 0;
        int yMax = 0;
        int zMin = 0;
        int zMax = 0;

        String worldName = location.getWorld().getName();
        if (worldName.equals("dim-code")) {
            xMax = 128;
            zMax = 512;
        } else if (worldName.startsWith("dim-")) {
            xMax = plotSize;
            zMax = plotSize;
        }

        // Clamp each coordinate between the minimum and maximum values
        double clampedX = Math.max(xMin, Math.min(location.getX(), xMax));
        double clampedY = Math.max(-64, Math.min(location.getY(), 320));
        double clampedZ = Math.max(zMin, Math.min(location.getZ(), zMax));

        // Return the clamped BlockVector3
        return new Location(location.getWorld(), clampedX, clampedY, clampedZ, location.getYaw(), location.getPitch());
    }

    public static Location clampLocationToCodeBounds(Location location) {

        int xMin = 0 - 16;
        int xMax = 128 + 16;
        int yMin = -63;
        int yMax = 512;
        int zMin = 0 - 16;
        int zMax = 512 + 16;

        // Clamp each coordinate between the minimum and maximum values
        double clampedX = Math.max(xMin, Math.min(location.getX(), xMax));
        double clampedY = Math.max(yMin, Math.min(location.getY(), yMax));
        double clampedZ = Math.max(zMin, Math.min(location.getZ(), zMax));

        // Return the clamped BlockVector3
        return new Location(location.getWorld(), clampedX, clampedY, clampedZ, location.getYaw(), location.getPitch());
    }
}
