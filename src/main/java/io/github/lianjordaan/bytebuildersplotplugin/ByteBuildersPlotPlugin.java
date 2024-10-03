package io.github.lianjordaan.bytebuildersplotplugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sk89q.worldedit.WorldEdit;
import io.github.lianjordaan.bytebuildersplotplugin.commands.AdminCommands;
import io.github.lianjordaan.bytebuildersplotplugin.commands.PlotCommands;
import io.github.lianjordaan.bytebuildersplotplugin.tabcompleters.AdminCommandTabCompleter;
import io.github.lianjordaan.bytebuildersplotplugin.tabcompleters.PlotCommandTabCompleter;
import io.github.lianjordaan.bytebuildersplotplugin.utils.EntityLoop;
import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.PlayerStateCheckUtils;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.LocationClamper;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.PluginModule;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.WorldEditLimitListener;
import io.github.lianjordaan.bytebuildersplotplugin.worldedit.WorldeditLoop;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ByteBuildersPlotPlugin extends JavaPlugin implements Listener {
    private Logger logger;

    private static volatile String latestMessage = "";

    public static Integer plotSize;

    public static WebSocketClient webSocketClient;

    public static String getLatestMessage() {
        return latestMessage;
    }

    public static void setLatestMessage(String latestMessage) {
        ByteBuildersPlotPlugin.latestMessage = latestMessage;
    }


    private Injector injector;

    public @NonNull Injector injector() {
        return this.injector;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!PlayerStateCheckUtils.isPlayerInAdminBypass(event.getPlayer())) {

            String worldName = event.getPlayer().getWorld().getName();
            if (worldName.equals("dim-code")) {;
                if (!LocationUtils.isWithinCodeBounds(event.getPlayer().getLocation())) {
                    event.getPlayer().teleport(LocationClamper.clampLocationToCodeBounds(event.getPlayer().getLocation()));
                }
            } else if (worldName.startsWith("dim-")) {
                if (!LocationUtils.isWithinPlotBounds(event.getPlayer().getLocation())) {
                    event.getPlayer().teleport(LocationClamper.clampLocationToPlotBounds(event.getPlayer().getLocation()));
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        WorldeditLoop.loop(event.getPlayer());
    }

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

        EntityLoop.startLoop();

        try {
            this.injector = Guice.createInjector(new PluginModule());

            WorldEdit.getInstance().getEventBus().register(this.injector().getInstance(WorldEditLimitListener.class));
            getServer().sendMessage(Component.text("Hooked into WorldEdit"));
        } catch (Exception e) {
            getServer().sendMessage(Component.text("Failed to hook into WorldEdit" + e.getMessage()));
        }
        this.getCommand("plot").setExecutor(new PlotCommands());
        this.getCommand("plot").setTabCompleter(new PlotCommandTabCompleter());

        this.getCommand("admin").setExecutor(new AdminCommands());
        this.getCommand("admin").setTabCompleter(new AdminCommandTabCompleter());
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
//         Plugin shutdown logic
        webSocketClient.send("{\"type\": \"status\", \"status\": \"stopping\"}");
        webSocketClient.send("{\"type\": \"forwarded-message\", \"targetId\": \"proxy\", \"message\": \"stopping\"}");
        webSocketClient.close();

        // Perform immediate cleanup tasks
        getLogger().info("Starting shutdown tasks...");
    }

    public void loadWorld(String worldName) {
        Bukkit.getScheduler().runTask(this, () -> {
            // Skip already loaded worlds
            if (Bukkit.getWorld(worldName) != null) {
                return;
            }
            try {
                // Load the world
                WorldCreator creator = new WorldCreator(worldName);
                Bukkit.createWorld(creator);
            } catch (Exception ignored) {
            }
        });
    }

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
                webSocketClient.send("{\"type\": \"request-file\", \"file\": \"play-void\", \"fileType\": \"world\", \"worldName\": \"dim-play\"}");
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> {
                    try {
                        // Parse the latest message
                        JsonObject jsonResponse = JsonParser.parseString(latestMessage).getAsJsonObject();

                        // Check if the response contains the desired confirmation
                        if (jsonResponse.get("type").getAsString().equals("response") &&
                                jsonResponse.get("status").getAsString().equals("world-transfer-complete") &&
                                jsonResponse.get("world").getAsString().equals("dim-play")) {

                            Bukkit.getScheduler().runTask(this, () -> {
                                Bukkit.createWorld(new WorldCreator("dim-play"));
                                LocationUtils.savePlotSpawnLocation(new Location(Bukkit.getWorld("dim-play"), 0.5, 1, 0.5, 0, 0));
                            });

                            // Output confirmation to console
                            System.out.println("World 'dim-play' created successfully.");

                            // Stop the executor once the world is created
                            executor.shutdown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error parsing response from WebSocket server.");
                    }
                }, 0, 1, TimeUnit.MILLISECONDS);
            }

            if (Bukkit.getWorld("dim-code") == null) {
                webSocketClient.send("{\"type\": \"request-file\", \"file\": \"code\", \"fileType\": \"world\", \"worldName\": \"dim-code\"}");
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> {
                    try {
                        // Parse the latest message
                        JsonObject jsonResponse = JsonParser.parseString(latestMessage).getAsJsonObject();

                        // Check if the response contains the desired confirmation
                        if (jsonResponse.get("type").getAsString().equals("response") &&
                                jsonResponse.get("status").getAsString().equals("world-transfer-complete") &&
                                jsonResponse.get("world").getAsString().equals("dim-code")) {


                            Bukkit.getScheduler().runTask(this, () -> {
                                World codeDimension = Bukkit.createWorld(new WorldCreator("dim-code"));
                                codeDimension.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
                                LocationUtils.savePlotSpawnLocation(new Location(codeDimension, 117.5, -63, 10.5, -90, 0));
                            });
                            // Output confirmation to console
                            System.out.println("World 'dim-code' created successfully.");

                            // Stop the executor once the world is created
                            executor.shutdown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error parsing response from WebSocket server.");
                    }
                }, 0, 1, TimeUnit.MILLISECONDS);
            }
        });
    }
}
