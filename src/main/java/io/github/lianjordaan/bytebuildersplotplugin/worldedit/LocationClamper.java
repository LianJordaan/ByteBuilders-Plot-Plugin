package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
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
        int clampedX = Math.max(0, Math.min(location.x(), 127));
        int clampedY = Math.max(-64, Math.min(location.y(), 320));
        int clampedZ = Math.max(0, Math.min(location.z(), 127));

        // Return the clamped BlockVector3
        return BlockVector3.at(clampedX, clampedY, clampedZ);
    }
    public static Location clampLocation(Location location) {
        // Clamp each coordinate between the minimum and maximum values
        double clampedX = Math.max(0, Math.min(location.getX(), 127));
        double clampedY = Math.max(-64, Math.min(location.getY(), 320));
        double clampedZ = Math.max(0, Math.min(location.getZ(), 127));

        // Return the clamped BlockVector3
        return new Location(location.getWorld(), clampedX, clampedY, clampedZ, location.getYaw(), location.getPitch());
    }
}
