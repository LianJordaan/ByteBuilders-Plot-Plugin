package io.github.lianjordaan.bytebuildersplotplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class LocationUtils {

    public static void savePlotSpawnLocation(Location location) {
        String basePath = "worldSpawns." + location.getWorld().getName() + ".location.";

        // Set x, y, z, yaw, pitch values in the JSON
        JsonDataManager.set(basePath + "x", location.getX());
        JsonDataManager.set(basePath + "y", location.getY());
        JsonDataManager.set(basePath + "z", location.getZ());
        JsonDataManager.set(basePath + "yaw", location.getYaw());
        JsonDataManager.set(basePath + "pitch", location.getPitch());
    }

    public static Location getPlotSpawnLocation(String worldName) {
        String basePath = "worldSpawns." + worldName + ".location.";

        // Retrieve x, y, z, yaw, pitch values from the JSON
        Double x = JsonDataManager.getDouble(basePath + "x");
        Double y = JsonDataManager.getDouble(basePath + "y");
        Double z = JsonDataManager.getDouble(basePath + "z");
        Float yaw = JsonDataManager.getFloat(basePath + "yaw");
        Float pitch = JsonDataManager.getFloat(basePath + "pitch");

        // Retrieve the world from the world name
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null; // If the world doesn't exist, return null
        }

        // Check if any of the coordinates are missing or invalid
        if (x == null || y == null || z == null || yaw == null || pitch == null) {
            // Return the world's default spawn point if the location is not set in the JSON
            savePlotSpawnLocation(world.getSpawnLocation());
            return world.getSpawnLocation();
        }

        // Create and return the location object with the retrieved values
        return new Location(world, x, y, z, yaw, pitch);
    }

}