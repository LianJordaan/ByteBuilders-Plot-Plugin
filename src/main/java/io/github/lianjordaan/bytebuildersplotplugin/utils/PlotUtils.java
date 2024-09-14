package io.github.lianjordaan.bytebuildersplotplugin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PlotUtils {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors(); // Or a fixed number like 8
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static final Map<String, Material> COLOR_TO_BLOCK_TYPE = new HashMap<>();

    static {
        // Initialize the map of color names to block types
        COLOR_TO_BLOCK_TYPE.put("black", Material.BLACK_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("blue", Material.BLUE_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("green", Material.GREEN_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("cyan", Material.CYAN_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("red", Material.RED_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("purple", Material.PURPLE_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("orange", Material.ORANGE_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("gray", Material.GRAY_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("light_gray", Material.LIGHT_GRAY_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("light_blue", Material.LIGHT_BLUE_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("lime", Material.LIME_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("pink", Material.PINK_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("magenta", Material.MAGENTA_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("yellow", Material.YELLOW_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("white", Material.WHITE_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("brown", Material.BROWN_STAINED_GLASS);
        COLOR_TO_BLOCK_TYPE.put("tinted", Material.TINTED_GLASS);
    }



    public static void handleCodespaceRemoveCommand(Player player, String[] args) {
        Set<String> filteredArgs = new HashSet<>();
        int amount = 1; // Default number

        // Parse arguments
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];

           if (arg.matches("\\d+")) { // Check if argument is a number
               if (filteredArgs.contains("amount")) {
                   player.sendMessage("You can only specify a number once.");
                   return;
               }

               if (Integer.parseInt(arg) <= 0) {
                   player.sendMessage("Has to be more than 0");
                   return;
               }

               amount = Integer.parseInt(arg);
               filteredArgs.add("amount");
           } else {
               player.sendMessage("Invalid argument: " + arg);
               return;
           }
        }

        // Mimic functionality here
        removeCodeLayersToPlot(amount, player);
    }

    private static void removeCodeLayersToPlot(int number, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> {

            List<Integer> takenLayers = PlotUtils.getLayersBelow(player.getY()).stream().toList();

            if (takenLayers.isEmpty()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#FF5555>Error: <#AAAAAA>We couldn't find any layers below you to remove."));
                return;
            }

            org.bukkit.World bukkitWorld = Bukkit.getWorld("dim-code");
            World weWorld = BukkitAdapter.adapt(bukkitWorld);

            EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld);
            editSession.setFastMode(true);
            try {
                int maxLayerCount = Math.min(takenLayers.size(), number);

                for (int i = 0; i < maxLayerCount; i++) {
                    int y = takenLayers.get(i);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<gray><i>debug info</i>: Removed layer at y=" + y));

                    Location pos1 = new Location(bukkitWorld, 127, y, 0);
                    Location pos2 = new Location(bukkitWorld, 3, y, 511);
                    fillWithBlock(editSession, Material.AIR, pos1, pos2);
                }

                player.sendMessage(MiniMessage.miniMessage().deserialize("<#55FF55><b>»</b> <#AAAAAA>Removed " + maxLayerCount + " codespace layers below you."));
            } finally {
                editSession.flushQueue();
                editSession.close();
            }

            if (number > takenLayers.size()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#FF5555><b>⏏</b> <#AAAAAA>Stopped early as there are no more codespaces below you to remove."));
            }
        });
    }

    public static void handleCodespaceAddCommand(Player player, String[] args) {
        Set<String> filteredArgs = new HashSet<>();
        boolean compact = false;
        String type = "-l"; // Default type
        Material colorMaterial = Material.GLASS; // Default color (plain glass)
        int number = 1; // Default number

        // Parse arguments
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];

            switch (arg.toLowerCase()) {
                case "-c":
                    if (filteredArgs.contains("compact")) {
                        player.sendMessage("The -c option can only be used once.");
                        return;
                    }
                    compact = true;
                    filteredArgs.add("compact");
                    break;
                case "-l":
                case "-d":
                case "-f":
                    if (filteredArgs.contains("type")) {
                        player.sendMessage("You can only use one of -l, -d, or -f.");
                        return;
                    }

                    type = arg.toLowerCase();
                    // Add all the types to the filtered args
                    filteredArgs.add("type");
                    break;
                default:
                    if (arg.matches("\\d+")) { // Check if argument is a number
                        if (filteredArgs.contains("amount")) {
                            player.sendMessage("You can only specify a number once.");
                            return;
                        }

                        if (Integer.parseInt(arg) <= 0) {
                            player.sendMessage("Has to be more than 0");
                            return;
                        }

                        number = Integer.parseInt(arg);
                        filteredArgs.add("amount");
                    } else if (COLOR_TO_BLOCK_TYPE.containsKey(arg.toLowerCase())) {
                        if (filteredArgs.contains("color")) {
                            player.sendMessage("You can only specify a color once.");
                            return;
                        }

                        colorMaterial = COLOR_TO_BLOCK_TYPE.get(arg.toLowerCase());
                        filteredArgs.add("color");
                    } else {
                        player.sendMessage("Invalid argument: " + arg);
                        return;
                    }
                    break;
            }
        }

        // Mimic functionality here
        addCodeLayersToPlot(type, colorMaterial, number, compact, player);
    }

    private static void addCodeLayersToPlot(String type, Material colorMaterial, int number, boolean compact, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> {

            List<Integer> emptyLayers = PlotUtils.getEmptyLayers(compact).stream().toList();

            if (emptyLayers.isEmpty()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#FF5555>Error: <#AAAAAA>You have hit the maximum number of layers."));
                return;
            }

            org.bukkit.World bukkitWorld = Bukkit.getWorld("dim-code");
            World weWorld = BukkitAdapter.adapt(bukkitWorld);

            EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld);
            editSession.setFastMode(true);
            try {
                int maxLayerCount = Math.min(emptyLayers.size(), number);

                for (int i = 0; i < maxLayerCount; i++) {
                    int y = emptyLayers.get(i);
                    Bukkit.getScheduler().runTask(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> {
                        Bukkit.getWorld("dim-code").getBlockAt(127, y, 0).setType(colorMaterial);
                    });
                }
                for (int i = 0; i < maxLayerCount; i++) {
                    int y = emptyLayers.get(i);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<gray><i>debug info</i>: Adding layer at y=" + y));

                    if (type.equalsIgnoreCase("-l")) {
                        for (int x = 127; x >= 4; x -= 3) {
                            Location pos1 = new Location(bukkitWorld, x, y, 0);
                            Location pos2 = new Location(bukkitWorld, x, y, 511);
                            fillWithBlock(editSession, colorMaterial, pos1, pos2);
                        }
                    } else if (type.equalsIgnoreCase("-d")) {
                        for (int x = 127; x >= 4; x -= 3) {
                            Location pos1 = new Location(bukkitWorld, x, y, 0);
                            Location pos2 = new Location(bukkitWorld, x - 1, y, 511);
                            fillWithBlock(editSession, colorMaterial, pos1, pos2);
                        }
                    } else {
                        Location pos1 = new Location(bukkitWorld, 127, y, 0);
                        Location pos2 = new Location(bukkitWorld, 4, y, 511);
                        fillWithBlock(editSession, colorMaterial, pos1, pos2);
                    }
                }

                player.sendMessage(MiniMessage.miniMessage().deserialize("<#55FF55><b>»</b> <#AAAAAA>Added " + maxLayerCount + " new codespace layers."));
            } finally {
                editSession.flushQueue();
                editSession.close();
            }


            System.out.println("Type: " + type);
            System.out.println("Color Material: " + colorMaterial);
            System.out.println("Number: " + number);
            System.out.println("Compact: " + compact);
        });
    }

    public static List<Integer> getEmptyLayers(boolean compact) {
        List<Integer> emptyLayers = new ArrayList<>();
        int layerSpacing = 5;

        if (!compact) {
            layerSpacing *= 2;
        }

        for (int y = -64; y < 320; y+=layerSpacing) {
            if (Bukkit.getWorld("dim-code").getBlockAt(127, y, 0).getType() == Material.AIR) {
                emptyLayers.add(y);
            }
        }

        return emptyLayers;
    }

    public static List<Integer> getLayersBelow(double yCoord) {
        List<Integer> usedLayers = new ArrayList<>();
        int startY = (int) Math.floor(yCoord/5)*5+1;

        for (int y = startY; y > -64; y-=5) {
            if (Bukkit.getWorld("dim-code").getBlockAt(127, y, 0).getType() != Material.AIR) {
                usedLayers.add(y);
            }
        }
        return usedLayers;
    }

    public static void fillWithBlock(EditSession editSession, Material blockType, Location pos1, Location pos2) {
            // Use WorldEdit to set the remaining blocks
            BlockVector3 min = BlockVector3.at(Math.min(pos1.getBlockX(), pos2.getBlockX()),
                    Math.min(pos1.getBlockY(), pos2.getBlockY()),
                    Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
            BlockVector3 max = BlockVector3.at(Math.max(pos1.getBlockX(), pos2.getBlockX()),
                    Math.max(pos1.getBlockY(), pos2.getBlockY()),
                    Math.max(pos1.getBlockZ(), pos2.getBlockZ()));

            Region region = new CuboidRegion(min, max);
            editSession.setBlocks(region, BukkitAdapter.asBlockType(blockType));
    }

    public static void getPlayerUUID(String username, Consumer<UUID> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> {
            try {
                URL url = new URL("http://192.168.0.125:3000/resolve-uuid?username=" + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JsonObject json = JsonParser.parseString(content.toString()).getAsJsonObject();
                if (json.has("uuid")) {
                    String uuidString = json.get("uuid").getAsString();
                    Bukkit.getScheduler().runTask(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> callback.accept(UUID.fromString(uuidString)));
                } else {
                    // Handle errors here
                    Bukkit.getScheduler().runTask(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> callback.accept(null));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), () -> callback.accept(null));
            }
        });
    }


}
