package io.github.lianjordaan.bytebuildersplotplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.util.FileUtils;
import org.java_websocket.client.WebSocketClient;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class ByteBuildersPlotPlugin extends JavaPlugin {
    private Logger logger;

    private static volatile String latestMessage = "";

    private WebSocketClient webSocketClient;

    public static String getLatestMessage() {
        return latestMessage;
    }

    public static void setLatestMessage(String latestMessage) {
        ByteBuildersPlotPlugin.latestMessage = latestMessage;
    }

    @Override
    public void onEnable() {
        this.logger = Bukkit.getLogger();
        // Plugin startup logic
        logger.info("ByteBuilders Plot Plugin initialized!");

        try {
            Properties env = EnvLoader.loadEnv();
            String username = env.getProperty("USERNAME");
            Objects.requireNonNull(username, "USERNAME not set in .env file");
            String uri = "ws://192.168.0.125:3000?username=" + username + "&id=" + this.getServer().getPort();
            System.out.println("Connecting to URI: " + uri);
            webSocketClient = new WebSocketClientHandler(new URI(uri), logger);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            // Handle URI syntax issues
            logger.log(Level.SEVERE, "Invalid WebSocket URI", e);
        } catch (IllegalArgumentException e) {
            // Handle illegal arguments like empty username
            logger.log(Level.SEVERE, "Configuration error", e);
        } catch (Exception e) {
            // Handle other exceptions
            logger.log(Level.SEVERE, "Failed to initialize WebSocket client", e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        webSocketClient.send("{\"type\": \"status\", \"status\": \"stopping\"}");
        webSocketClient.send("{\"type\": \"forwarded-message\", \"targetId\": \"proxy\", \"message\": \"stopping\"}");
        webSocketClient.close();

        // Perform immediate cleanup tasks
        getLogger().info("Starting shutdown tasks...");
    }

    // might add later, but as of now, it generates a lot of errors, because I am unable to load the plugins back...

//    public void unloadPlugins() {
//        Bukkit.getScheduler().runTask(this, () -> {
//            PluginManager pm = Bukkit.getPluginManager();
//            for (Plugin plugin : pm.getPlugins()) {
//                // Skip the plugin that's currently executing this code
//                if (plugin.isEnabled() && !plugin.getName().equals(this.getName())) {
//                    pm.disablePlugin(plugin);
//                }
//            }
//            System.out.println("All plugins have been unloaded except this plugin.");
//        });
//    }

    public void loadWorlds() {
        Bukkit.getScheduler().runTask(this, () -> {
            // Get the server's world container (the folder where worlds are stored)
            File worldContainer = Bukkit.getWorldContainer();

            // List all directories in the server's world container
            File[] worldFolders = worldContainer.listFiles(File::isDirectory);
            if (worldFolders != null) {
                for (File worldFolder : worldFolders) {
                    String worldName = worldFolder.getName();

                    // Skip already loaded worlds or non-dimension folders
                    if (Bukkit.getWorld(worldName) != null || !worldName.startsWith("dim-")) {
                        continue;
                    }

                    // Load the world
                    WorldCreator creator = new WorldCreator(worldName);
                    Bukkit.createWorld(creator);
                    getLogger().info("Loaded world: " + worldName);
                }
            }
            if (Bukkit.getWorld("dim-play") == null) {
                World playDimension = Bukkit.createWorld(new WorldCreator("dim-play")
                        .generateStructures(false)
                        .generator("VoidGen")
                        .keepSpawnLoaded(TriState.TRUE)
                        .environment(World.Environment.NORMAL)
                        .generatorSettings("{\"biome\":\"plains\"}")
                        .type(WorldType.FLAT));
                playDimension.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                playDimension.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                playDimension.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }
            if (Bukkit.getWorld("dim-code") == null) {
                World codeDimension = Bukkit.createWorld(new WorldCreator("dim-code")
                        .generateStructures(false)
                        .generator("VoidGen")
                        .keepSpawnLoaded(TriState.TRUE)
                        .environment(World.Environment.NORMAL)
                        .generatorSettings("{\"biome\":\"plains\"}")
                        .type(WorldType.FLAT));
                codeDimension.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                codeDimension.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                codeDimension.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }
        });
    }

    // might add later, but as of now, it generates a lot of errors, because you can't load paper plugins after the server has started

//    public void loadPlugins() {
//        Bukkit.getScheduler().runTask(this, () -> {
//            File pluginsFolder = new File("plugins");
//            for (File file : Objects.requireNonNull(pluginsFolder.listFiles())) {
//                if (file.isFile() && file.getName().endsWith(".jar")) {
//                    try {
//                        Bukkit.getPluginManager().loadPlugin(file);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            System.out.println("All plugins have been loaded.");
//        });
//    }


}
